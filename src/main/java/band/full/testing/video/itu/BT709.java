package band.full.testing.video.itu;

import static band.full.testing.video.color.Primaries.sRGB;

import band.full.testing.video.color.Primaries;

/**
 * Parameter values for the HDTV standards for production and international
 * programme exchange
 *
 * @author Igor Malinin
 * @see <a href=
 *      "http://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.709-6-201506-I!!PDF-E.pdf">
 *      Recommendation ITU-R BT.709-6 (06/2015)</a>
 */
public class BT709 {
    /**
     * <ul>
     * ITU-T colour_primaries = 1
     * <li><strong>Rec. ITU-R BT.709-6</strong>
     * <li>Rec. ITU-R BT.1361-0 conventional colour gamut system <br>
     * and extended colour gamut system (historical)
     * <li>IEC 61966-2-1 sRGB or sYCC IEC 61966-2-4
     * <li>Annex B of SMPTE RP 177 (1993)
     * </ul>
     */
    public static final Primaries PRIMARIES = sRGB;

    /**
     * BT.709 only defines low resolution rounded values, use precise definition
     * of the same formula instead as defined in BT.2020.
     * <p>
     * transfer_characteristics = 1
     * <ul>
     * <li>Rec. ITU-R BT.709-6
     * <li>Rec. ITU-R BT.1361-0 conventional colour gamut system (historical)
     * <br/>
     * (functionally the same as the values 6, 14 and 15)
     * </ul>
     */
    public static final TransferCharacteristics TRANSFER = new Gamma(1);

    /**
     * <p>
     * matrix_coeffs = 1
     * <ul>
     * <li>ITU-R Rec. BT.709-6
     * <li>ITU-R Rec. BT.1361-0 conventional colour gamut system <br>
     * and extended colour gamut system (historical)
     * <li>IEC 61966-2-1 sYCC
     * <li>IEC 61966-2-4 xvYCC709 SMPTE RP 177 (1993) Annex B
     * <li>See Equations E-28 to E-30
     * </ul>
     */
    public static YCbCr BT709_8bit = new YCbCr(1, TRANSFER, PRIMARIES, 8);

    /**
     * <p>
     * matrix_coeffs = 1
     * <ul>
     * <li>ITU-R Rec. BT.709-6
     * <li>ITU-R Rec. BT.1361-0 conventional colour gamut system <br>
     * and extended colour gamut system (historical)
     * <li>IEC 61966-2-1 sYCC
     * <li>IEC 61966-2-4 xvYCC709 SMPTE RP 177 (1993) Annex B
     * <li>See Equations E-28 to E-30
     * </ul>
     */
    public static YCbCr BT709_10bit = new YCbCr(1, TRANSFER, PRIMARIES, 10);

    private BT709() {}
}
