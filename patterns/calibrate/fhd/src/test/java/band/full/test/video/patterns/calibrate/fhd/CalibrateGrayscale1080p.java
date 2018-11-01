package band.full.test.video.patterns.calibrate.fhd;

import static band.full.test.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.test.video.executor.GenerateVideo.Type.MAIN;
import static band.full.test.video.generator.GeneratorFactory.AVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateGrayscaleBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class CalibrateGrayscale1080p extends CalibrateGrayscaleBase {
    public CalibrateGrayscale1080p() {
        super(AVC, FULLHD_MAIN8, "FullHD", "1080p");
    }
}
