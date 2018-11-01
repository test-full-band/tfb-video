package band.full.test.video.generator;

import static band.full.core.ArrayMath.multiply;
import static band.full.core.Quantizer.round;
import static band.full.core.Resolution.STD_1080p;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.text.Font.font;

import band.full.core.color.Matrix3x3;
import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;
import band.full.test.video.executor.FrameVerifier;
import band.full.test.video.executor.FxImage;
import band.full.video.buffer.FrameBuffer;
import band.full.video.buffer.Plane;
import band.full.video.itu.BT2020;
import band.full.video.itu.BT709;
import band.full.video.smpte.ST2084;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Colour bar test pattern for high dynamic range television systems.
 *
 * @author Igor Malinin
 * @see <a href=
 *      "https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.2111-0-201712-I!!PDF-E.pdf">
 *      Rec. ITU-R BT.2111-0 (12/2017)</a>
 */
// TODO output file type and test.full.band branding
public class BT2111Generator extends GeneratorBase<Void> {
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
    private final int b2; // 540
    private final int b12; // 90

    public BT2111Generator(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder, "BT2111", group);

        if (width % STD_1080p.width != 0)
            throw new IllegalArgumentException(
                    "Unsupported resolution: " + resolution);

        switch (transfer.code()) {
            case 2: // DV PQ
            case 16: // PQ
                alpha = 0.58;
                break;

            case 18: // HLG
                alpha = 0.75;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported transfer function: " + transfer);
        }

        bt709conv = BT709.PRIMARIES.RGBtoXYZ.multiply(matrix.XYZtoRGB)
                .multiply(matrix.transfer.toLinear(alpha));

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

        b2 = height / 2;
        b12 = b2 / 6;
    }

    @Override
    protected void encode(EncoderY4M e, Void args, String phase) {
        var fb = draw(e.newFrameBuffer());
        FxImage.overlay(overlay(params), fb);
        e.render(gop, () -> fb);
    }

    @Override
    protected void verify(DecoderY4M d, Void args) {
        var expected = draw(d.newFrameBuffer());
        d.read(fb -> {
            // cover overlay before verification
            int gray40 = round(matrix.toLumaCode(0.4));
            fb.Y.fillRect(0, 0, wc, b12 + b12, gray40);
            fb.Y.fillRect(width - wc, 0, wc, b12 + b12, gray40);

            FrameVerifier.verify(expected, fb, 4, 0.002);
        });
    }

    private FrameBuffer draw(FrameBuffer fb) {
        fillBigBars(fb);
        fillStair(fb);
        fillRamp(fb);
        fillBottomBars(fb);
        return fb;
    }

    private void fillBigBars(FrameBuffer fb) {
        int x = wc;

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

        multiply(rgb, rgb, transfer.toLinear(alpha));

        int[] dim = round(
                matrix.toCodes(matrix.fromLinearRGB(rgb, buf), buf));

        fb.fillRect(x, 0, w, y, bright);
        fb.fillRect(x, y, w, h, dim);
        return x + w;
    }

    private void fillStair(FrameBuffer fb) {
        int y = b2 + b12, x = wc;

        fb.Y.fillRect(0, y, width, b12, round(matrix.toLumaCode(alpha)));

        x = fillRect(fb.Y, x, y, wd, b12, matrix.VMIN);
        for (int i = 0; i <= 10; i++) {
            int w = (i < 4 || i > 5 ? wd : we) / 2;
            x = fillRect(fb.Y, x, y, w, b12, i / 10.0);
        }
        x = fillRect(fb.Y, x, y, wd / 2, b12, matrix.VMAX);
    }

    private void fillRamp(FrameBuffer fb) {
        int y = b2 + 2 * b12;

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

    protected Parent overlay(EncoderParameters params) {
        int b24 = b12 / 2, x = width - wc;
        Color fill = Color.BLACK;

        Pane grid = new Pane(
                text(0, 0, wc, b12, fill, font(height / 15), transferLabel()),
                text(0, b12, wc, b24, fill, font(height / 40), rangeLabel()),
                text(x, 0, wc, b12, fill, font(height / 18), "BT.2111"),
                text(x, b12, wc, b24, fill, font(height / 40),
                        "test.full.band"));

        grid.setBackground(EMPTY);
        return grid;
    }

    private String transferLabel() {
        if (bitdepth == 10 && primaries == BT2020.PRIMARIES) {
            if (transfer instanceof ST2084) return "HDR10";
        }

        String label = transfer.toString();
        if (bitdepth > 8) {
            label += bitdepth;
        }

        return label;
    }

    private String rangeLabel() {
        return matrix.range.name() + " RANGE";
    }
}
