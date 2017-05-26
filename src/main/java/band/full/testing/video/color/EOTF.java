package band.full.testing.video.color;

/** Electro-Optical Transfer Function */
@FunctionalInterface
public interface EOTF {
    double eotf(double v);
}
