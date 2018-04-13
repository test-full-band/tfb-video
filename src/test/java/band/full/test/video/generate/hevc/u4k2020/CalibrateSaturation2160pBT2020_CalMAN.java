package band.full.test.video.generate.hevc.u4k2020;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.UHD4K_MAIN10;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateSaturationBase_CalMAN;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateSaturation2160pBT2020_CalMAN
        extends CalibrateSaturationBase_CalMAN {
    public CalibrateSaturation2160pBT2020_CalMAN() {
        super(HEVC, UHD4K_MAIN10, "UHD4K/BT2020_10", "U4K_2020");
    }
}
