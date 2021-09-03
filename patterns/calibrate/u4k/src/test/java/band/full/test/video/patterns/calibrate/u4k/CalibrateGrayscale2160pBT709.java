package band.full.test.video.patterns.calibrate.u4k;

import static band.full.test.video.encoder.EncoderParameters.UHD4K_MAIN8;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateGrayscaleBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateGrayscale2160pBT709 extends CalibrateGrayscaleBase {
    public CalibrateGrayscale2160pBT709() {
        super(HEVC, UHD4K_MAIN8, "UHD4K/BT709", "U4K");
    }
}
