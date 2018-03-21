package band.full.testing.video.generate.hevc.fhd;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.generator.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generator.BasicSetupBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup1080pHEVC extends BasicSetupBase {
    public BasicSetup1080pHEVC() {
        super(HEVC, FULLHD_MAIN8, "FullHD/Calibrate/Basic", "FHD");
    }
}
