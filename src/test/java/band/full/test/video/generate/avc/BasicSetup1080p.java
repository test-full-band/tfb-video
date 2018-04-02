package band.full.test.video.generate.avc;

import static band.full.test.video.executor.GenerateVideo.Type.MAIN;
import static band.full.test.video.generator.GeneratorFactory.AVC;
import static band.full.video.encoder.EncoderParameters.FULLHD_MAIN8;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.BasicSetupBase;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class BasicSetup1080p extends BasicSetupBase {
    public BasicSetup1080p() {
        super(AVC, FULLHD_MAIN8, "FullHD/Calibrate/Basic", "1080p");
    }
}
