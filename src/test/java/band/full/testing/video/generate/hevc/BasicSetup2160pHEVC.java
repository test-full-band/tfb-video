package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.UHD4K_MAIN8;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.BasicSetupBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pHEVC extends BasicSetupBase {
    public BasicSetup2160pHEVC() {
        super(HEVC, UHD4K_MAIN8, "UHD4K/BT709/Calibrate/Basic");
    }
}
