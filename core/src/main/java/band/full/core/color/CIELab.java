package band.full.core.color;

import static java.lang.String.format;

/**
 * @author Igor Malinin
 * @see <a href="https://en.wikipedia.org/wiki/Lab_color_space">Wikipedia</a>
 */
public class CIELab {
    public final double L;
    public final double a;
    public final double b;

    public CIELab(double L, double a, double b) {
        this.L = L;
        this.a = a;
        this.b = b;
    }

    public CIELab(double[] Lab) {
        this(Lab[0], Lab[1], Lab[2]);
    }

    public double[] array() {
        return new double[] {L, a, b};
    }

    // TODO test
    public CIEXYZ CIEXYZ() {
        double i = (L + 16.0) * (1.0 / 116.0);
        double X = fi(i + a * (1.0 / 500.0));
        double Y = fi(i);
        double Z = fi(i - b * (1.0 / 200.0));
        return new CIEXYZ(X, Y, Z);
    }

    private static double fi(double x) {
        return x > 6.0 / 29.0
                ? x * x * x
                : (108.0 / 841.0) * (x - (4.0 / 29.0));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) return false;
        CIELab other = (CIELab) o;
        return other.L == L && other.a == a && other.b == b;
    }

    @Override
    public String toString() {
        return format("CIELAB(L=%.4f, a=%.4f, b=%.4f)", L, a, b);
    }
}
