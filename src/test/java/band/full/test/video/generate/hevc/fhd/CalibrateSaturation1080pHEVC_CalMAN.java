package band.full.test.video.generate.hevc.fhd;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.FULLHD_MAIN8;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateSaturationBase_CalMAN;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateSaturation1080pHEVC_CalMAN
        extends CalibrateSaturationBase_CalMAN {
    public CalibrateSaturation1080pHEVC_CalMAN() {
        super(HEVC, FULLHD_MAIN8, "FullHD/BT709", "FHD");
    }
}
