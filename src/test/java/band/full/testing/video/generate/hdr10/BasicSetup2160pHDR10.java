package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.BT2111Generator;
import band.full.testing.video.generate.base.BasicSetupBase;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pHDR10 extends BasicSetupBase {
    public BasicSetup2160pHDR10() {
        super(HEVC, HDR10, "UHD4K/HDR10/Calibrate/Basic");
    }

    @Test
    public void bt2111() {
        new BT2111Generator(factory, params, folder, "").generate();
    }
}
