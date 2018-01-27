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

    public final Matrix3x3 RGBtoLMS;
    public final Matrix3x3 LMStoRGB;

    public final Matrix3x3 ITPtoPQLMS;
    public final Matrix3x3 PQLMStoITP;

    // TODO derive matrix from BT.2020 color space
    // private final Matrix3x3 LMStoXYZ = new Matrix3x3(
    // 2.071, -1.327, 0.207,
    // 0.365, 0.681, -0.045,
    // -0.049, -0.05, 1.188);

    public ICtCp(TransferCharacteristics transfer, Primaries primaries,
            int bitdepth) {
        this(transfer, primaries, bitdepth, FULL);
    }

    public ICtCp(TransferCharacteristics transfer, Primaries primaries,
            int bitdepth, ColorRange range) {
        super(14, primaries);

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

        RGBtoLMS = new Matrix3x3(
                1688, 2146, 262,
                683, 2951, 462,
                99, 309, 3688)
                        .multiply(1.0 / 4096);

        LMStoRGB = RGBtoLMS.invert();

        PQLMStoITP = new Matrix3x3(
                2048, 2048, 0,
                6610, -13613, 7003,
                17933, -17390, -543)
                        .multiply(1.0 / 4096);

        ITPtoPQLMS = PQLMStoITP.invert();
    }

    /** Input is linear RGB. Output is nonlinear ITP. */
    @Override
    public void fromRGB(double[] rgb, double[] itp) {
        RGBtoLMS.multiply(rgb, itp);
        transfer.oetf(itp, itp); // L'M'S'
        PQLMStoITP.multiply(itp, itp);
    }

    @Override
    public void toRGB(double[] itp, double[] rgb) {
        ITPtoPQLMS.multiply(itp, rgb);
        transfer.eotf(rgb, rgb); // LMS
        LMStoRGB.multiply(rgb, rgb);
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
