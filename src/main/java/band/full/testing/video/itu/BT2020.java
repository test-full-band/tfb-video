package band.full.testing.video.itu;

import band.full.testing.video.color.CIExy;
import band.full.testing.video.color.Primaries;

/**
 * Parameter values for ultra-high definition television systems for production
 * and international programme exchange
 *
 * @author Igor Malinin
 * @see <a href=
 *      "https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.2020-2-201510-I!!PDF-E.pdf">
 *      Rec. ITU-R BT.2020-2 (10/2015)</a>
 */
public class BT2020 {
    public static final CIExy CIE_R = new CIExy(0.708, 0.292);
    public static final CIExy CIE_G = new CIExy(0.170, 0.797);
    public static final CIExy CIE_B = new CIExy(0.131, 0.046);

    // D65 values are rounded per BT.2020
    public static final CIExy CIE_D65 = new CIExy(0.3127, 0.3290);

    /**
     * <p>
     * ITU-T colour_primaries = 9
     * <ul>
     * <li>Rec. ITU-R BT.2020-2
     * <li>Rec. ITU-R BT.2100-0
     * </ul>
     */
    public static Primaries PRIMARIES =
            new Primaries(9, CIE_R, CIE_G, CIE_B, CIE_D65);

    /**
     * Uses precise definition of the formula.
     * <p>
     * transfer_characteristics = 14
     * <ul>
     * <li>Rec. ITU-R BT.2020-2<br/>
     * (functionally the same as the values 1, 6 and 15)
     * </ul>
     */
    public static final TransferCharacteristics TRANSFER_10bit = new Gamma(14);

    /**
     * Uses precise definition of the formula.
     * <p>
     * transfer_characteristics = 15
     * <ul>
     * <li>Rec. ITU-R BT.2020-2<br/>
     * (functionally the same as the values 1, 6 and 14)
     * </ul>
     */
    public static final TransferCharacteristics TRANSFER_12bit = new Gamma(15);

    /**
     * <p>
     * matrix_coeffs = 9
     * <ul>
     * <li>Rec. ITU-R BT.2020-2 non-constant luminance system
     * <li>See Equations E-28 to E-30
     * </ul>
     */
    public static YCbCr BT2020_10bit = new YCbCr(9, PRIMARIES, 10);
    public static YCbCr BT2020_12bit = new YCbCr(9, PRIMARIES, 12);

    private BT2020() {}
}
