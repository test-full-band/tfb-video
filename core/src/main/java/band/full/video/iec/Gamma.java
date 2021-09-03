package band.full.video.iec;

import static java.lang.Math.pow;

import band.full.video.itu.TransferCharacteristics;

/**
 * sRGB gamma OETF
 *
 * @see IEC61966_2_1
 */
public class Gamma implements TransferCharacteristics {
    private static final double ALPHA = 1.055;
    private static final double BETA = 0.0031308;
    private static final double GAMMA = 2.4;
    private static final double POWER = 1.0 / GAMMA;
    private static final double LINEAR = 12.92;
    private static final double A1 = ALPHA - 1.0;
    private static final double BL = BETA * LINEAR;

    Gamma() {}

    @Override
    public int code() {
        return 0;
    }

    @Override
    public String toString() {
        return "sRGB";
    }

    @Override
    public boolean isDefinedByEOTF() {
        return false;
    }

    @Override
    public double getNominalDisplayPeakLuminance() {
        return 100.0;
    }

    /** Opto-Electrical Transfer Function */
    @Override
    public double fromLinear(double l) {
        return l < BETA ? LINEAR * l : ALPHA * pow(l, POWER) - A1;
    }

    /** Inverse Opto-Electrical Transfer Function */
    @Override
    public double toLinear(double v) {
        return v < BL ? v / LINEAR : pow((v + A1) / ALPHA, GAMMA);
    }
}
