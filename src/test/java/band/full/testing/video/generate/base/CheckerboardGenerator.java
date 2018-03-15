package band.full.testing.video.generate.base;

import static band.full.testing.video.core.Quantizer.round;
import static band.full.testing.video.itu.ColorRange.FULL;
import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.stream.Stream.concat;

import band.full.testing.video.core.FrameBuffer;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.generate.GeneratorFactory;
import band.full.testing.video.generate.ParametrizedGeneratorBase;
import band.full.testing.video.itu.ColorMatrix;

import java.time.Duration;
import java.util.stream.Stream;

/**
 * Class for creating full screen checkerboard fills.
 *
 * @author Igor Malinin
 */
public class CheckerboardGenerator
        extends ParametrizedGeneratorBase<CheckerboardGenerator.Args> {
    protected static final Duration DURATION = ofSeconds(10);

    public static class Args {
        public final String suffix;
        public final int yMin, yMax;

        public Args(String suffix, int yMin, int yMax) {
            this.suffix = suffix;
            this.yMin = yMin;
            this.yMax = yMax;
        }

        @Override
        public String toString() {
            return format("%s %03d-%03d",
                    suffix.length() == 0 ? "Nominal" : suffix,
                    yMin, yMax);
        }
    }

    public CheckerboardGenerator(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, "Checkerboard-" + pattern);
    }

    public Stream<Args> args() {
        Stream<Args> args = Stream.of(
                new Args("$NR", matrix.YMIN, matrix.YMAX),
                new Args("1090",
                        round(matrix.toLumaCode(0.1)),
                        round(matrix.toLumaCode(0.9))),
                new Args("2080",
                        round(matrix.toLumaCode(0.2)),
                        round(matrix.toLumaCode(0.8))),
                new Args("2575",
                        round(matrix.toLumaCode(0.25)),
                        round(matrix.toLumaCode(0.75))),
                new Args("3070",
                        round(matrix.toLumaCode(0.3)),
                        round(matrix.toLumaCode(0.7))));

        return (matrix.range == FULL) ? args
                : concat(args, Stream.of(
                        new Args("$VR", matrix.VMIN, matrix.VMAX)));
    }

    @Override
    protected String getFileName(Args args) {
        return factory.folder + '/' + folder + '/'
                + pattern + '-' + args.suffix;
    }

    @Override
    protected void encode(EncoderY4M e, Args args) {
        FrameBuffer fb = e.newFrameBuffer();
        ColorMatrix matrix = fb.matrix;

        fb.Y.calculate(
                (x, y) -> (x + y) % 2 == 0 ? matrix.YMIN : matrix.YMAX);

        e.render(DURATION, () -> fb);
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        d.read(fb -> {});
    }
}
