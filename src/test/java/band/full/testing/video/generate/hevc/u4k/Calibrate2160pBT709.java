package band.full.testing.video.generate.hevc.u4k;

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
public class Calibrate2160pBT709 extends CalibrationBase {
    public Calibrate2160pBT709() {
        super(HEVC, FULLHD_MAIN8, "UHD4K/BT709/Calibrate", "U4K");
    }
}
