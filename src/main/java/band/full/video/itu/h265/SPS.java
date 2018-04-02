package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.SPS_NUT;

public class SPS extends NALUnit {
    public SPS() {
        super(SPS_NUT);
    }
}
