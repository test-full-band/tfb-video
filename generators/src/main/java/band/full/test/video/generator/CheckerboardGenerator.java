package band.full.test.video.generator;

import static band.full.core.Quantizer.round;
import static band.full.video.itu.ColorRange.FULL;
import static java.lang.String.format;
import static java.util.stream.Stream.concat;

import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;
import band.full.test.video.generator.CheckerboardGenerator.Args;

import java.util.stream.Stream;

/**
 * Class for creating full screen checkerboard fills.
 *
 * @author Igor Malinin
 */
public class CheckerboardGenerator extends GeneratorBase<Args> {
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
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder, "Checkerboard", group);
    }

    public Stream<Args> args() {
        var args = Stream.of(
                new Args("$NR", matrix.YMIN, matrix.YMAX),
                new Args("1090",
                        round(matrix.toLumaCode(0.1)),
                        round(matrix.toLumaCode(0.9))),
                new Args("2575",
                        round(matrix.toLumaCode(0.25)),
                        round(matrix.toLumaCode(0.75))),
                new Args("3070",
                        round(matrix.toLumaCode(0.3)),
                        round(matrix.toLumaCode(0.7))),
                new Args("4060",
                        round(matrix.toLumaCode(0.4)),
                        round(matrix.toLumaCode(0.6))),
                new Args("4555",
                        round(matrix.toLumaCode(0.45)),
                        round(matrix.toLumaCode(0.55))));

        return (matrix.range == FULL) ? args
                : concat(args, Stream.of(
                        new Args("$VR", matrix.VMIN, matrix.VMAX)));
    }

    @Override
    protected String getPattern(Args args) {
        return super.getPattern(args) + '-' + args.suffix;
    }

    @Override
    protected void encode(EncoderY4M e, Args args, String phase) {
        var fb = e.newFrameBuffer();

        fb.Y.calculate(
                (x, y) -> (x + y) % 2 == 0 ? args.yMin : args.yMax);

        e.render(gop, () -> fb);
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        d.read(fb -> {});
    }
}
