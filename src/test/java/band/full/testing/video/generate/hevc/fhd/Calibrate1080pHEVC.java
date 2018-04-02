package band.full.testing.video.generate.hevc.fhd;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.generator.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generator.CalibrationBase;

/**
 * Calibration box fills.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Calibrate1080pHEVC extends CalibrationBase {
    public Calibrate1080pHEVC() {
        super(HEVC, FULLHD_MAIN8, "FullHD/Calibrate", "FHD");
    }
}
