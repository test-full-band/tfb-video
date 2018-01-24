package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.GeneratorBase;
import band.full.testing.video.generate.basic.BasicSetupBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pHDR10 extends BasicSetupBase {
    @Override
    public void generate(GeneratorBase generator, String fileName) {
        generator.generate("HEVC/UHD4K/HDR10/Calibrate/Basic/" + fileName,
                HEVC, HDR10);
    }
}
