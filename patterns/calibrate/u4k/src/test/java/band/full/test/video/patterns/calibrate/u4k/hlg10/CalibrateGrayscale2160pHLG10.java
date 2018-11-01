package band.full.test.video.patterns.calibrate.u4k.hlg10;

import static band.full.test.video.encoder.EncoderParameters.HLG10;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateGrayscaleBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateGrayscale2160pHLG10 extends CalibrateGrayscaleBase {
    public CalibrateGrayscale2160pHLG10() {
        super(HEVC, HLG10, "UHD4K/HLG10", "U4K_HLG10");
    }
}
