package band.full.testing.video.itu;

import static java.lang.Math.pow;

/**
 * Uses precise definition of the formula from BT.2020 recommendation
 */
class Gamma implements TransferCharacteristics {
    private static final double POWER = 0.45;
    private static final double LINEAR = 4.5;
    private static final double ALPHA = 1.09929682680944;
    private static final double BETA = 0.018053968510807;
    private static final double GAMMA = 1.0 / POWER;
    private static final double A1 = ALPHA - 1.0;
    private static final double BL = BETA * LINEAR;

    private final int code;

    Gamma(int code) {
        this.code = code;
    }

    @Override
    public int code() {
        return code;
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
