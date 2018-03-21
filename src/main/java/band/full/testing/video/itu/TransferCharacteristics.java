package band.full.testing.video.itu;

import java.util.function.DoubleUnaryOperator;

public interface TransferCharacteristics {
    int code();

    boolean isDefinedByEOTF();

    double getNominalDisplayPeakLuminance();

    /**
     * Opto-Electrical Transfer Function.
     * <p>
     * Values are normalized in the range [0.0 .. 1.0]
     */
    double oetf(double l);

    /**
     * Electro-Optical Transfer Function
     * <p>
     * Values are normalized in the range [0.0 .. 1.0]
     */
    double eotf(double v);

    /**
     * Inverse Opto-Electrical Transfer Function
     * <p>
     * Values are normalized in the range [0.0 .. 1.0]
     */
    double oetfi(double v);

    /**
     * Inverse Electro-Optical Transfer Function
     * <p>
     * Values are normalized in the range [0.0 .. 1.0]
     */
    double eotfi(double l);

    default double[] oetf(double[] src, double[] dst) {
        return transfer(this::oetf, src, dst);
    }

    default double[] eotf(double[] src, double[] dst) {
        return transfer(this::eotf, src, dst);
    }

    default double[] oetfi(double[] src, double[] dst) {
        return transfer(this::oetfi, src, dst);
    }

    default double[] eotfi(double[] src, double[] dst) {
        return transfer(this::eotfi, src, dst);
    }

    static double[] transfer(DoubleUnaryOperator op,
            double[] src, double[] dst) {
        for (int i = 0; i < src.length; i++) {
            dst[i] = op.applyAsDouble(src[i]);
        }

        return dst;
    }
}
