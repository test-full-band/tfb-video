package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Window.square;
import static band.full.testing.video.encoder.EncoderHDR10.MASTER_DISPLAY_PRIMARIES;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.itu.BT2020.BT2020_10bit;
import static band.full.testing.video.itu.BT2020.PRIMARIES;
import static band.full.testing.video.smpte.ST2084.PQ;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.time.Duration.ofMinutes;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.paint.Color.gray;
import static javafx.scene.text.Font.font;

import band.full.testing.video.color.CIEXYZ;
import band.full.testing.video.color.CIExy;
import band.full.testing.video.color.Matrix3x3;
import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.core.Window;
import band.full.testing.video.encoder.EncoderHDR10;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.generate.GenerateVideo;
import band.full.testing.video.generate.GenerateVideoRunner;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

import java.time.Duration;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Calibration patterns for LG OLED TVs.
 *
 * @author Igor Malinin
 * @see <a href=
 *      "http://www.lg.com/us/support/products/documents/UHDA%20Calibration%20Procedure.pdf">
 *      Procedure to Calibrate OLED TVs to Meet UHD Alliance Grayscale Tracking
 *      Requirements</a>
 */
@RunWith(GenerateVideoRunner.class)
@Category(GenerateVideo.class)
@UseParametersRunnerFactory
public class Calibrate2160pHDR10_LGOLED {
    private static final String PATH =
            "HEVC/UHD4K/HDR10/Calibrate/Win10_LGOLED";

    private static final Duration DURATION = ofMinutes(1);
    private static final Matrix3x3 RGB2XYZ = PRIMARIES.getRGBtoXYZ();

    private static final int[] OLED_CODES = {
        127, 254, 320, 386, 419, 451, 467, 482, 498, 513,
        529, 544, 560, 575, 591, 606, 622, 637, 653, 668,
    };

    @Test
    public void win10grayscale() {
        for (int i = 0; i < OLED_CODES.length; i++) {
            win10gray(i + 1, OLED_CODES[i]);
        }
    }

    private void win10gray(int sequence, int yCode) {
        EncoderParameters options = HDR10.withEncoderOptions(
                "--max-cll", "540,54", "--master-display",
                MASTER_DISPLAY_PRIMARIES + "L(5400000,0)");

        EncoderHDR10.encode(getFileName(sequence, yCode), options, e -> {
            Window win = square(e.encoderParameters.resolution, 0.1);

            CanvasYCbCr canvas = e.newCanvas();
            canvas.Y.fill(e.parameters.YMIN);
            canvas.Y.fillRect(win.x, win.y, win.width, win.height, yCode);
            canvas.Cb.fill(e.parameters.ACHROMATIC);
            canvas.Cr.fill(e.parameters.ACHROMATIC);
            canvas.overlay(overlay(yCode));

            e.render(DURATION, () -> canvas);
        });
    }

    private static Parent overlay(int yCode) {
        double ye = BT2020_10bit.fromLumaCode(yCode);

        Label label = new Label(getLabel(yCode));
        label.setFont(font(40));
        label.setTextFill(gray(max(0.25, min(0.5, ye))));

        BorderPane.setMargin(label, new Insets(20));
        BorderPane layout = new BorderPane();
        layout.setBackground(EMPTY);
        layout.setBottom(label);
        return layout;
    }

    private static String getFileName(int sequence, int yCode) {
        return PATH + format("/GrayHDR10LGOLED-%02d-Y%03d", sequence, yCode);
    }

    private static String getLabel(int yCode) {
        double ye = BT2020_10bit.fromLumaCode(yCode);
        double yo = PQ.eotf(ye);
        double[] rgb = {yo, yo, yo};

        CIExy xy = new CIEXYZ(RGB2XYZ.multiply(rgb)).CIExy();

        String text = format("LG OLED HDR10 grayscale CIE(x=%.4f, y=%.4f)"
                + " C%d, %.2f nit", xy.x, xy.y, yCode, yo * 10000.0);

        if (yo > 0.054) {
            text += "; set TV to max of 540 nit!";
        }

        return text;
    }

    public static void main(String[] args) {
        for (int element : OLED_CODES) {
            System.out.println(getLabel(element));
        }
    }
}
