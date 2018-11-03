package band.full.test.video.generator;

import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;
import band.full.test.video.generator.LinesGenerator.Args;

import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class for creating full screen horizontal and vertical lines.
 *
 * @author Igor Malinin
 */
public class LinesGenerator extends GeneratorBase<Args> {
    public static class Args {
        public final String suffix;
        public final boolean vertical;
        public final int width;

        public Args(boolean vertical, int width) {
            suffix = (vertical ? "V" : "H") + width;
            this.vertical = vertical;
            this.width = width;
        }

        @Override
        public String toString() {
            return suffix;
        }
    }

    public LinesGenerator(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder, "Lines", group);
    }

    public Stream<Args> args() {
        return IntStream.of(1, 2, 3, 4).boxed().flatMap(
                w -> Stream.of(new Args(false, w), new Args(true, w)));
    }

    @Override
    protected String getPattern(Args args) {
        return super.getPattern(args) + '-' + args.suffix;
    }

    @Override
    protected void encode(EncoderY4M e, Args args, String phase) {
        var fb = e.newFrameBuffer();

        fb.Y.calculate(calculator(args));

        e.render(gop, () -> fb);
    }

    private IntBinaryOperator calculator(Args args) {
        var op = calculator(args.width);

        return args.vertical
                ? (x, y) -> op.applyAsInt(x)
                : (x, y) -> op.applyAsInt(y);
    }

    private IntUnaryOperator calculator(int w) {
        int w2 = w / 2;
        return x -> ((x + w2) / w) % 2 == 0 ? matrix.YMIN : matrix.YMAX;
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        d.read(fb -> {});
    }
}
