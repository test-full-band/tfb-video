package band.full.testing.video.itu;

import static band.full.testing.video.itu.ColorRange.FULL;

import band.full.testing.video.color.Matrix3x3;
import band.full.testing.video.color.Primaries;

/**
 * Encoding/decoding of <em>R'G'B'</em> values to/from
 * <em>IC<sub>T</sub>C<sub>P</sub></em> color space.
 *
 * @author Igor Malinin
 */
public final class ICtCp extends ColorMatrix {
    public final TransferCharacteristics transfer;
    public final int bitdepth;
    public final ColorRange range;

    public final int YMIN, YMAX;
    public final int CMIN, CMAX;
    public final int ACHROMATIC;

    public final Matrix3x3 RGBtoXYZ;
    public final Matrix3x3 XYZtoRGB;

    public ICtCp(int code, TransferCharacteristics transfer,
            Primaries primaries, int bitdepth) {
        this(code, transfer, primaries, bitdepth, FULL);
    }

    public ICtCp(int code, TransferCharacteristics transfer,
            Primaries primaries, int bitdepth,
            ColorRange range) {
        super(code, primaries);

        if (bitdepth < 8) throw new IllegalArgumentException(
                "bitdepth should be at least 8 but was " + bitdepth);

        this.transfer = transfer;
        this.bitdepth = bitdepth;
        this.range = range;

        int shift = bitdepth - 8;

        YMIN = range == FULL ? 0 : 16 << shift;
        YMAX = range == FULL ? (256 << shift) - 1 : 235 << shift;

        CMIN = range == FULL ? 1 : 16 << shift;
        CMAX = range == FULL ? (256 << shift) - 1 : 240 << shift;

        ACHROMATIC = 128 << shift;

        RGBtoXYZ = primaries.getRGBtoXYZ();
        XYZtoRGB = primaries.getXYZtoRGB();
    }

    /** Input is linear RGB. Output is nonlinear ITP. */
    @Override
    public void fromRGB(double[] rgb, double[] itp) {
        double r = rgb[0];
        double g = rgb[1];
        double b = rgb[2];

        double l = transfer.oetf((1688d * r + 2146d * g + 262d * b) / 4096d);
        double m = transfer.oetf((683d * r + 2951d * g + 462d * b) / 4096d);
        double s = transfer.oetf((99d * r + 309d * g + 3688d * b) / 4096d);

        itp[0] = (l + m) / 2d; // I
        itp[1] = (6610 * l - 13611 * m + 7003 * s) / 4096d; // Ct
        itp[2] = (17933 * l - 17390 * m + 543 * s) / 4096d; // Cp
    }

    @Override
    public void toRGB(double[] itp, double[] rgb) {
        throw new NoSuchMethodError("Decoding have to be implemented!"); // TODO
    }

    public final double toLumaCode(double i) {
        return i * (YMAX - YMIN) + YMIN;
    }

    public final double toChromaCode(double c) {
        return c * (CMAX - CMIN) + ACHROMATIC;
    }

    public final double fromLumaCode(double iCode) {
        return (iCode - YMIN) / (YMAX - YMIN);
    }

    public final double fromChromaCode(double cCode) {
        return (cCode - ACHROMATIC) / (CMAX - CMIN);
    }
}
