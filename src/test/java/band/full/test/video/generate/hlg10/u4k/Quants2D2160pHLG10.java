package band.full.test.video.generate.hlg10.u4k;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.HLG10;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants2DBase10;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2D2160pHLG10 extends Quants2DBase10 {
    public Quants2D2160pHLG10() {
        super(HEVC, HLG10, "UHD4K/HLG10/Quants2D", "U4K_HLG10");
    }
}
