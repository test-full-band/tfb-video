package band.full.video.smpte;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.pow;

import band.full.video.itu.TransferCharacteristics;

/**
 * High Dynamic Range Electro Optical Transfer Function of Mastering Reference
 * Displays.
 * <p>
 * transfer_characteristics = 16
 * <ul>
 * <li>SMPTE ST 2084 for 10, 12, 14 and 16-bit systems
 * <li>Rec. ITU-R BT.2100-0 perceptual quantization (PQ) system
 * </ul>
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
public class ST2084 implements TransferCharacteristics {
    public static ST2084 PQ = new ST2084();

    public static final double L_MAX = 10_000.0;

    private static final double N = 2610.0 / 4096 / 4;
    private static final double M = 2523.0 / 4096 * 128;
    private static final double C1 = 3424.0 / 4096;
    private static final double C2 = 2413.0 / 4096 * 32;
    private static final double C3 = 2392.0 / 4096 * 32;

    private final int code;
    private final String name;

    protected ST2084() {
        this(16, "PQ");
    }

    public ST2084(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * transfer_characteristics = 16
     * <ul>
     * <li>SMPTE ST 2084 for 10, 12, 14 and 16-bit systems
     * <li>Rec. ITU-R BT.2100-0 perceptual quantization (PQ) system
     * </ul>
     */
    @Override
    public int code() {
        return code;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean isDefinedByEOTF() {
        return true;
    }

    @Override
    public double getNominalDisplayPeakLuminance() {
        return 10_000.0;
    }

    @Override
    public double toLinear(double v) {
        if (v <= 0.0) return 0.0;

        double vm = pow(v, 1.0 / M);

        double a = vm - C1;
        if (a <= 0.0) return 0.0;

        double b = C2 - C3 * vm;
        return b <= 0.0
                ? POSITIVE_INFINITY
                : pow(a / b, 1.0 / N);
    }

    @Override
    public double fromLinear(double l) {
        if (l <= 0.0) return 0.0;

        double ln = pow(l, N);

        double a = C1 + C2 * ln;
        double b = 1 + C3 * ln;
        return pow(a / b, M);
    }
}
