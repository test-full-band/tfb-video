package band.full.core.color;

import static java.lang.String.format;

public record CIExyY(double x, double y, double Y) {
    public CIEXYZ CIEXYZ() {
        return new CIEXYZ(x * Y / y, Y, (1.0 - x - y) * Y / y);
    }

    @Override
    public String toString() {
        return format("CIE(x=%.5f, y=%.5f, Y=%.5f)", x, y, Y);
    }
}
