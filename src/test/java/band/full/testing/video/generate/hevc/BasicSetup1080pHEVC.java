package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.GeneratorBase;
import band.full.testing.video.generate.basic.BasicSetupBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup1080pHEVC extends BasicSetupBase {
    @Override
    public void generate(GeneratorBase generator, String fileName) {
        generator.generate("HEVC/FullHD/Calibrate/Basic/" + fileName,
                HEVC, FULLHD_MAIN8);
    }
}