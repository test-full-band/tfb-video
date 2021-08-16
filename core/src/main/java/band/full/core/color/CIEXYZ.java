package band.full.core.color;

import static java.lang.Math.cbrt;
import static java.lang.String.format;

/**
 * @author Igor Malinin
 * @see "ASTM E308-01"
 * @see <a href="https://en.wikipedia.org/wiki/Standard_illuminant">Standard
 *      illuminant</a>
 */
public record CIEXYZ(double X, double Y, double Z) {
    public CIEXYZ(double X, double Z) {
        this(X, 1.0, Z);
    }

    public CIEXYZ(double[] XYZ) {
        this(XYZ[0], XYZ[1], XYZ[2]);
    }

    public static final CIEXYZ ILLUMINANT_C = new CIEXYZ(0.98074, 1.18232);
    public static final CIEXYZ ILLUMINANT_D50 = new CIEXYZ(0.96422, 0.82521);
    public static final CIEXYZ ILLUMINANT_D55 = new CIEXYZ(0.95682, 0.92149);

    /**
     * @see <a href="https://en.wikipedia.org/wiki/Illuminant_D65">Wikipedia</a>
     */
    public static final CIEXYZ ILLUMINANT_D65 = new CIEXYZ(0.95047, 1.08883);
    public static final CIEXYZ ILLUMINANT_D75 = new CIEXYZ(0.94972, 1.22638);

    public double[] array() {
        return new double[] {X, Y, Z};
    }

    public CIExyY CIExyY() {
        double sum = X + Y + Z;
        return new CIExyY(X / sum, Y / sum, Y);
    }

    public CIExyY CIExyY(double yScale) {
        double sum = X + Y + Z;
        return new CIExyY(X / sum, Y / sum, Y * yScale);
    }

    public CIExy CIExy() {
        double sum = X + Y + Z;
        return new CIExy(X / sum, Y / sum);
    }

    // TODO test
    public CIELab CIELab() {
        double l = f(Y);
        double L = 116.0 * l - 16.0;
        double a = 500.0 * (f(X) - l);
        double b = 200.0 * (l - f(Z));
        return new CIELab(L, a, b);
    }

    private static double f(double x) {
        return x > 216.0 / 24389.0
                ? cbrt(x)
                : (841.0 / 108.0) * x + (4.0 / 29.0);
    }

    @Override
    public String toString() {
        return format("CIE(X=%.5f, Y=%.5f, Z=%.5f)", X, Y, Z);
    }

    public static void main(String[] args) {
        System.out.println(ILLUMINANT_D65.CIExyY());
        System.out.println(new CIExy(0.31271, 0.32902).CIEXYZ());
        System.out.println(ChromaticAdaptation.BRADFORD_D50_D65);
    }
}
