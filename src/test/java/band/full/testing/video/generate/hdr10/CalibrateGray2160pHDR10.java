package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Quantizer.round;
import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.itu.BT2020.BT2020_10bit;
import static band.full.testing.video.itu.BT709.BT709;
import static band.full.testing.video.smpte.ST2084.PQ;
import static java.time.Duration.ofSeconds;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.paint.Color.gray;
import static javafx.scene.text.Font.font;

import band.full.testing.video.color.CIEXYZ;
import band.full.testing.video.color.CIExy;
import band.full.testing.video.color.CIExyY;
import band.full.testing.video.color.Matrix3x3;
import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.encoder.EncoderHDR10;
import band.full.testing.video.generate.FxDisplay;
import band.full.testing.video.generate.GenerateVideo;
import band.full.testing.video.generate.GenerateVideoRunner;
import band.full.testing.video.itu.BT2020;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Calibration box fills.
 *
 * @author Igor Malinin
 */
@RunWith(GenerateVideoRunner.class)
@Category(GenerateVideo.class)
@UseParametersRunnerFactory
public class CalibrateGray2160pHDR10 {
    private final int WIDTH = 910;
    private final int HEIGHT = 910;

    private final int OFFSET_X = (STD_2160p.width - WIDTH) >> 1;
    private final int OFFSET_Y = (STD_2160p.height - HEIGHT) >> 1;

    private static final Matrix3x3 RGB2XYZ = BT2020.PRIMARIES.getRGBtoXYZ();

    @Test
    public void gray10() {
        for (int i = 0; i <= 20; i++) {
            gray10(round(BT2020_10bit.toLumaCode(i / 20.0)));
        }
    }

    private void gray10(int yCode) {
        EncoderHDR10.encode("HDR10/10pcGray" + yCode, e -> {
            CanvasYCbCr canvas = e.newCanvas();
            canvas.Y.fill(e.parameters.YMIN);
            canvas.Y.fillRect(OFFSET_X, OFFSET_Y, WIDTH, HEIGHT, yCode);
            canvas.Cb.fill(e.parameters.ACHROMATIC);
            canvas.Cr.fill(e.parameters.ACHROMATIC);
            canvas.overlay(overlay(yCode));
            e.render(ofSeconds(30), () -> canvas);
        });
    }

    private static Parent overlay(int yCode) {
        double ye = BT2020_10bit.fromLumaCode(yCode);
        double yo = PQ.eotf(ye);

        double[] rgb = {yo, yo, yo};
        CIEXYZ xyz = new CIEXYZ(RGB2XYZ.multiply(rgb));
        CIExy xy = xyz.CIExy();

        String text = String.format("%.1f%% Gray,"
                + " Y: C%d (%.1fnit, x=%.5f, y=%.5f)",
                ye * 100.0, yCode, yo * 10000.0, xy.x, xy.y);

        Label label = new Label(text);
        label.setFont(font(40));
        label.setTextFill(gray(0.2));

        BorderPane.setMargin(label, new Insets(20));
        BorderPane layout = new BorderPane();
        layout.setBackground(EMPTY);
        layout.setBottom(label);
        return layout;
    }

    public static void main(String[] args) {
        FxDisplay.show(HDR10.resolution, () -> overlay(256));
    }

    // TODO Calman codes for red
    private static final double[][] RxyY = {
        {0.1, 0.3529, 0.3252, 75.1454},
        {0.2, 0.3911, 0.3217, 62.6958},
        {0.3, 0.4318, 0.3179, 53.1167},
        {0.4, 0.4735, 0.3140, 45.7786},
        {0.5, 0.5077, 0.3107, 41.0151},
        {0.6, 0.5468, 0.3071, 36.5778},
        {0.7, 0.5877, 0.3033, 32.7705},
        {0.8, 0.6308, 0.2992, 29.4604},
        {0.9, 0.6678, 0.2958, 27.0506},
        {1.0, 0.7080, 0.2920, 24.7943}
    };

    @Test
    @Ignore("Investigate chromacity percentage")
    public void red() throws Exception {
        Matrix3x3 XYZ2RGB = BT2020.PRIMARIES.getXYZtoRGB();

        for (double[] dxyY : RxyY) {
            CIExyY xyY = new CIExyY(dxyY[1], dxyY[2], dxyY[3] / 100.0);
            double[] rgb = XYZ2RGB.multiply(xyY.CIEXYZ().array());

            // Linear RGB
            System.out.print(String.format("R%.4f G%6.4f B%.4f | ",
                    rgb[0], rgb[1], rgb[2]));

            // TODO: PQ
            double r = BT709.oetf(rgb[0]);
            double g = BT709.oetf(rgb[1]);
            double b = BT709.oetf(rgb[2]);

            // RGB Codes (video std)
            System.out.print(String.format("R%05.1f G%05.1f B%05.1f | ",
                    BT709.toLumaCode(r),
                    BT709.toLumaCode(g),
                    BT709.toLumaCode(b)));

            double y = BT709.getY(rgb[0], rgb[1], rgb[2]);
            double cb = BT709.getCb(y, rgb[2]);
            double cr = BT709.getCb(y, rgb[1]);

            // Electrical YCbCr (video std)
            System.out.print(String.format("Y%.4f Cb%.4f Cr%.4f | ",
                    y, cb, cr));

            // YCbCr Codes (video std)
            System.out.print(String.format("Y%05.1f Cb%05.1f Cr%05.1f | ",
                    BT709.toLumaCode(y),
                    BT709.toChromaCode(cb),
                    BT709.toChromaCode(cr)));

            CIEXYZ xyz = new CIEXYZ(RGB2XYZ.multiply(rgb));
            System.out.println(xyz.CIExyY());
        }
    }
}
