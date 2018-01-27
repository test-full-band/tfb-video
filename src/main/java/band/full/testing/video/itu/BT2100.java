package band.full.testing.video.itu;

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
    public static final TransferCharacteristics PQ = ST2084.PQ;

    public static final ICtCp ITP10_PQ = new ICtCp(PQ, PRIMARIES, 10);
    public static final ICtCp ITP12_PQ = new ICtCp(PQ, PRIMARIES, 12);
}
