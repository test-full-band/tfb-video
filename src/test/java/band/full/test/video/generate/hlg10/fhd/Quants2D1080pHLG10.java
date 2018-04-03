package band.full.test.video.generate.hlg10.fhd;

import static band.full.core.Resolution.STD_1080p;
import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.buffer.Framerate.FPS_23_976;
import static band.full.video.itu.BT2100.HLG10;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants2DBase10HDR;
import band.full.video.encoder.EncoderParameters;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2D1080pHLG10 extends Quants2DBase10HDR {
    public Quants2D1080pHLG10() {
        super(HEVC, new EncoderParameters(STD_1080p, HLG10, FPS_23_976),
                "UHD4K/HLG10/Quants2D", "U4K_HLG10");
    }
}
