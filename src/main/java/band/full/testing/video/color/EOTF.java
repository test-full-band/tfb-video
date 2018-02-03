package band.full.testing.video.color;

/** Electro-Optical Transfer Function */
@FunctionalInterface
public interface EOTF {
    double eotf(double v);

    default double[] eotf(double[] src, double[] dst) {
        for (int i = 0; i < src.length; i++) {
            dst[i] = eotf(src[i]);
        }

        return dst;
    }
}
