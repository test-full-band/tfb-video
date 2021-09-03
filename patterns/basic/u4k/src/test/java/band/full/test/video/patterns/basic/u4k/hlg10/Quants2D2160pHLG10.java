package band.full.test.video.patterns.basic.u4k.hlg10;

import static band.full.test.video.encoder.EncoderParameters.HLG10;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants2DBase10HDR;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2D2160pHLG10 extends Quants2DBase10HDR {
    public Quants2D2160pHLG10() {
        super(HEVC, HLG10, "UHD4K/HLG10", "U4K_HLG10");
    }
}
