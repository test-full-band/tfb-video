package band.full.test.video.generate.hevc.fhd;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.FULLHD_MAIN8;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.BasicSetupBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup1080pHEVC extends BasicSetupBase {
    public BasicSetup1080pHEVC() {
        super(HEVC, FULLHD_MAIN8, "FullHD/Calibrate/Basic", "FHD");
    }
}
