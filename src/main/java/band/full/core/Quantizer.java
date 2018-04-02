package band.full.core;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.Math.max;
import static java.lang.Math.min;

@FunctionalInterface
public interface Quantizer {
    int quantize(double value);

    static int round(double value) {
        long round = Math.round(value);

        return (int) min(max(round, MIN_VALUE), MAX_VALUE);
    }

    static int[] round(double[] values) {
        return round(values, new int[values.length]);
    }

    static int[] round(double[] values, int[] dst) {
        for (int i = 0; i < values.length; i++) {
            dst[i] = round(values[i]);
        }

        return dst;
    }
}
