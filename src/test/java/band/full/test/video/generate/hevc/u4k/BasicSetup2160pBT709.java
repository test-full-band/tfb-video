package band.full.test.video.generate.hevc.u4k;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.UHD4K_MAIN8;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.BasicSetupBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pBT709 extends BasicSetupBase {
    public BasicSetup2160pBT709() {
        super(HEVC, UHD4K_MAIN8, "UHD4K/BT709/Calibrate/Basic", "U4K");
    }
}
