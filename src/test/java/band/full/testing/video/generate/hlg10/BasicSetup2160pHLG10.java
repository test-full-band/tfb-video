package band.full.testing.video.generate.hlg10;

import static band.full.testing.video.encoder.EncoderParameters.HLG10;
import static band.full.testing.video.encoder.EncoderParameters.HLG10ITP;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.GeneratorBase;
import band.full.testing.video.generate.basic.BasicSetupBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pHLG10 extends BasicSetupBase {
    @Override
    public void generate(GeneratorBase generator, String fileName) {
        generator.generate("HEVC/UHD4K/HLG10/Calibrate/Basic/" + fileName,
                HEVC, HLG10);

        generator.generate("HEVC/UHD4K/HLG10ITP/Calibrate/Basic/" + fileName,
                HEVC, HLG10ITP);
    }
}
