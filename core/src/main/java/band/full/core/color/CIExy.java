package band.full.core.color;

import static java.lang.String.format;

public record CIExy(double x, double y) {
    public CIEXYZ CIEXYZ() {
        return new CIEXYZ(x / y, 1.0, (1 - x - y) / y);
    }

    @Override
    public String toString() {
        return format("(x=%.5f, y=%.5f)", x, y);
    }
}
