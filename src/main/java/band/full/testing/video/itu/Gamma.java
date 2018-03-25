package band.full.testing.video.itu;

import static java.lang.Math.pow;

/**
 * Common gamma OETF.<br>
 * Uses precise definition of the formula from BT.2020 recommendation.
 *
 * @see BT2020
 */
public class Gamma implements TransferCharacteristics {
    private static final double POWER = 0.45;
    private static final double LINEAR = 4.5;
    private static final double ALPHA = 1.09929682680944;
    private static final double BETA = 0.018053968510807;
    private static final double GAMMA = 1.0 / POWER;
    private static final double A1 = ALPHA - 1.0;
    private static final double BL = BETA * LINEAR;

    private final int code;
    private final String name;

    Gamma(int code, String name) {
        this.code = code;
        this.name = name;
    }

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
