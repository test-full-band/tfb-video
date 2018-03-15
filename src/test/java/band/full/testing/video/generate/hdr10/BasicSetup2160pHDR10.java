package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.BasicSetupHDRBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pHDR10 extends BasicSetupHDRBase {
    public BasicSetup2160pHDR10() {
        super(HEVC, HDR10, "UHD4K/HDR10/Calibrate/Basic", "U4K_HDR10");
    }
}
