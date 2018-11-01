package band.full.test.video.patterns.calman.fhd;

import static band.full.test.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.test.video.executor.GenerateVideo.Type.MAIN;
import static band.full.test.video.generator.GeneratorFactory.AVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateSaturationBase_CalMAN;

/**
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class CalibrateSaturation1080p_CalMAN
        extends CalibrateSaturationBase_CalMAN {
    public CalibrateSaturation1080p_CalMAN() {
        super(AVC, FULLHD_MAIN8, "FullHD", "1080p");
    }
}
