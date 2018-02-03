package band.full.testing.video.generate;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;

import band.full.testing.video.core.CanvasYUV;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.itu.BT709;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Testing color bands separation / dynamic quantization step uniformity.
 *
 * @author Igor Malinin
 */
public abstract class Quants3DBase
        extends ParametrizedGeneratorBase<Quants3DBase.Args> {
    public class Args {
        public final String speed;
        public final int frames;
        public final int lsb;

        public Args(String speed, int frames, int lsb) {
            this.speed = speed;
            this.frames = frames;
            this.lsb = lsb;
        }
    }

    protected List<Args> params() {
        return IntStream.of(1, 2, 4, 8, 16).boxed()
                .flatMap(lsb -> List.of(
                        new Args("Fast", 1, lsb),
                        new Args("Norm", 2, lsb),
                        new Args("Slow", 4, lsb),
                        new Args("Xtra", 6, lsb)).stream())
                .collect(toList());
    }

    @Override
    protected void encode(EncoderY4M e, Args args) {
        int uCode = e.matrix.ACHROMATIC;
        int vCode = e.matrix.ACHROMATIC;

        CanvasYUV c = e.newCanvas();
        // e.parameters.framerate.toFrames(ofMillis(145));

        rangeClosed(e.matrix.YMIN, e.matrix.YMAX).forEach(yCode -> {
            draw(c, yCode, uCode, vCode, args.lsb);
            e.render(args.frames, () -> c);
        });
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        int uCode = d.matrix.ACHROMATIC;
        int vCode = d.matrix.ACHROMATIC;

        CanvasYUV expected = d.newCanvas();
        // d.parameters.framerate.toFrames(ofMillis(170));

        rangeClosed(d.matrix.YMIN, d.matrix.YMAX).forEach(yCode -> {
            draw(expected, yCode, uCode, vCode, args.lsb);
            d.read(args.frames, c -> c.verify(expected, 1, 0.00001));
        });
    }

    private CanvasYUV draw(CanvasYUV c,
            int yCode, int uCode, int vCode, int lsb) {
        int height = c.Y.height;
        int yMid = height >> 1;

        // single square box side truncated to an odd number
        int box = yMid / 9 << 1;
        int half = box / 2; // half of the square box size

        int width = c.Y.width;
        int xMid = width >> 1;

        // left/right space to a side four box pattern
        int margin = (xMid >> 1) - half * 5;

        int x1 = margin, x2 = xMid - half * 3, x3 = width - margin - box * 3;
        int y1 = box, y2 = yMid - half, y3 = height - box * 4;

        c.fill(yCode, uCode, vCode);

        // top four box row

        fill(c, x1, y1, box, yCode, uCode, vCode,
                -lsb, lsb, lsb, -lsb, -lsb, -lsb);

        fill(c, x2, y1, box, yCode, uCode, vCode,
                0, lsb, 0, 0, 0, lsb);

        fill(c, x3, y1, box, yCode, uCode, vCode,
                lsb, lsb, -lsb, -lsb, lsb, -lsb);

        // bottom four box row

        fill(c, x1, y3, box, yCode, uCode, vCode,
                -lsb, lsb, 0, lsb, lsb, 0);

        fill(c, x2, y3, box, yCode, uCode, vCode,
                0, -lsb, lsb, 0, -lsb, -lsb);

        fill(c, x3, y3, box, yCode, uCode, vCode,
                lsb, 0, -lsb, lsb, 0, lsb);

        // mid left/right boxes
        int m1 = (x1 + x2) / 2 + box, m2 = (x2 + x3) / 2 + box;

        c.fillRect(m1, y2, box, box, yCode + lsb, uCode, vCode);
        c.fillRect(m2, y2, box, box, yCode - lsb, uCode, vCode);

        return c;
    }

    private void fill(CanvasYUV c, int x, int y, int box,
            int yCode, int uCode, int vCode,
            int yTop, int uTop, int vTop,
            int yLeft, int uLeft, int vLeft) {
        int box2 = box * 2;

        c.fillRect(x + box, y, box, box, // top
                yCode + yTop, uCode + uTop, vCode + vTop);

        c.fillRect(x + box, y + box2, box, box, // bottom
                yCode - yTop, uCode - uTop, vCode - vTop);

        c.fillRect(x, y + box, box, box, // left
                yCode + yLeft, uCode + uLeft, vCode + vLeft);

        c.fillRect(x + box2, y + box, box, box, // right
                yCode - yLeft, uCode - uLeft, vCode - vLeft);
    }

    public static void main(String[] args) {
        double[] xxx = {0, 0.5, 0};
        BT709.BT709_8bit.fromRGB(xxx, xxx);
        System.out.println(Arrays.toString(xxx));
    }
}
