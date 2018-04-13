package band.full.test.video.generate.hevc.fhd;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.FULLHD_MAIN8;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateColorCheckerBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateColorChecker1080pHEVC extends CalibrateColorCheckerBase {
    public CalibrateColorChecker1080pHEVC() {
        super(HEVC, FULLHD_MAIN8, "FullHD/BT709", "FHD");
    }
}
