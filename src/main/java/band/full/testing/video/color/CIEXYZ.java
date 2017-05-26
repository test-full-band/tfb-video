package band.full.testing.video.color;

import static java.lang.String.format;

public class CIEXYZ {
    public final double X;
    public final double Y;
    public final double Z;

    public CIEXYZ(double X, double Y, double Z) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    public CIEXYZ(double[] XYZ) {
        this(XYZ[0], XYZ[1], XYZ[2]);
    }

    public double[] array() {
        return new double[] {X, Y, Z};
    }

    public CIExyY CIExyY() {
        double sum = X + Y + Z;
        return new CIExyY(X / sum, Y / sum, Y);
    }

    public CIExy CIExy() {
        double sum = X + Y + Z;
        return new CIExy(X / sum, Y / sum);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) return false;
        CIEXYZ other = (CIEXYZ) o;
        return other.X == X && other.Y == Y && other.Z == Z;
    }

    @Override
    public String toString() {
        return format("CIE(X=%.5f, Y=%.5f, Z=%.5f)", X, Y, Z);
    }
}
