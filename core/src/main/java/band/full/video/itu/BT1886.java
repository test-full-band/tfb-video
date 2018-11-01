package band.full.video.itu;

import static java.lang.Math.max;
import static java.lang.Math.pow;

/**
 * Reference electro-optical transfer function for flat panel displays used in
 * HDTV studio production
 *
 * @author Igor Malinin
 * @see <a href=
 *      "https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.1886-0-201103-I!!PDF-E.pdf">
 *      Recommendation ITU-R BT.1886-0 (03/2011)</a>
 */
public class BT1886 {
    public static final BT1886 TRUE_BLACK_TRANSFER = transfer(0.0);

    private static final double GAMMA = 2.4;
    private static final double POWER = 1.0 / GAMMA;

    /**
     * @param b
     *            black level lift
     * @return transfer functions
     */
    public static BT1886 transfer(double b) {
        return new BT1886(b);
    }

    /**
     * @param black
     *            black level cd/m2
     * @param white
     *            white level cd/m2
     * @return transfer functions
     */
    public static BT1886 transfer(double black, double white) {
        double lbp = pow(black, POWER);
        double lwp = pow(white, POWER);
        return new BT1886(lbp / (lwp - lbp));
    }

    /** Black level lift */
    public final double b;

    private BT1886(double b) {
        this.b = b;
    }

    /** Electro-Optical Transfer Function */
    public double eotf(double v) {
        return pow(max(v + b, 0.0), GAMMA);
    }

    /** Inverse Electro-Optical Transfer Function */
    public double eotfi(double l) {
        return pow(max(l, 0.0), POWER) - b;
    }
}
