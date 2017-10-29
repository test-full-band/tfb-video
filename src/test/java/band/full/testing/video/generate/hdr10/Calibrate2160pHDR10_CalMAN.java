package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.executor.GenerateVideo.Type.LOSSLESS;
import static band.full.testing.video.itu.BT2020.PRIMARIES;
import static band.full.testing.video.itu.BT709.BT709;
import static java.lang.String.format;

import band.full.testing.video.color.CIEXYZ;
import band.full.testing.video.color.CIExyY;
import band.full.testing.video.color.Matrix3x3;
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
    private static final Matrix3x3 RGB2XYZ = PRIMARIES.getRGBtoXYZ();

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
        Matrix3x3 XYZ2RGB = PRIMARIES.getXYZtoRGB();

        for (double[] dxyY : RxyY) {
            CIExyY xyY = new CIExyY(dxyY[1], dxyY[2], dxyY[3] / 100.0);
            double[] rgb = XYZ2RGB.multiply(xyY.CIEXYZ().array());

            // Linear RGB
            System.out.print(format("R%.4f G%6.4f B%.4f | ",
                    rgb[0], rgb[1], rgb[2]));

            // TODO: PQ
            double r = BT709.oetf(rgb[0]);
            double g = BT709.oetf(rgb[1]);
            double b = BT709.oetf(rgb[2]);

            // RGB Codes (video std)
            System.out.print(format("R%05.1f G%05.1f B%05.1f | ",
                    BT709.toLumaCode(r),
                    BT709.toLumaCode(g),
                    BT709.toLumaCode(b)));

            double y = BT709.getY(rgb[0], rgb[1], rgb[2]);
            double cb = BT709.getCb(y, rgb[2]);
            double cr = BT709.getCb(y, rgb[1]);

            // Electrical YCbCr (video std)
            System.out.print(format("Y%.4f Cb%.4f Cr%.4f | ",
                    y, cb, cr));

            // YCbCr Codes (video std)
            System.out.print(format("Y%05.1f Cb%05.1f Cr%05.1f | ",
                    BT709.toLumaCode(y),
                    BT709.toChromaCode(cb),
                    BT709.toChromaCode(cr)));

            CIEXYZ xyz = new CIEXYZ(RGB2XYZ.multiply(rgb));
            System.out.println(xyz.CIExyY());
        }
    }
}
