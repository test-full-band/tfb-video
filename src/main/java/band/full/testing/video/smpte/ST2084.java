package band.full.testing.video.smpte;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.pow;

import band.full.testing.video.color.TransferFunctions;

/**
 * <p>
 * High Dynamic Range Electro Optical Transfer Function of Mastering Reference
 * Displays.
 *
 * @see SMPTE ST2084:2014 (CEA-861-3)
 * @see <a href=
 *      "https://www.smpte.org/sites/default/files/23-1615-TS7-2-IProc02-Miller.pdf">
 *      Perceptual Signal Coding for More Efficient Usage of Bit Codes</a>
 * @see <a href=
 *      "http://downloads.bbc.co.uk/rd/pubs/whp/whp-pdf-files/WHP283.pdf">
 *      Non-linear Opto-Electrical Transfer Functions for High Dynamic Range
 *      Television</a>
 * @author Igor Malinin
 */
public class ST2084 implements TransferFunctions {
    public static ST2084 PQ = new ST2084();

    public static final double L_MAX = 10000.0;

    private static final double N = 2610.0 / 4096 / 4;
    private static final double M = 2523.0 / 4096 * 128;
    private static final double C1 = 3424.0 / 4096;
    private static final double C2 = 2413.0 / 4096 * 32;
    private static final double C3 = 2392.0 / 4096 * 32;

    private ST2084() {}

    /** values are 0..1 */
    @Override
    public double eotf(double v) {
        if (v <= 0.0)
            return 0.0;

        double vm = pow(v, 1.0 / M);

        double a = vm - C1;
        if (a <= 0.0)
            return 0.0;

        double b = C2 - C3 * vm;
        if (b <= 0.0)
            return POSITIVE_INFINITY;

        return pow(a / b, 1.0 / N);
    }

    /** values are 0..1 */
    @Override
    public double oetf(double l) {
        if (l <= 0.0)
            return 0.0;

        double ln = pow(l, N);

        double a = C1 + C2 * ln;
        double b = 1 + C3 * ln;

        return pow(a / b, M);
    }
}
