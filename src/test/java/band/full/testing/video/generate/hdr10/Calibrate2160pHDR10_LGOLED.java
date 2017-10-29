package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Window.square;
import static band.full.testing.video.encoder.EncoderHDR10.MASTER_DISPLAY_PRIMARIES;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.executor.GenerateVideo.Type.LOSSLESS;
import static band.full.testing.video.itu.BT2020.BT2020_10bit;
import static band.full.testing.video.itu.BT2020.PRIMARIES;
import static band.full.testing.video.smpte.ST2084.PQ;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.lang.System.out;
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
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.itu.YCbCr;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Calibration patterns for LG OLED TVs.
 *
 * @author Igor Malinin
 */
@GenerateVideo(LOSSLESS)
public class Calibrate2160pHDR10_LGOLED {
    private static final String PATH =
            "HEVC/UHD4K/HDR10/Calibrate/Win10_LGOLED";

    private static final Duration DURATION = ofMinutes(1);
    private static final Matrix3x3 RGB2XYZ = PRIMARIES.getRGBtoXYZ();

    private static final int[] OLED6_CODES = {
        127, 254, 320, 386, 419, 451, 467, 482, 498, 513,
        529, 544, 560, 575, 591, 606, 622, 637, 653, 668,
    };

    private static final int[] OLED7_MAXCLL540_CODES = {
        269, 332, 396, 422, 451, 464, 475, 490, 504, 516,
        530, 544, 556, 570, 584, 597, 609, 623, 636, // 401.45 nit
    };

    private static final int[] OLED7_MAXCLL1000_CODES = {
        301, 359, 416, 445, 475, 490, 503, 517, 529, 544,
        555, 568, 581, 592, 605, 618, 631, 644, 657, // 501.65 nit
    };

    private static final int[] OLED7_MAXCLL4000_CODES = {
        320, 382, 443, 475, 502, 516, 530, 545, 557, 572,
        584, 599, 612, 627, 640, 654, 666, 678, 705, // 831.56 nit
    };

    /**
     * 2016 TVs
     *
     * @see <a href=
     *      "http://www.lg.com/us/support/products/documents/UHDA%20Calibration%20Procedure.pdf">
     *      Procedure to Calibrate 2016 OLED TVs to Meet UHD Alliance Grayscale
     *      Tracking Requirements</a>
     */
    @Test
    public void oled6win10grayscale() {
        win10grayscale(6, 540, 0, OLED6_CODES);
    }

    /**
     * 2017 TVs
     *
     * @see <a href=
     *      "http://www.lg.com/us/support/products/documents/Calibration%20Notes%20for%202017%20LG%20OLED%20TVs.pdf">
     *      Calibration Notes for 2017 LG OLED TVs</a>
     */
    @Test
    public void oled7win10grayscale() {
        win10grayscale(7, 540, 540, OLED7_MAXCLL540_CODES);
        win10grayscale(7, 1000, 1000, OLED7_MAXCLL1000_CODES);
        win10grayscale(7, 4000, 4000, OLED7_MAXCLL4000_CODES);
    }

    public void win10grayscale(int version, int masterDisplay, int maxcll,
            int[] codes) {
        // show brightest and darkest patterns in the beginning
        win10gray(-1, version, masterDisplay, maxcll, codes[codes.length - 1]);
        win10gray(0, version, masterDisplay, maxcll, 64); // pure black

        for (int i = 0; i < codes.length; i++) {
            win10gray(i + 1, version, masterDisplay, maxcll, codes[i]);
        }

        // test clipping of 10000 nit
        win10gray(codes.length + 1, version, masterDisplay, maxcll, 940);
    }

    private void win10gray(int sequence, int version, int masterDisplay,
            int maxcll, int yCode) {
        String name =
                getFileName(sequence, version, masterDisplay, maxcll, yCode);

        EncoderParameters options = HDR10.withEncoderOptions(
                "--max-cll", format("%d,%d", maxcll, maxcll / 10),
                "--master-display",
                MASTER_DISPLAY_PRIMARIES + "L(" + maxcll + "0000,0)");

        EncoderHDR10.encode(name, options, e -> {
            Window win = square(e.encoderParameters.resolution, 0.1);
            YCbCr params = e.parameters;

            CanvasYCbCr canvas = e.newCanvas();
            canvas.Y.fill(params.YMIN);
            canvas.Y.fillRect(win.x, win.y, win.width, win.height,
                    yCode);
            canvas.Cb.fill(params.ACHROMATIC);
            canvas.Cr.fill(params.ACHROMATIC);
            canvas.overlay(overlay(version, maxcll, yCode));

            e.render(DURATION, () -> canvas);
        });
    }

    private static String getFileName(int seq, int version,
            int masterDisplay, int maxcll, int yCode) {
        String dirSuffix = version < 7 ? "" : format("_MaxCLL%04d", maxcll);
        String fileSuffix = version < 7 ? "" : format("_%d", maxcll);
        String fileSeq = seq < 0 ? "$$" : seq == 0 ? "00" : format("%02d", seq);

        return PATH + format("%d%s/GrayHDR10_LGOLED%d%s-%s-Y%03d",
                version, dirSuffix, version, fileSuffix, fileSeq, yCode);
    }

    private static Parent overlay(int version, int maxcll, int yCode) {
        double ye = BT2020_10bit.fromLumaCode(yCode);

        Label label = new Label(getLabel(version, maxcll, yCode));
        label.setFont(font(40));
        label.setTextFill(gray(max(0.25, min(0.5, ye))));

        BorderPane.setMargin(label, new Insets(20));
        BorderPane layout = new BorderPane();
        layout.setBackground(EMPTY);
        layout.setBottom(label);
        return layout;
    }

    private static String getLabel(int version, int maxcll, int yCode) {
        double ye = BT2020_10bit.fromLumaCode(yCode);
        double yo = PQ.eotf(ye);
        CIExy xy = getColor(yo);

        String text = format("LG OLED%d HDR10 MaxCLL %d grayscale"
                + " CIE(x=%.4f, y=%.4f) C%d, %.2f nit",
                version, maxcll, xy.x, xy.y, yCode, yo * 10000.0);

        if (yCode == 668) {
            text += "; set TV to max of 540 nit!";
        }

        return text;
    }

    private static CIExy getColor(double yo) {
        double w = yo <= 0 ? 1 : yo; // fake color for pure black
        double[] rgb = {w, w, w};
        return new CIEXYZ(RGB2XYZ.multiply(rgb)).CIExy();
    }

    public static void main(String[] args) {
        out.println("OLED 2016");
        out.println(getLabel(6, 540, 64));
        for (int element : OLED6_CODES) {
            out.println(getLabel(6, 540, element));
        }
        out.println();
    }
}
