package band.full.test.video.generate.hdr10;

import static band.full.core.ArrayMath.multiply;
import static band.full.video.encoder.EncoderParameters.HDR10;
import static band.full.video.itu.BT2020.PRIMARIES;
import static band.full.video.itu.BT709.BT709_8bit;
import static band.full.video.smpte.ST2084.PQ;
import static java.lang.String.format;

import band.full.core.Quantizer;
import band.full.core.color.CIEXYZ;
import band.full.core.color.CIExyY;
import band.full.core.color.Matrix3x3;
import band.full.core.color.Primaries;
import band.full.video.itu.TransferCharacteristics;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * TODO Find the right YCbCr code points to fulfill the expectations
 *
 * @author Igor Malinin
 */
@Disabled("Investigate chromacity percentage")
public class Calibrate2160pHDR10_CalMAN {
    private static final Matrix3x3 RGBtoXYZ = PRIMARIES.RGBtoXYZ;

    // TODO Calman codes for red
    private static final double[][] RxyY = {
        {0.0, 0.3127, 0.3290, 94.39},
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
    public void red() {
        for (var x : RxyY) {
            System.out.println(x[1]);
        }

        System.out.println();

        for (var y : RxyY) {
            System.out.println(y[2]);
        }

        System.out.println();

        for (var y : RxyY) {
            System.out.println(y[3]);
        }

        System.out.println();

        var XYZtoRGB = PRIMARIES.XYZtoRGB;

        for (double[] dxyY : RxyY) {
            CIExyY xyY = new CIExyY(dxyY[1], dxyY[2], dxyY[3] / 10000.0);

            var xyz = xyY.CIEXYZ().array();

            // XYZ
            System.out.print(format("X%.6f Y%.6f Z%.6f | ",
                    xyz[0], xyz[1], xyz[2]));

            var linear = XYZtoRGB.multiply(xyz);

            // Linear RGB
            System.out.print(format("R%.6f G%.6f B%.6f | ",
                    linear[0], linear[1], linear[2]));

            double[] rgb = PQ.fromLinear(linear, new double[3]);

            // PQ RGB
            System.out.print(format("R%.4f G%6.4f B%.4f | ",
                    rgb[0], rgb[1], rgb[2]));

            HDR10.matrix.toRGBCodes(rgb, rgb);

            // RGB Codes (video std)
            System.out.print(format("R%05.1f G%05.1f B%05.1f | ",
                    rgb[0], rgb[1], rgb[2]));

            double[] yuv = BT709_8bit.fromRGB(linear, new double[3]);

            // Electrical YCbCr (video std)
            System.out.print(format("Y%.4f Cb%.4f Cr%.4f | ",
                    yuv[0], yuv[1], yuv[2]));

            int[] codes = BT709_8bit.toCodes(yuv, Quantizer::round, new int[3]);

            // YCbCr Codes (video std)
            System.out.print(format("Y%d Cb%d Cr%d | ",
                    codes[0], codes[1], codes[2]));

            xyz = RGBtoXYZ.multiply(linear);
            System.out.println(new CIEXYZ(xyz).CIExyY());
        }
    }

    @Test
    public void red50() {
        TransferCharacteristics transfer = HDR10.matrix.transfer;
        double y50 = transfer.toLinear(HDR10.matrix.fromLumaCode(504));
        System.out.println(format("Y 0.5 to linear: %.6f", y50));

        for (int i = 0; i <= 100; i += 10) {
            var linear = getRed(PRIMARIES, y50, i);

            // Linear RGB
            System.out.print(format("R%.6f G%.6f B%.6f | ",
                    linear[0], linear[1], linear[2]));

            var rgb = PQ.fromLinear(linear, new double[3]);

            // PQ RGB
            System.out.print(format("R%.4f G%6.4f B%.4f | ",
                    rgb[0], rgb[1], rgb[2]));

            HDR10.matrix.toRGBCodes(rgb, rgb);

            // RGB Codes (video std)
            System.out.println(format("R%05.1f G%05.1f B%05.1f | ",
                    rgb[0], rgb[1], rgb[2]));
        }
    }

    public double[] getRed(Primaries p, double r, int saturation) {
        double x = p.white.x + (p.red.x - p.white.x) / 100 * saturation;
        double y = p.white.y + (p.red.y - p.white.y) / 100 * saturation;

        double[] rgb = p.XYZtoRGB.multiply(
                new CIExyY(x, y, 1.0).CIEXYZ().array());

        return multiply(rgb, rgb, r / rgb[0]);
    }
}
