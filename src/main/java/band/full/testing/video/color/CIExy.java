package band.full.testing.video.color;

import static java.lang.String.format;

public class CIExy {
    public final double x;
    public final double y;

    public CIExy(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public CIEXYZ CIEXYZ() {
        return new CIEXYZ(x / y, 1.0, (1 - x - y) / y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) return false;
        CIExy other = (CIExy) o;
        return other.x == x && other.y == y;
    }

    @Override
    public String toString() {
        return format("(x=%.5f, y=%.5f)", x, y);
    }
}
