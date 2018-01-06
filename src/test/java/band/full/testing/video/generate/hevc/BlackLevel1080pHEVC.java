package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.generate.GeneratorFactory.AVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.BlackLevelBase;

import org.junit.jupiter.api.Test;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class BlackLevel1080pHEVC extends BlackLevelBase {
    @Test
    public void generate() {
        generate("HEVC/UHD4K/HDR10/Calibrate/Basic/BlackLevel2160pHEVC",
                AVC, FULLHD_MAIN8);
    }
}
