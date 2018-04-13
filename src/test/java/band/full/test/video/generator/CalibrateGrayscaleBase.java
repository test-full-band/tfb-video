package band.full.test.video.generator;

import static band.full.core.Quantizer.round;
import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.IntStream.concat;
import static java.util.stream.IntStream.iterate;

import band.full.video.encoder.EncoderParameters;

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
public class CalibrateGrayscaleBase extends CalibratePatchesBase {
    public CalibrateGrayscaleBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder + "/Calibrate", pattern);
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
        if (args.window == 0)
            return factory.folder + '/' + folder +
                    format("/Fill/%s", args.file);

        return factory.folder + '/' + folder +
                format("/Win%02d/%s", args.window, args.file);
    }

    @Override
    protected String getPattern(Args args) {
        int y = args.yuv[0];
        String fmt;

        if (matrix.bitdepth > 10) {
            fmt = "%04d";
        } else if (y > 1000 && y < 1100) {
            y %= 100; // for 10 bit WtW and full-range patterns
            fmt = "X%02d";
        } else {
            fmt = "%03d";
        }

        if (args.window == 0)
            return format("%s-%s-%s-" + fmt,
                    args.file, pattern, args.sequence, y);

        return format("%s%d-%s-%s-" + fmt,
                args.file, args.window,
                pattern, args.sequence, y);
    }
}
