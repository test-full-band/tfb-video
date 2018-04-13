package band.full.test.video.generate.hevc.u4k;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.FULLHD_MAIN8;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateColorCheckerBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateColorChecker2160pBT709 extends CalibrateColorCheckerBase {
    public CalibrateColorChecker2160pBT709() {
        super(HEVC, FULLHD_MAIN8, "UHD4K/BT709", "U4K");
    }
}
