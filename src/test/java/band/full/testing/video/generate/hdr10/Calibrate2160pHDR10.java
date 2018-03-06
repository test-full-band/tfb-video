package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Resolution.STD_1080p;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.FxDisplay;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.CalibrationBase;

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

    public static void main(String[] args) {
        Calibrate2160pHDR10 instance = new Calibrate2160pHDR10();
        Args gray = new Args("X", "X", 10, "10", 512, 512, 512);
        FxDisplay.show(STD_1080p, () -> instance.overlay(gray));
    }
}
