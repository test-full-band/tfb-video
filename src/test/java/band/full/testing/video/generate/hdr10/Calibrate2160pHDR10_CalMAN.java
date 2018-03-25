package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.executor.GenerateVideo.Type.LOSSLESS;
import static band.full.testing.video.itu.BT2020.PRIMARIES;
import static band.full.testing.video.itu.BT709.BT709_8bit;
import static band.full.testing.video.smpte.ST2084.PQ;
import static java.lang.String.format;

import band.full.testing.video.color.CIEXYZ;
import band.full.testing.video.color.CIExyY;
import band.full.testing.video.color.Matrix3x3;
import band.full.testing.video.core.Quantizer;
import band.full.testing.video.executor.GenerateVideo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * TODO Find the right YCbCr code points to fulfill the expectations
 *
 * @author Igor Malinin
 */
@GenerateVideo(LOSSLESS)
@Disabled("Investigate chromacity percentage")
public class Calibrate2160pHDR10_CalMAN {
    private static final Matrix3x3 RGBtoXYZ = PRIMARIES.RGBtoXYZ;

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
    public void red() throws Exception {
        var XYZtoRGB = PRIMARIES.XYZtoRGB;

        for (double[] dxyY : RxyY) {
            CIExyY xyY = new CIExyY(dxyY[1], dxyY[2], dxyY[3] / 100.0);
            var linear = XYZtoRGB.multiply(xyY.CIEXYZ().array());

            // Linear RGB
            System.out.print(format("R%.4f G%6.4f B%.4f | ",
                    linear[0], linear[1], linear[2]));

            double[] rgb = PQ.fromLinear(linear, new double[3]);

            // RGB Codes (video std)
            System.out.print(format("R%05.1f G%05.1f B%05.1f | ",
                    rgb[0], rgb[1], rgb[2]));

            double[] yuv = BT709_8bit.fromRGB(linear, new double[3]);

            // Electrical YCbCr (video std)
            System.out.print(format("Y%.4f Cb%.4f Cr%.4f | ",
                    yuv[0], yuv[1], yuv[2]));

            int[] codes = BT709_8bit.toCodes(yuv, Quantizer::round, new int[3]);

            // YCbCr Codes (video std)
            System.out.print(format("Y%05.1f Cb%05.1f Cr%05.1f | ",
                    codes[0], codes[1], codes[2]));

            var xyz = new CIEXYZ(RGBtoXYZ.multiply(linear));
            System.out.println(xyz.CIExyY());
        }
    }
}
