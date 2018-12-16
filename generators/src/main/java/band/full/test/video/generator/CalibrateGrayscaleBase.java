package band.full.test.video.generator;

import static band.full.core.Quantizer.round;
import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.IntStream.concat;
import static java.util.stream.IntStream.iterate;

import band.full.test.video.encoder.EncoderParameters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Base class for creating different intensity gray patches in the middle of the
 * screen with specified area percentage.
 *
 * @author Igor Malinin
 */
public abstract class CalibrateGrayscaleBase extends CalibratePatchesBase {
    public CalibrateGrayscaleBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder + "/Calibrate", group);
    }

    public CalibrateGrayscaleBase(GeneratorFactory factory,
            EncoderParameters params, NalUnitPostProcessor<Args> processor,
            MuxerFactory muxer, String folder, String group) {
        super(factory, params, processor, muxer, folder + "/Calibrate", group);
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("grayscale")
    public void grayscale(Args args) {
        generate(args);
    }

    public Stream<Args> grayscale() {
        return IntStream.of(0, 5, 10, 20, 50).boxed().flatMap(this::grayscale);
    }

    public Stream<Args> grayscale(int window) {
        var percents = concat(IntStream.of(0, 1, 2, 3, 4, 5, 7),
                iterate(10, i -> i < 100, i -> i + 5));

        // show brightest and darkest patterns in the beginning
        var first = Stream.of(gray(window, "$$", matrix.YMAX));
        var steps = percents.mapToObj(grayArgs(window));
        var white = grayscaleWhite(window);

        return Stream.of(first, steps, white).flatMap(identity());
    }

    /** White and Whiter than White (WtW) for narrow range encodes */
    public Stream<Args> grayscaleWhite(int window) {
        if (matrix.YMAX == matrix.VMAX)
            return Stream.of(gray(window, "X0", matrix.YMAX));

        int yX5 = round(matrix.toLumaCode(1.05));

        return Stream.of(
                gray(window, "X0", matrix.YMAX),
                gray(window, "X5", yX5), // WtW
                gray(window, "X9", matrix.VMAX));
    }

    private IntFunction<Args> grayArgs(int window) {
        return n -> {
            double ye = n / 100.0;
            int y = round(matrix.toLumaCode(ye));
            double percent = matrix.fromLumaCode(y) * 100.0;
            String sequence = format("%02.0f", percent);
            return gray(window, sequence, y);
        };
    }

    public Args gray(int window, String sequence, int y) {
        int c0 = matrix.ACHROMATIC;
        return new Args("Grayscale", sequence, "Grayscale",
                format("%.1f%% White", matrix.fromLumaCode(y) * 100.0),
                window, y, c0, c0);
    }

    @Override
    protected String getBottomCenterText(Args args) {
        return "Code " + args.yuv[0];
    }

    @Override
    protected String getFolder(Args args) {
        String suffix = args.window == 0
                ? format("/Fill/%s", args.file)
                : format("/Win%02d/%s", args.window, args.file);

        return factory.folder + '/' + folder + suffix;
    }

    @Override
    protected String getPattern(Args args) {
        StringBuilder buf = new StringBuilder(args.file);
        if (args.window != 0) {
            buf.append(args.window);
        }

        return buf.append(PG_SEPARATOR).append(group)
                .append('-').append(args.sequence)
                .append(getFormatSuffix(args.yuv[0])).toString();
    }

    protected String getFormatSuffix(int y) {
        String fmt;

        if (bitdepth > 10) {
            fmt = "%04d";
        } else if (y > 1000 && y < 1100) {
            y %= 100; // for 10 bit WtW and full-range patterns
            fmt = "X%02d";
        } else {
            fmt = "%03d";
        }

        return format(fmt, y);
    }
}
