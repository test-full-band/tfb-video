package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.encoder.EncoderParameters.HDR10FR;
import static band.full.testing.video.encoder.EncoderParameters.MASTER_DISPLAY_PRIMARIES;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;
import static band.full.testing.video.itu.ColorRange.FULL;
import static java.lang.String.format;

import band.full.testing.video.color.CIExyY;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.CalibrationBase;

import org.junit.jupiter.api.Test;

/**
 * Calibration patterns for LG OLED TVs.
 *
 * @author Igor Malinin
 * @see <a href=
 *      "http://www.lg.com/us/support/products/documents/UHDA%20Calibration%20Procedure.pdf">
 *      Procedure to Calibrate 2016 OLED TVs to Meet UHD Alliance Grayscale
 *      Tracking Requirements</a>
 * @see <a href=
 *      "http://www.lg.com/us/support/products/documents/Calibration%20Notes%20for%202017%20LG%20OLED%20TVs.pdf">
 *      Calibration Notes for 2017 LG OLED TVs</a>
 */
@GenerateVideo
public class Calibrate2160pHDR10_LGOLED {
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

    private static class LG extends CalibrationBase {
        private final int version;
        private final int display;

        LG(EncoderParameters ep, int version, int display) {
            super(HEVC, ep.withEncoderOptions("--master-display",
                    MASTER_DISPLAY_PRIMARIES + "L(" + display + "0000,0)"),
                    "UHD4K/HDR10/Calibrate/[LG]", "U4K_HDR10");

            this.version = version;
            this.display = display;
        }

        @Override
        protected String getLabelText(Args args) {
            CIExyY xyY = getColor(args);

            String text = format("LG OLED%d HDR10 %d grayscale"
                    + " CIE(x=%.4f, y=%.4f) Y%d, %.2f nit",
                    version, display, xyY.x, xyY.y, args.y, xyY.Y * 10000.0);

            if (version == 6 && args.sequence.equals("$$"))
                return text + "; set TV to max of 540 nit!";

            return text;
        }

        @Override
        protected String getFileName(Args args) {
            boolean v2016 = version == 6;

            String dirRange = matrix.range == FULL ? "FR" : "LR";

            String versionDir = v2016 ? "OLED6"
                    : format("OLED%d%s_%04d", version, dirRange, display);

            String fileRange = matrix.range == FULL ? "10FR" : "10";
            String fileSuffix = v2016 ? "" : format("_%d", display);

            return factory.folder + '/' + folder + '/' + versionDir + '/' +
                    format("GrayHDR%s_LGOLED%d%s-%s-Y%03d", fileRange,
                            version, fileSuffix, args.sequence, args.y);
        }
    }

    /** 2016 TVs */
    @Test
    public void oled6grayscale() {
        grayscale(6, 540, HDR10, OLED6_CODES);
    }

    /** 2017 TVs, Full Range signal */
    @Test
    public void oled7grayscaleFR() {
        grayscale(7, 540, HDR10FR, OLED7_CODES_FR540);
        grayscale(7, 1000, HDR10FR, OLED7_CODES_FR1000);
        grayscale(7, 4000, HDR10FR, OLED7_CODES_FR4000);
    }

    /** 2017 TVs, Limited Range signal */
    @Test
    public void oled7grayscaleLR() {
        grayscale(7, 540, HDR10, OLED7_CODES_LR540);
        grayscale(7, 1000, HDR10, OLED7_CODES_LR1000);
        grayscale(7, 4000, HDR10, OLED7_CODES_LR4000);
    }

    public void grayscale(int version, int display,
            EncoderParameters ep, int[] codes) {
        LG lg = new LG(ep, version, display);

        // show brightest and darkest patterns in the beginning
        generate(lg, "$$", codes[codes.length - 1]);
        generate(lg, "00", ep.matrix.YMIN);

        for (int i = 0; i < codes.length; i++) {
            generate(lg, format("%02d", i + 1), codes[i]);
        }

        // test clipping of 10000 nit
        generate(lg, format("%02d", codes.length + 1), ep.matrix.YMAX);
    }

    private void generate(LG lg, String sequence, int y) {
        lg.generate(lg.gray(10, sequence, y));
    }
}
