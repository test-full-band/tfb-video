package band.full.test.video.generate.avc;

import static band.full.test.video.executor.GenerateVideo.Type.MAIN;
import static band.full.test.video.generator.GeneratorFactory.AVC;
import static band.full.video.encoder.EncoderParameters.FULLHD_MAIN8;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateColorCheckerBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class CalibrateColorChecker1080p extends CalibrateColorCheckerBase {
    public CalibrateColorChecker1080p() {
        super(AVC, FULLHD_MAIN8, "FullHD", "1080p");
    }
}
