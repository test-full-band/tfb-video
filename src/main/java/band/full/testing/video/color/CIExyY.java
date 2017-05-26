package band.full.testing.video.color;

import static java.lang.String.format;

public class CIExyY {
    public final double x;
    public final double y;
    public final double Y;

    public CIExyY(double x, double y, double Y) {
        this.x = x;
        this.y = y;
        this.Y = Y;
    }

    public CIEXYZ CIEXYZ() {
        return new CIEXYZ(x * Y / y, Y, (1 - x - y) * Y / y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) return false;
        CIExyY other = (CIExyY) o;
        return other.x == x && other.y == y && other.Y == Y;
    }

    @Override
    public String toString() {
        return format("CIE(x=%.5f, y=%.5f, Y=%.5f)", x, y, Y);
    }
}
