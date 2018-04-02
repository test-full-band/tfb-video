package band.full.test.video.generator;

import static band.full.core.Quantizer.round;
import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.stream.IntStream.concat;
import static java.util.stream.IntStream.iterate;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.concat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.video.encoder.EncoderParameters;

import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.util.function.IntFunction;
import java.util.stream.Stream;

/**
 * Base class for creating different intensity gray patches in the middle of the
 * screen with specified area percentage.
 *
 * @author Igor Malinin
 */
@TestInstance(PER_CLASS)
public class GrayscalePatchesGenerator extends PatchesGenerator {
    protected static final Duration DURATION_INTRO = ofSeconds(5);
    protected static final Duration DURATION = ofSeconds(25);

    public GrayscalePatchesGenerator(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, pattern);
    }

    public Stream<Args> grayscale(int window) {
        var percents = concat(range(0, 5),
                iterate(5, i -> i < 100, i -> i + 5));

        // show brightest and darkest patterns in the beginning
        var first = Stream.of(gray(window, "$$", matrix.YMAX));
        var steps = percents.mapToObj(grayArgs(window));
        var white = grayscaleWhite(window);
        return concat(concat(first, steps), white);
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
    protected String getFileName(Args args) {
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

        if (args.window == 0) return factory.folder + '/' + folder +
                format("/Fill/%s/%s-%s-%s-" + fmt,
                        args.file, args.file, pattern, args.sequence, y);

        return factory.folder + '/' + folder +
                format("/Win%02d/%s/%s%d-%s-%s-" + fmt,
                        args.window, args.file, args.file, args.window,
                        pattern, args.sequence, y);
    }

    public static void main(String[] args) {
        System.out.println(String.format("X%02d", 1080));
    }
}
