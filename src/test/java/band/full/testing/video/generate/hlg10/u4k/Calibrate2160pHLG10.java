package band.full.testing.video.generate.hlg10.u4k;

import static band.full.testing.video.core.Resolution.STD_1080p;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
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
public class Calibrate2160pHLG10 extends CalibrationBase {
    public Calibrate2160pHLG10() {
        super(HEVC, HDR10, "UHD4K/HLG10/Calibrate", "U4K_HLG10");
    }

    public static void main(String[] args) {
        var instance = new Calibrate2160pHLG10();
        var gray = new Args("File", "SN", "Set", "Label", 10, 0, 512, 512);
        FxDisplay.show(STD_1080p, () -> instance.overlay(gray));
    }
}
