package band.full.testing.video.generate.hevc.u4k2020;

import static band.full.testing.video.core.Resolution.STD_1080p;
import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.generator.GeneratorFactory.HEVC;

import band.full.testing.video.executor.FxDisplay;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generator.CalibrationBase;

/**
 * Calibration box fills.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Calibrate2160pBT2020 extends CalibrationBase {
    public Calibrate2160pBT2020() {
        super(HEVC, FULLHD_MAIN8, "UHD4K/BT2020_10/Calibrate", "U4K_2020");
    }

    public static void main(String[] args) {
        var instance = new Calibrate2160pBT2020();
        var gray = new Args("File", "SN", "Set", "Label", 10, 0, 512, 512);
        FxDisplay.show(STD_1080p, () -> instance.overlay(gray));
    }
}
