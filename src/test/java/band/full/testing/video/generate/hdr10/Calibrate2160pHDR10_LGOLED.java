package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Framerate.FPS_23_976;
import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.core.Window.square;
import static band.full.testing.video.encoder.EncoderParameters.MASTER_DISPLAY_PRIMARIES;
import static band.full.testing.video.itu.BT2020.BT2020_10bit;
import static band.full.testing.video.itu.BT2020.PRIMARIES;
import static band.full.testing.video.itu.ColorRange.FULL;
import static band.full.testing.video.itu.ColorRange.LIMITED;
import static band.full.testing.video.smpte.ST2084.PQ;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.paint.Color.gray;
import static javafx.scene.text.Font.font;

import band.full.testing.video.color.CIEXYZ;
import band.full.testing.video.color.CIExy;
import band.full.testing.video.color.Matrix3x3;
import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.core.Window;
import band.full.testing.video.encoder.EncoderHEVC;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.itu.ColorRange;
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
@GenerateVideo
public class Calibrate2160pHDR10_LGOLED {
    // TODO extends CalibrationBase
    private static final String PATH = "HEVC/UHD4K/HDR10/Calibrate/LG/OLED";

    private static final Duration DURATION = ofSeconds(30);
    private static final Matrix3x3 RGB2XYZ = PRIMARIES.getRGBtoXYZ();

    private static final int[] OLED6_CODES = {
        127, 254, 320, 386, 419, 451, 467, 482, 498, 513,
        529, 544, 560, 575, 591, 606, 622, 637, 653, 668,
    };

    private static final int[] OLED7_CODES_FR540 = {
        240, 314, 388, 419, 453, 468, 481, 498, 515, 529,
        545, 561, 575, 591, 608, 623, 637, 653, 669,
    };

    private static final int[] OLED7_CODES_LR540 = {
        269, 332, 396, 422, 451, 464, 475, 490, 504, 516,
        530, 544, 556, 570, 584, 597, 609, 623, 636, // 401.45 nit
    };

    private static final int[] OLED7_CODES_FR1000 = {
        277, 345, 412, 446, 480, 498, 513, 530, 544, 561,
        574, 589, 604, 617, 632, 647, 663, 678, 693,
    };

    private static final int[] OLED7_CODES_LR1000 = {
        301, 359, 416, 445, 475, 490, 503, 517, 529, 544,
        555, 568, 581, 592, 605, 618, 631, 644, 657, // 501.65 nit
    };

    private static final int[] OLED7_CODES_FR4000 = {
        320, 382, 443, 475, 502, 516, 530, 545, 557, 572,
        584, 599, 612, 627, 640, 654, 666, 678, 705,
    };

    private static final int[] OLED7_CODES_LR4000 = {
        299, 372, 443, 480, 512, 528, 545, 562, 576, 594,
        608, 625, 640, 658, 673, 690, 704, 718, 749, // 831.56 nit
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
    public void oled6grayscale() {
        grayscale(6, 540, LIMITED, OLED6_CODES);
    }

    /**
     * 2017 TVs
     *
     * @see <a href=
     *      "http://www.lg.com/us/support/products/documents/Calibration%20Notes%20for%202017%20LG%20OLED%20TVs.pdf">
     *      Calibration Notes for 2017 LG OLED TVs</a>
     */
    @Test
    public void oled7grayscale() {
        grayscale(7, 540, FULL, OLED7_CODES_FR540);
        grayscale(7, 540, LIMITED, OLED7_CODES_LR540);
        grayscale(7, 1000, FULL, OLED7_CODES_FR1000);
        grayscale(7, 1000, LIMITED, OLED7_CODES_LR1000);
        grayscale(7, 4000, FULL, OLED7_CODES_FR4000);
        grayscale(7, 4000, LIMITED, OLED7_CODES_LR4000);
    }

    public void grayscale(int version, int display,
            ColorRange range, int[] codes) {
        YCbCr params = new YCbCr(BT2020_10bit.code, PRIMARIES, 10, range);

        // show brightest and darkest patterns in the beginning
        if (version == 6) {
            grayscale(-1, version, display, params, codes[codes.length - 1],
                    "; set TV to max of 540 nit!");
        } else {
            grayscale(-1, version, display, params, codes[codes.length - 1]);
        }

        grayscale(0, version, display, params, params.YMIN); // pure black

        for (int i = 0; i < codes.length; i++) {
            grayscale(i + 1, version, display, params, codes[i]);
        }

        // test clipping of 10000 nit
        grayscale(codes.length + 1, version, display, params, params.YMAX);
    }

    private void grayscale(int sequence, int version, int display,
            YCbCr params, int yCode) {
        grayscale(sequence, version, display, params, yCode, "");
    }

    private void grayscale(int sequence, int version, int display,
            YCbCr params, int yCode, String suffix) {
        String name = getFileName(sequence, version, display, params, yCode);

        EncoderParameters options =
                new EncoderParameters(STD_2160p, PQ, params, FPS_23_976)
                        .withEncoderOptions(
                                "--master-display", MASTER_DISPLAY_PRIMARIES
                                        + "L(" + display + "0000,0)");

        EncoderHEVC.encode(name, options, e -> {
            Window win = square(e.parameters.resolution, 0.1);

            CanvasYCbCr canvas = e.newCanvas();
            canvas.Y.fillRect(win.x, win.y, win.width, win.height, yCode);
            canvas.overlay(overlay(version, display, params, yCode, suffix));

            e.render(DURATION, () -> canvas);
        });
    }

    private static String getFileName(int seq, int version, int display,
            YCbCr params, int yCode) {
        boolean v2016 = version == 6;

        String dirRange = params.range == FULL ? "FR" : "LR";
        String dirSuffix = v2016 ? "" : format("%s_%04d", dirRange, display);
        String fileRange = params.range == FULL ? "FR" : "10"; // std HDR10
        String fileSuffix = v2016 ? "" : format("_%d", display);
        String fileSeq = seq < 0 ? "$$" : format("%02d", seq);

        return PATH + format("%d%s/GrayHDR%s_LGOLED%d%s-%s-Y%03d", version,
                dirSuffix, fileRange, version, fileSuffix, fileSeq, yCode);
    }

    private static Parent overlay(int version, int display,
            YCbCr params, int yCode, String suffix) {
        double ye = params.fromLumaCode(yCode);

        Label label = new Label(getLabel(version, display, params, yCode,
                suffix));
        label.setFont(font(40));
        label.setTextFill(gray(max(0.25, min(0.5, ye))));

        BorderPane.setMargin(label, new Insets(20));
        BorderPane layout = new BorderPane();
        layout.setBackground(EMPTY);
        layout.setBottom(label);
        return layout;
    }

    private static String getLabel(int version, int display,
            YCbCr params, int yCode, String suffix) {
        double ye = params.fromLumaCode(yCode);
        double yo = PQ.eotf(ye);
        CIExy xy = getColor(yo);

        String text = format("LG OLED%d HDR10 %d grayscale"
                + " CIE(x=%.4f, y=%.4f) Y%d, %.2f nit",
                version, display, xy.x, xy.y, yCode, yo * 10000.0);

        return text + suffix;
    }

    private static CIExy getColor(double yo) {
        double w = yo <= 0 ? 1 : yo; // fake color for pure black
        double[] rgb = {w, w, w};
        return new CIEXYZ(RGB2XYZ.multiply(rgb)).CIExy();
    }
}
