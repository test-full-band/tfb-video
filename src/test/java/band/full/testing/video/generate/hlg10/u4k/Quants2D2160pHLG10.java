package band.full.testing.video.generate.hlg10.u4k;

import static band.full.testing.video.encoder.EncoderParameters.HLG10;
import static band.full.testing.video.generator.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generator.Quants2DBase10;

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
