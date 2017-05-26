package band.full.testing.video.color;

/** Opto-Electrical Transfer Function */
@FunctionalInterface
public interface OETF {
    double oetf(double l);
}
