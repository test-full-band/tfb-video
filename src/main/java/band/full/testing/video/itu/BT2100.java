package band.full.testing.video.itu;

import static band.full.testing.video.itu.ColorRange.FULL;

import band.full.testing.video.color.Primaries;
import band.full.testing.video.smpte.ST2084;

/**
 * Image parameter values for high dynamic range television for use in
 * production and international programme exchange
 *
 * @author Igor Malinin
 * @see <a href=
 *      "https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.2100-1-201706-I!!PDF-E.pdf">
 *      Rec. ITU-R BT.2100-1 (06/2017)</a>
 * @see <a href=
 *      "https://www.dolby.com/us/en/technologies/dolby-vision/ICtCp-white-paper.pdf">
 *      White paper: What is ICtCp? - Introduction</a>
 */
public class BT2100 {
    public static final Primaries PRIMARIES = BT2020.PRIMARIES;
    public static final ST2084 PQ = ST2084.PQ;
    public static final HybridLogGamma HLG = HybridLogGamma.HLG;

    public static final YCbCr HLG10 = new YCbCr(9, HLG, PRIMARIES, 10);
    public static final YCbCr HLG12 = new YCbCr(9, HLG, PRIMARIES, 12);

    public static final ICtCp HLG10ITP = new ICtCp(HLG, PRIMARIES, 10);
    public static final ICtCp HLG12ITP = new ICtCp(HLG, PRIMARIES, 12);

    public static final YCbCr PQ10 = new YCbCr(9, PQ, PRIMARIES, 10);
    public static final YCbCr PQ12 = new YCbCr(9, PQ, PRIMARIES, 12);

    public static final YCbCr PQ10FR = new YCbCr(9, PQ, PRIMARIES, 10, FULL);
    public static final YCbCr PQ12FR = new YCbCr(9, PQ, PRIMARIES, 12, FULL);

    public static final ICtCp PQ10ITP = new ICtCp(PQ, PRIMARIES, 10);
    public static final ICtCp PQ12ITP = new ICtCp(PQ, PRIMARIES, 12);
}
