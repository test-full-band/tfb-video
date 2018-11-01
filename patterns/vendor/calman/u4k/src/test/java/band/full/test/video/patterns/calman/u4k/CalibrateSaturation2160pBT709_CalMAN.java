package band.full.test.video.patterns.calman.u4k;

import static band.full.test.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateSaturationBase_CalMAN;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateSaturation2160pBT709_CalMAN
        extends CalibrateSaturationBase_CalMAN {
    public CalibrateSaturation2160pBT709_CalMAN() {
        super(HEVC, FULLHD_MAIN8, "UHD4K/BT709", "U4K");
    }
}
