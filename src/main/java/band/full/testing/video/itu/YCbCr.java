package band.full.testing.video.itu;

import static band.full.testing.video.itu.ColorRange.NARROW;

import band.full.testing.video.color.Primaries;

/**
 * Encoding/decoding of <em>R'G'B'</em> values to/from
 * <em>Y'C'<sub>b</sub>C'<sub>r</sub></em> color space.
 *
 * @author Igor Malinin
 */
public final class YCbCr extends ColorMatrix {
    public final double RY, GY, BY;
    public final double BCD, RCD;

    public YCbCr(int code, TransferCharacteristics transfer,
            Primaries primaries, int bitdepth) {
        this(code, transfer, primaries, bitdepth, NARROW);
    }

    public YCbCr(int code, TransferCharacteristics transfer,
            Primaries primaries, int bitdepth, ColorRange range) {
        super(code, transfer, primaries, bitdepth, range);

        RY = RGBtoXYZ.get(1, 0);
        GY = RGBtoXYZ.get(1, 1);
        BY = RGBtoXYZ.get(1, 2);

        BCD = (RY + GY) * 2.0;
        RCD = (GY + BY) * 2.0;
    }

    @Override
    public double[] fromRGB(double[] rgb, double[] yuv) {
        double r = rgb[0];
        double b = rgb[2];
        double y = getY(r, rgb[1], b);

        yuv[0] = y;
        yuv[1] = getCb(y, b);
        yuv[2] = getCr(y, r);

        return yuv;
    }

    @Override
    public double[] fromLinearRGB(double[] rgb, double[] yuv) {
        transfer.oetf(rgb, yuv);
        return fromRGB(yuv, yuv);
    }

    @Override
    public double[] toRGB(double[] yuv, double[] rgb) {
        double y = yuv[0];
        double b = getB(y, yuv[1]);
        double r = getR(y, yuv[2]);

        rgb[0] = r;
        rgb[1] = getG(y, b, r);
        rgb[2] = b;

        return rgb;
    }

    @Override
    public double[] toLinearRGB(double[] yuv, double[] rgb) {
        toRGB(yuv, rgb);
        return transfer.eotf(rgb, rgb);
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
}
