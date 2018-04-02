package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.PPS_NUT;

public class PPS extends NALUnit {
    public PPS() {
        super(PPS_NUT);
    }
}
