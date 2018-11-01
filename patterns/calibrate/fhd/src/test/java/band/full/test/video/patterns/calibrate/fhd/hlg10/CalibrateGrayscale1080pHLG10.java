package band.full.test.video.patterns.calibrate.fhd.hlg10;

import static band.full.core.Resolution.STD_1080p;
import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.buffer.Framerate.FPS_23_976;
import static band.full.video.itu.BT2100.HLG10;

import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateGrayscaleBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateGrayscale1080pHLG10 extends CalibrateGrayscaleBase {
    public CalibrateGrayscale1080pHLG10() {
        super(HEVC, new EncoderParameters(STD_1080p, HLG10, FPS_23_976),
                "FullHD/HLG10", "FHD_HLG10");
    }
}
