package band.full.testing.video.generate.base;

import static java.util.stream.IntStream.rangeClosed;

import band.full.testing.video.core.FrameBuffer;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.generate.GeneratorFactory;
import band.full.testing.video.generate.ParametrizedGeneratorBase;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Testing color bands separation / dynamic quantization step uniformity.
 *
 * @author Igor Malinin
 */
public class Quants3DBase extends ParametrizedGeneratorBase<Quants3DBase.Args> {
    public static class Args {
        public final String speed;
        public final int frames;
        public final int lsb;

        public Args(String speed, int frames, int lsb) {
            this.speed = speed;
            this.frames = frames;
            this.lsb = lsb;
        }
    }

    protected static Stream<Args> params() {
        return IntStream.of(1, 2, 4, 8, 16).boxed().flatMap(lsb -> Stream.of(
                new Args("Fast", 1, lsb),
                new Args("Norm", 2, lsb),
                new Args("Slow", 4, lsb),
                new Args("Xtra", 6, lsb)));
    }

    protected Quants3DBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, pattern);
    }

    @Override
    protected String getFileName(Args args) {
        return factory.name() + '/' + folder + '/' +
                pattern + '-' + args.speed + args.lsb;
    }

    @Override
    protected void encode(EncoderY4M e, Args args) {
        int uCode = matrix.ACHROMATIC;
        int vCode = matrix.ACHROMATIC;

        FrameBuffer fb = e.newFrameBuffer();
        // e.parameters.framerate.toFrames(ofMillis(145));

        rangeClosed(matrix.YMIN, matrix.YMAX).forEach(yCode -> {
            draw(fb, yCode, uCode, vCode, args.lsb);
            e.render(args.frames, () -> fb);
        });
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        int uCode = matrix.ACHROMATIC;
        int vCode = matrix.ACHROMATIC;

        FrameBuffer expected = d.newFrameBuffer();
        // d.parameters.framerate.toFrames(ofMillis(170));

        rangeClosed(matrix.YMIN, matrix.YMAX).forEach(yCode -> {
            draw(expected, yCode, uCode, vCode, args.lsb);
            d.read(args.frames, fb -> fb.verify(expected, 1, 0.00001));
        });
    }

    private FrameBuffer draw(FrameBuffer fb,
            int yCode, int uCode, int vCode, int lsb) {
        int yMid = height >> 1;

        // single square box side truncated to an odd number
        int box = yMid / 9 << 1;
        int half = box / 2; // half of the square box size

        int xMid = width >> 1;

        // left/right space to a side four box pattern
        int margin = (xMid >> 1) - half * 5;

        int x1 = margin, x2 = xMid - half * 3, x3 = width - margin - box * 3;
        int y1 = box, y2 = yMid - half, y3 = height - box * 4;

        fb.fill(yCode, uCode, vCode);

        // top four box row

        fill(fb, x1, y1, box, yCode, uCode, vCode,
                -lsb, lsb, lsb, -lsb, -lsb, -lsb);

        fill(fb, x2, y1, box, yCode, uCode, vCode,
                0, lsb, 0, 0, 0, lsb);

        fill(fb, x3, y1, box, yCode, uCode, vCode,
                lsb, lsb, -lsb, -lsb, lsb, -lsb);

        // bottom four box row

        fill(fb, x1, y3, box, yCode, uCode, vCode,
                -lsb, lsb, 0, lsb, lsb, 0);

        fill(fb, x2, y3, box, yCode, uCode, vCode,
                0, -lsb, lsb, 0, -lsb, -lsb);

        fill(fb, x3, y3, box, yCode, uCode, vCode,
                lsb, 0, -lsb, lsb, 0, lsb);

        // mid left/right boxes
        int m1 = (x1 + x2) / 2 + box, m2 = (x2 + x3) / 2 + box;

        fb.fillRect(m1, y2, box, box, yCode + lsb, uCode, vCode);
        fb.fillRect(m2, y2, box, box, yCode - lsb, uCode, vCode);

        return fb;
    }

    private void fill(FrameBuffer fb, int x, int y, int box,
            int yCode, int uCode, int vCode,
            int yTop, int uTop, int vTop,
            int yLeft, int uLeft, int vLeft) {
        int box2 = box * 2;

        fb.fillRect(x + box, y, box, box, // top
                yCode + yTop, uCode + uTop, vCode + vTop);

        fb.fillRect(x + box, y + box2, box, box, // bottom
                yCode - yTop, uCode - uTop, vCode - vTop);

        fb.fillRect(x, y + box, box, box, // left
                yCode + yLeft, uCode + uLeft, vCode + vLeft);

        fb.fillRect(x + box2, y + box, box, box, // right
                yCode - yLeft, uCode - uLeft, vCode - vLeft);
    }
}
