package band.full.testing.video.generate.avc;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;
import static band.full.testing.video.generator.GeneratorFactory.AVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generator.CalibrationBase;

/**
 * Calibration box fills.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class Calibrate1080p extends CalibrationBase {
    public Calibrate1080p() {
        super(AVC, FULLHD_MAIN8, "FullHD/Calibrate", "1080p");
    }
}
