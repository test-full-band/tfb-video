package band.full.test.video.generate.avc;

import static band.full.test.video.executor.GenerateVideo.Type.MAIN;
import static band.full.test.video.generator.GeneratorFactory.AVC;
import static band.full.video.encoder.EncoderParameters.FULLHD_MAIN8;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrationBase;

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
