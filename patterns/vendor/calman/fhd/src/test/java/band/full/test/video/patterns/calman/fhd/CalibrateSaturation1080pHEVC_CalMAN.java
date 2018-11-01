package band.full.test.video.patterns.calman.fhd;

import static band.full.test.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

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
