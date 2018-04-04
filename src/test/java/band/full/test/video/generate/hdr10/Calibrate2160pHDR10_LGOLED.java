package band.full.test.video.generate.hdr10;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.HDR10;
import static band.full.video.encoder.EncoderParameters.HDR10FR;
import static band.full.video.encoder.EncoderParameters.MASTER_DISPLAY_PRIMARIES;
import static band.full.video.itu.ColorRange.FULL;
import static java.lang.String.format;
import static java.util.function.Function.identity;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.GrayscalePatchesGenerator;
import band.full.test.video.generator.PatchesGenerator.Args;
import band.full.video.encoder.EncoderParameters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    private static class LG extends GrayscalePatchesGenerator {
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
        protected String getTopLeftText(Args args) {
            String suffix = version == 6 && args.sequence.equals("$$")
                    ? "; set TV to max of 540 nit!"
                    : "";

            return format("LG OLED 201%d HDR10 %d%s",
                    version, display, suffix);
        }

        @Override
        protected String getFolder(Args args) {
            String dirRange = matrix.range == FULL ? "FR" : "LR";

            String versionDir = version == 6 ? "OLED6"
                    : format("OLED%d%s_%04d", version, dirRange, display);

            return factory.folder + '/' + folder + '/' + versionDir;
        }

        @Override
        protected String getPattern(Args args) {
            String fileRange = matrix.range == FULL ? "10FR" : "10";
            String fileSuffix = version == 6 ? "" : format("_%d", display);

            return format("GrayHDR%s_LGOLED%d%s-%s-Y%03d", fileRange,
                    version, fileSuffix, args.sequence, args.yuv[0]);
        }
    }

    // 2016 TVs

    @ParameterizedTest
    @MethodSource("oled6grayscale")
    public void oled6grayscale(Args args) {
        new LG(HDR10, 6, 540).generate(args);
    }

    public static Stream<Args> oled6grayscale() {
        return grayscale(HDR10, OLED6_CODES);
    }

    // 2017 TVs, Full Range signal

    @ParameterizedTest
    @MethodSource("oled7grayscaleLR540")
    public void oled7grayscaleFR540(Args args) {
        new LG(HDR10FR, 7, 540).generate(args);
    }

    public static Stream<Args> oled7grayscaleFR540() {
        return grayscale(HDR10FR, OLED7_CODES_FR540);
    }

    @ParameterizedTest
    @MethodSource("oled7grayscaleFR1000")
    public void oled7grayscaleFR1000(Args args) {
        new LG(HDR10FR, 7, 1000).generate(args);
    }

    public static Stream<Args> oled7grayscaleFR1000() {
        return grayscale(HDR10FR, OLED7_CODES_FR1000);
    }

    @ParameterizedTest
    @MethodSource("oled7grayscaleFR4000")
    public void oled7grayscaleFR4000(Args args) {
        new LG(HDR10FR, 7, 4000).generate(args);
    }

    public static Stream<Args> oled7grayscaleFR4000() {
        return grayscale(HDR10FR, OLED7_CODES_FR4000);
    }

    // 2017 TVs, Limited Range signal

    @ParameterizedTest
    @MethodSource("oled7grayscaleLR540")
    public void oled7grayscaleLR540(Args args) {
        new LG(HDR10, 7, 540).generate(args);
    }

    public static Stream<Args> oled7grayscaleLR540() {
        return grayscale(HDR10, OLED7_CODES_LR540);
    }

    @ParameterizedTest
    @MethodSource("oled7grayscaleLR1000")
    public void oled7grayscaleLR1000(Args args) {
        new LG(HDR10, 7, 1000).generate(args);
    }

    public static Stream<Args> oled7grayscaleLR1000() {
        return grayscale(HDR10, OLED7_CODES_LR1000);
    }

    @ParameterizedTest
    @MethodSource("oled7grayscaleLR4000")
    public void oled7grayscaleLR4000(Args args) {
        new LG(HDR10, 7, 4000).generate(args);
    }

    public static Stream<Args> oled7grayscaleLR4000() {
        return grayscale(HDR10, OLED7_CODES_LR4000);
    }

    private static Stream<Args> grayscale(EncoderParameters ep, int[] codes) {
        var lg = new GrayscalePatchesGenerator(HEVC, ep, null, null);

        var start = Stream.of(
                lg.gray(10, "$$", codes[codes.length - 1]),
                lg.gray(10, "00", ep.matrix.YMIN));

        var mid = IntStream.range(0, codes.length)
                .mapToObj(i -> lg.gray(10, format("%02d", i + 1), codes[i]));

        var clip = Stream.of(
                lg.gray(10, format("%02d", codes.length + 1), ep.matrix.YMAX));

        return Stream.of(start, mid, clip).flatMap(identity());
    }
}
