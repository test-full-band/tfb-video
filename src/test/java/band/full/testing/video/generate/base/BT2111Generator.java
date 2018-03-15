package band.full.testing.video.generate.base;

import static band.full.testing.video.core.Quantizer.round;
import static band.full.testing.video.core.Resolution.STD_1080p;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.time.Duration.ofSeconds;

import band.full.testing.video.color.Matrix3x3;
import band.full.testing.video.core.FrameBuffer;
import band.full.testing.video.core.Plane;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.generate.GeneratorBase;
import band.full.testing.video.generate.GeneratorFactory;
import band.full.testing.video.itu.BT709;

import java.time.Duration;

/**
 * @author Igor Malinin
 */
public class BT2111Generator extends GeneratorBase {
    protected static final Duration DURATION = ofSeconds(10);

    private final double alpha;
    private final Matrix3x3 bt709conv;

    private final int scale;

    // bar widths in comments are based on 1920x1080 resolution
    private final int wc; // 240
    private final int wd; // 206
    private final int we; // 204
    private final int wf; // 136
    private final int wg; // 70
    private final int wh; // 68
    private final int wi; // 238
    private final int wj; // 438
    private final int wk; // 282

    public BT2111Generator(GeneratorFactory factory,
            EncoderParameters params, String folder, String suffix) {
        super(factory, params, folder, "BT2111" + suffix);

        if (width % STD_1080p.width != 0)
            throw new IllegalArgumentException(
                    "Unsupported resolution: " + resolution);

        switch (matrix.transfer.code()) {
            case 16: // PQ
                alpha = 0.58;
                break;

            case 18: // HLG
                alpha = 0.75;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported transfer function: " + matrix.transfer);
        }

        bt709conv = BT709.PRIMARIES.RGBtoXYZ.multiply(matrix.XYZtoRGB)
                .multiply(matrix.transfer.eotf(alpha));

        scale = width / STD_1080p.width;

        wc = width / 8;
        wd = 206 * scale;
        we = width - 2 * wc - 6 * wd;
        wg = 70 * scale;
        wf = wd - wg;
        wh = wf / 2;
        wi = wd - wg + we / 2;
        wj = 438 * scale;
        wk = width - 2 * wc - wf - 3 * wg - 2 * wh - wi - wj;
    }

    @Override
    protected void encode(EncoderY4M e) {
        FrameBuffer fb = e.newFrameBuffer();

        fillBigBars(fb);
        fillStair(fb);
        fillRamp(fb);
        fillBottomBars(fb);

        e.render(DURATION, () -> fb);
    }

    @Override
    protected void verify(DecoderY4M d) {
        d.read(fb -> {}); // TODO
    }

    private void fillBigBars(FrameBuffer fb) {
        int b2 = height / 2, b12 = b2 / 6, x = wc;

        fb.Y.fillRect(0, 0, width, b12 + b2, round(matrix.toLumaCode(0.4)));

        x = fillBigBar(fb, x, b12, wd, b2, 1.0, 1.0, 1.0); // W
        x = fillBigBar(fb, x, b12, wd, b2, 1.0, 1.0, 0.0); // Y
        x = fillBigBar(fb, x, b12, wd, b2, 0.0, 1.0, 1.0); // C
        x = fillBigBar(fb, x, b12, we, b2, 0.0, 1.0, 0.0); // G
        x = fillBigBar(fb, x, b12, wd, b2, 1.0, 0.0, 1.0); // M
        x = fillBigBar(fb, x, b12, wd, b2, 1.0, 0.0, 0.0); // R
        x = fillBigBar(fb, x, b12, wd, b2, 0.0, 0.0, 1.0); // B
    }

    private int fillBigBar(FrameBuffer fb, int x, int y, int w, int h,
            double r, double g, double b) {
        double[] rgb = {r, g, b}, buf = new double[3];

        int[] bright = round(
                matrix.toCodes(matrix.fromLinearRGB(rgb, buf), buf));

        multiply(rgb, matrix.transfer.eotf(alpha));

        int[] dim = round(
                matrix.toCodes(matrix.fromLinearRGB(rgb, buf), buf));

        fb.fillRect(x, 0, w, y, bright);
        fb.fillRect(x, y, w, h, dim);
        return x + w;
    }

    private void fillStair(FrameBuffer fb) {
        int b2 = height / 2, b12 = b2 / 6, y = b2 + b12, x = wc;

        fb.Y.fillRect(0, y, width, b12, round(matrix.toLumaCode(alpha)));

        x = fillRect(fb.Y, x, y, wd, b12, matrix.VMIN);
        for (int i = 0; i <= 10; i++) {
            int w = (i < 4 || i > 5 ? wd : we) / 2;
            x = fillRect(fb.Y, x, y, w, b12, i / 10.0);
        }
        x = fillRect(fb.Y, x, y, wd / 2, b12, matrix.VMAX);
    }

    private void fillRamp(FrameBuffer fb) {
        int b2 = height / 2, b12 = b2 / 6, y = b2 + 2 * b12;

        for (int x = wc; x < width; x++) {
            // TODO 12 bit support and diagonal sub-pixel shift below 8K
            // TODO half toning for 10 bit above 2K
            int value = max(matrix.VMIN, min(matrix.VMAX,
                    matrix.VMAX + x / scale + 206 - width / scale));
            fb.Y.fillRect(x, y, 1, b12, value);
        }
    }

    private void fillBottomBars(FrameBuffer fb) {
        int b4 = height / 4, y = height - b4, c3 = wc / 3, x = 0;
        int btb2 = max(matrix.VMIN, round(matrix.toLumaCode(-0.02)));

        x = fill709(fb, x, y, c3, b4, 1.0, 1.0, 0.0); // Y
        x = fill709(fb, x, y, c3, b4, 0.0, 1.0, 1.0); // C
        x = fill709(fb, x, y, c3, b4, 0.0, 1.0, 0.0); // G
        x += wf; // black
        x = fillRect(fb.Y, x, y, wg, b4, btb2); // PLUGE -2
        x += wh; // black
        x = fillRect(fb.Y, x, y, wg, b4, 0.02); // PLUGE +2
        x += wh; // black
        x = fillRect(fb.Y, x, y, wg, b4, 0.04); // PLUGE +4
        x += wi; // black
        x = fillRect(fb.Y, x, y, wj, b4, alpha); // W
        x += wk; // black
        x = fill709(fb, x, y, c3, b4, 1.0, 0.0, 1.0); // M
        x = fill709(fb, x, y, c3, b4, 1.0, 0.0, 0.0); // R
        x = fill709(fb, x, y, c3, b4, 0.0, 0.0, 1.0); // B
    }

    private int fillRect(Plane luma, int x, int y, int w, int h, double value) {
        return fillRect(luma, x, y, w, h, round(matrix.toLumaCode(value)));
    }

    private int fillRect(Plane luma, int x, int y, int w, int h, int code) {
        luma.fillRect(x, y, w, h, code);
        return x + w;
    }

    private int fill709(FrameBuffer fb, int x, int y, int w, int h,
            double r, double g, double b) {
        double[] buf = {r, g, b};
        bt709conv.multiply(buf, buf);
        matrix.toCodes(matrix.fromLinearRGB(buf, buf), buf);
        fb.fillRect(x, y, w, h, round(buf));
        return x + w;
    }

    private static void multiply(double[] buf, double mult) {
        for (int i = 0; i < buf.length; i++) {
            buf[i] *= mult;
        }
    }
}
