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
public class BT2020 extends YCbCr {
    public static final CIExy CIE_R = new CIExy(0.708, 0.292);
    public static final CIExy CIE_G = new CIExy(0.170, 0.797);
    public static final CIExy CIE_B = new CIExy(0.131, 0.046);

    // D65 values are rounded per BT.2020
    public static final CIExy CIE_D65 = new CIExy(0.3127, 0.3290);

    public static Primaries PRIMARIES =
            new Primaries(CIE_R, CIE_G, CIE_B, CIE_D65);

    public static BT2020 BT2020_10bit = new BT2020(10);
    public static BT2020 BT2020_12bit = new BT2020(12);

    private BT2020(int bitdepth) {
        super(bitdepth, PRIMARIES);
    }
}
