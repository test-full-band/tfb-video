package band.full.test.video.patterns.calibrate.fhd;

import static band.full.test.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

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
