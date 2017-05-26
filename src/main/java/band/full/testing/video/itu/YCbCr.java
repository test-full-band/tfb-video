package band.full.testing.video.itu;

import band.full.testing.video.color.Matrix3x3;
import band.full.testing.video.color.Primaries;

/**
 * Encoding/decoding of <em>R'G'B'</em> values to/from
 * <em>Y'C'<sub>b</sub>C'<sub>r</sub></em> color space.
 *
 * @author Igor Malinin
 */
public class YCbCr {
    public final int bitdepth;

    public final int YMIN, YMAX;
    public final int CMIN, CMAX;
    public final int ACHROMATIC;

    public final Matrix3x3 RGBtoXYZ;
    public final Matrix3x3 XYZtoRGB;

    public final double RY, GY, BY;
    public final double BCD, RCD;

    public YCbCr(int bitdepth, Primaries primaries) {
        if (bitdepth < 8) throw new IllegalArgumentException(
                "bitdepth should be at least 8 but was " + bitdepth);

        this.bitdepth = bitdepth;

        int shift = bitdepth - 8;

        YMIN = 16 << shift;
        YMAX = 235 << shift;

        CMIN = 16 << shift;
        CMAX = 240 << shift;

        ACHROMATIC = 128 << shift;

        RGBtoXYZ = primaries.getRGBtoXYZ();
        XYZtoRGB = primaries.getXYZtoRGB();

        RY = RGBtoXYZ.get(1, 0);
        GY = RGBtoXYZ.get(1, 1);
        BY = RGBtoXYZ.get(1, 2);

        BCD = (RY + GY) * 2.0;
        RCD = (GY + BY) * 2.0;
    }

    /** Values are in 0..1 range */
    public final double getY(double r, double g, double b) {
        return RY * r + GY * g + BY * b;
    }

    public final double getG(double y, double b, double r) {
        return (y - b * BY - r * RY) / GY;
    }

    /** YB values are in 0..1 range, Cb value is in -0.5..+0.5 range */
    public final double getCb(double y, double b) {
        return (b - y) / BCD;
    }

    public final double getB(double y, double cb) {
        return cb * BCD + y;
    }

    /** YR values are in 0..1 range, Cr value is in -0.5..+0.5 range */
    public final double getCr(double y, double r) {
        return (r - y) / RCD;
    }

    public final double getR(double y, double cr) {
        return cr * RCD + y;
    }

    public final double toLumaCode(double y) {
        return y * (YMAX - YMIN) + YMIN;
    }

    public final double toChromaCode(double c) {
        return c * (CMAX - CMIN) + ACHROMATIC;
    }

    public final double fromLumaCode(double yCode) {
        return (yCode - YMIN) / (YMAX - YMIN);
    }

    public final double fromChromaCode(double cCode) {
        return (cCode - ACHROMATIC) / (CMAX - CMIN);
    }
}
