package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.generator.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generator.CalibrationBase;

/**
 * Calibration box fills.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Calibrate2160pHDR10 extends CalibrationBase {
    public Calibrate2160pHDR10() {
        super(HEVC, HDR10, "UHD4K/HDR10/Calibrate", "U4K_HDR10");
    }
}
