package band.full.testing.video.color;

public class CIExy {
    public final double x;
    public final double y;

    public CIExy(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) return false;
        CIExy other = (CIExy) o;
        return other.x == x && other.y == y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
