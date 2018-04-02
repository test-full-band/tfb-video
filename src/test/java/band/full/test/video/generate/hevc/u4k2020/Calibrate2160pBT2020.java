package band.full.test.video.generate.hevc.u4k2020;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.UHD4K_MAIN10;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrationBase;

/**
 * Calibration box fills.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Calibrate2160pBT2020 extends CalibrationBase {
    public Calibrate2160pBT2020() {
        super(HEVC, UHD4K_MAIN10, "UHD4K/BT2020_10/Calibrate", "U4K_2020");
    }
}
