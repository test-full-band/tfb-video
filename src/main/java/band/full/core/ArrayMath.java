package band.full.core;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Igor Malinin
 */
public class ArrayMath {
    private ArrayMath() {}

    public static double[] add(double[] in, double[] out, int mul) {
        for (int i = 0; i < in.length; i++) {
            out[i] = in[i] + mul;
        }

        return out;
    }

    public static int[] multiply(int[] in, int[] out, int mul) {
        for (int i = 0; i < in.length; i++) {
            out[i] = in[i] * mul;
        }

        return out;
    }

    public static double[] multiply(double[] in, double[] out, double mul) {
        for (int i = 0; i < in.length; i++) {
            out[i] = in[i] * mul;
        }

        return out;
    }

    public static double[] apply(double[] in, double[] out,
            DoubleUnaryOperator op) {
        for (int i = 0; i < in.length; i++) {
            out[i] = op.applyAsDouble(in[i]);
        }

        return out;
    }
}
