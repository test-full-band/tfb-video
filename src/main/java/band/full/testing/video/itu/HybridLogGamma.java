package band.full.testing.video.itu;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;

/**
 * ARIB <em>STD-B67</em>, <em>Hybrid Log Gama</em> EOTF/OETF.
 *
 * @author Igor Malinin
 * @see <a href=
 *      "https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.2100-1-201706-I!!PDF-E.pdf">
 *      Rec. ITU-R BT.2100-1 (06/2017)</a>
 * @see <a href=
 *      "https://www.arib.or.jp/english/html/overview/doc/2-STD-B67v1_0.pdf">
 *      Essential Parameter Values for the Extended Image Dynamic Range
 *      Television (EIDRTV) System for Programme Production</a>
 */
public final class HybridLogGamma implements TransferCharacteristics {
    public static final HybridLogGamma HLG = new HybridLogGamma();

    public static final double A = 0.17883277;
    public static final double B = 1.0 - 4.0 * A;
    public static final double C = 0.5 - A * log(4.0 * A);

    private HybridLogGamma() {}

    /**
     * transfer_characteristics = 18
     * <ul>
     * <li>Association of Radio Industries and Businesses (ARIB) STD-B67
     * <li>Rec. ITU-R BT.2100-0 hybrid log-gamma (HLG) system
     * </ul>
     */
    @Override
    public int code() {
        return 18;
    }

    @Override
    public boolean isDefinedByEOTF() {
        return false;
    }

    @Override
    public double getNominalDisplayPeakLuminance() {
        return 1_000.0;
    }

    @Override
    public double oetf(double l) {
        if (l <= 0.0)
            return 0.0;

        return l <= 1.0 / 12.0 ? sqrt(3.0 * l) : A * log(12.0 * l - B) + C;
    }

    @Override
    public double oetfi(double v) {
        if (v <= 0.0)
            return 0.0;

        return v <= 0.5 ? v * v / 3.0 : (exp((v - C) / A) + B) / 12.0;
    }

    @Override
    public double eotf(double v) {
        return oetfi(v); // TODO
    }

    @Override
    public double eotfi(double l) {
        return oetf(l); // TODO
    }
}
