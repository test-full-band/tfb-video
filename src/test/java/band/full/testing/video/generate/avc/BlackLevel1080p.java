package band.full.testing.video.generate.avc;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;

import band.full.testing.video.encoder.EncoderAVC;
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
        EncoderAVC.encode(
                "AVC/FullHD/Calibrate/Basic/BlackLevel1080p",
                FULLHD_MAIN8, this::encode, this::verify);
    }
}
