package band.full.testing.video.core;

@FunctionalInterface
public interface Quantizer {
    int quantize(double value);

    static int round(double value) {
        long round = Math.round(value);

        return round > Integer.MAX_VALUE ? Integer.MAX_VALUE
                : round < Integer.MIN_VALUE ? Integer.MIN_VALUE
                        : (int) round;
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
