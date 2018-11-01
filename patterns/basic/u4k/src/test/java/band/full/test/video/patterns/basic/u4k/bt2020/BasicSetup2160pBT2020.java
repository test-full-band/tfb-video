package band.full.test.video.patterns.basic.u4k.bt2020;

import static band.full.test.video.encoder.EncoderParameters.UHD4K_MAIN10;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.BasicSetupBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pBT2020 extends BasicSetupBase {
    public BasicSetup2160pBT2020() {
        super(HEVC, UHD4K_MAIN10, "UHD4K/BT2020_10", "U4K_2020");
    }
}
