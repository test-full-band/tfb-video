package band.full.testing.video.color;

/** Opto-Electrical Transfer Function */
@FunctionalInterface
public interface OETF {
    double oetf(double l);

    default void oetf(double[] src, double[] dst) {
        for (int i = 0; i < src.length; i++) {
            dst[i] = oetf(src[i]);
        }
    }
}
