package band.full.testing.video.generate.hevc.fhd;

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
public class Calibrate1080pHEVC extends CalibrationBase {
    public Calibrate1080pHEVC() {
        super(HEVC, FULLHD_MAIN8, "FullHD/Calibrate", "FHD");
    }

    public static void main(String[] args) {
        var instance = new Calibrate1080pHEVC();
        var gray = new Args("File", "SN", "Set", "Label", 10, 128, 128, 128);
        FxDisplay.show(STD_1080p, () -> instance.overlay(gray));
    }
}
