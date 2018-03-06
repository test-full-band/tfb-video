package band.full.testing.video.generate.hlg10;

import static band.full.testing.video.encoder.EncoderParameters.HLG10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.BT2111Generator;
import band.full.testing.video.generate.base.BasicSetupBase;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pHLG10 extends BasicSetupBase {
    public BasicSetup2160pHLG10() {
        super(HEVC, HLG10, "UHD4K/HLG10/Calibrate/Basic");
    }

    @Test
    public void bt2111() {
        new BT2111Generator(factory, params, folder, "").generate();
    }
}
