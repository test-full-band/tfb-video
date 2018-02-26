package band.full.testing.video.generate.avc;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;
import static band.full.testing.video.generate.GeneratorFactory.AVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.basic.BasicSetupBase;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class BasicSetup1080p extends BasicSetupBase {
    public BasicSetup1080p() {
        super(AVC, FULLHD_MAIN8, "FullHD/Calibrate/Basic");
    }
}
