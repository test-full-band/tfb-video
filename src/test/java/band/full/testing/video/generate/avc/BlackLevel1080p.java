package band.full.testing.video.generate.avc;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;
import static band.full.testing.video.generate.GeneratorFactory.AVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.BlackLevelBase;

import org.junit.jupiter.api.Test;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class BlackLevel1080p extends BlackLevelBase {
    @Test
    public void generate() {
        generate("AVC/FullHD/Calibrate/Basic/BlackLevel1080p",
                AVC, FULLHD_MAIN8);
    }
}
