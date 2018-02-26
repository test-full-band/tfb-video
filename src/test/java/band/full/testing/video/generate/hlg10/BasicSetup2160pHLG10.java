package band.full.testing.video.generate.hlg10;

import static band.full.testing.video.encoder.EncoderParameters.HLG10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.basic.BasicSetupBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pHLG10 extends BasicSetupBase {
    public BasicSetup2160pHLG10() {
        super(HEVC, HLG10, "UHD4K/HLG10/Calibrate/Basic");
    }
}
