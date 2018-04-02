package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.IDR_W_RADL;

public class IDR extends NALUnit {
    public IDR() {
        super(IDR_W_RADL);
    }
}
