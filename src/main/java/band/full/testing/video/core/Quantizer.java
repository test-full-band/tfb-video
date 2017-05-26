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

    static Quantizer newRPDF() {
        return null; // FIXME
    }

    static Quantizer newTPDF() {
        return null; // FIXME
    }
}
