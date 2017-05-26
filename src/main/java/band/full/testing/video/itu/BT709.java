package band.full.testing.video.itu;

import static band.full.testing.video.color.Primaries.sRGB;
import static java.lang.Math.pow;

import band.full.testing.video.color.Primaries;
import band.full.testing.video.color.TransferFunctions;

/**
 * Parameter values for the HDTV standards for production and international
 * programme exchange
 *
 * @author Igor Malinin
 * @see <a href=
 *      "http://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.709-6-201506-I!!PDF-E.pdf">
 *      Recommendation ITU-R BT.709-6 (06/2015)</a>
 */
public class BT709 extends YCbCr implements TransferFunctions {
    public static final Primaries PRIMARIES = sRGB;

    public static BT709 BT709 = new BT709();

    private static final double POWER = 0.45;
    private static final double LINEAR = 4.5;
    private static final double ALPHA = 1.09929682680944;
    private static final double BETA = 0.018053968510807;
    private static final double GAMMA = 1.0 / POWER;
    private static final double A1 = ALPHA - 1.0;
    private static final double BL = BETA * LINEAR;

    private BT709() {
        super(8, PRIMARIES);
    }

    @Override
    public double eotf(double v) {
        return v < BL ? v / LINEAR : pow((v + A1) / ALPHA, GAMMA);
    }

    @Override
    public double oetf(double l) {
        return l < BETA ? LINEAR * l : ALPHA * pow(l, POWER) - A1;
    }
}
