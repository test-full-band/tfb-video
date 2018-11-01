package band.full.video.itu.h265;

import band.full.video.itu.nal.NalContext;

/**
 * @author Igor Malinin
 */
public class H265Context implements NalContext {
    // active parameter sets
    public VPS vps;
    public SPS sps;

    public StRefPicSet[] st_ref_pic_set;

    public int NumDeltaPocs(int index) {
        var ref = st_ref_pic_set[index];
        return ref.num_negative_pics + ref.num_positive_pics;
    }
}
