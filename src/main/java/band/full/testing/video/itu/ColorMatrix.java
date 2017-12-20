package band.full.testing.video.itu;

import band.full.testing.video.color.Primaries;

/**
 * Color Matrix
 *
 * @author Igor Malinin
 */
public abstract class ColorMatrix {
    public final int code;
    public final Primaries primaries;

    protected ColorMatrix(int code, Primaries primaries) {
        this.primaries = primaries;
        this.code = code;
    }

    public abstract void toRGB(double[] src, double[] rgb);

    public abstract void fromRGB(double[] rgb, double[] dst);
}
