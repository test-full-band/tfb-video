package band.full.video.itu;

import static band.full.video.itu.ColorRange.FULL;

import band.full.core.color.Matrix3x3;
import band.full.core.color.Primaries;

/**
 * Encoding/decoding of <em>RGB</em> values to/from
 * <em>IC<sub>T</sub>C<sub>P</sub></em> color space.
 *
 * @author Igor Malinin
 */
public final class ICtCp extends ColorMatrix {
    public static final Matrix3x3 RGBtoLMS = new Matrix3x3(
            1688, 2146, 262,
            683, 2951, 462,
            99, 309, 3688).multiply(1.0 / 4096);

    public static final Matrix3x3 LMStoRGB = RGBtoLMS.invert();

    public static final Matrix3x3 PQLMStoITP = new Matrix3x3(
            2048, 2048, 0,
            6610, -13613, 7003,
            17933, -17390, -543).multiply(1.0 / 4096);

    public static final Matrix3x3 ITPtoPQLMS = PQLMStoITP.invert();

    // TODO derive matrix from BT.2020 color space
    // private static final Matrix3x3 LMStoXYZ = new Matrix3x3(
    // 2.071, -1.327, 0.207,
    // 0.365, 0.681, -0.045,
    // -0.049, -0.05, 1.188);

    public ICtCp(TransferCharacteristics transfer, Primaries primaries,
            int bitdepth) {
        this(transfer, primaries, bitdepth, FULL);
    }

    public ICtCp(TransferCharacteristics transfer, Primaries primaries,
            int bitdepth, ColorRange range) {
        this(14, transfer, primaries, bitdepth, range);
    }

    public ICtCp(int code, TransferCharacteristics transfer,
            Primaries primaries, int bitdepth, ColorRange range) {
        super(code, transfer, primaries, bitdepth, range);
    }

    @Override
    public double[] fromRGB(double[] rgb, double[] itp) {
        return fromLinearRGB(transfer.toLinear(rgb, itp), itp);
    }

    @Override
    public double[] fromLinearRGB(double[] rgb, double[] itp) {
        RGBtoLMS.multiply(rgb, itp);
        transfer.fromLinear(itp, itp);
        return PQLMStoITP.multiply(itp, itp);
    }

    @Override
    public double[] toRGB(double[] itp, double[] rgb) {
        return transfer.fromLinear(toLinearRGB(itp, rgb), rgb);
    }

    @Override
    public double[] toLinearRGB(double[] itp, double[] rgb) {
        ITPtoPQLMS.multiply(itp, rgb);
        transfer.toLinear(rgb, rgb);
        return LMStoRGB.multiply(rgb, rgb);
    }

    public static void main(String[] args) {
        System.out.println(ITPtoPQLMS.toString());
    }
}
