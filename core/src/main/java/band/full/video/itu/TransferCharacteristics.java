package band.full.video.itu;

import java.util.function.DoubleUnaryOperator;

public interface TransferCharacteristics {
    int code();

    boolean isDefinedByEOTF();

    double getNominalDisplayPeakLuminance();

    /** Values are normalized in the range [0.0 .. 1.0] */
    double fromLinear(double l);

    /** Values are normalized in the range [0.0 .. 1.0] */
    double toLinear(double v);

    default double[] fromLinear(double[] src, double[] dst) {
        return transfer(this::fromLinear, src, dst);
    }

    default double[] toLinear(double[] src, double[] dst) {
        return transfer(this::toLinear, src, dst);
    }

    static double[] transfer(DoubleUnaryOperator op,
            double[] src, double[] dst) {
        for (int i = 0; i < src.length; i++) {
            dst[i] = op.applyAsDouble(src[i]);
        }

        return dst;
    }
}
