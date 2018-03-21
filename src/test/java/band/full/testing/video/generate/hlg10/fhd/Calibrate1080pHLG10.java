package band.full.testing.video.generate.hlg10.fhd;

import static band.full.testing.video.core.Framerate.FPS_23_976;
import static band.full.testing.video.core.Resolution.STD_1080p;
import static band.full.testing.video.generator.GeneratorFactory.HEVC;
import static band.full.testing.video.itu.BT2100.HLG10;

import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.executor.FxDisplay;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generator.CalibrationBase;

/**
 * Calibration box fills.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Calibrate1080pHLG10 extends CalibrationBase {
    public Calibrate1080pHLG10() {
        super(HEVC, new EncoderParameters(STD_1080p, HLG10, FPS_23_976),
                "UHD4K/HLG10/Calibrate", "U4K_HLG10");
    }

    public static void main(String[] args) {
        var instance = new Calibrate1080pHLG10();
        var gray = new Args("X", "X", 10, "10", 512, 512, 512);
        FxDisplay.show(STD_1080p, () -> instance.overlay(gray));
    }
}
