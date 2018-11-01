package band.full.test.video.patterns.basic.fhd;

import static band.full.test.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.BasicSetupBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup1080pHEVC extends BasicSetupBase {
    public BasicSetup1080pHEVC() {
        super(HEVC, FULLHD_MAIN8, "FullHD/BT709", "FHD");
    }
}
