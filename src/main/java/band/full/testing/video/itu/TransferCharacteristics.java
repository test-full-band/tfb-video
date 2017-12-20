package band.full.testing.video.itu;

import band.full.testing.video.color.EOTF;
import band.full.testing.video.color.OETF;

public interface TransferCharacteristics extends EOTF, OETF {
    int code();
}
