package band.full.test.video.patterns.basic.u4k.hdr10;

import static band.full.test.video.encoder.EncoderParameters.HDR10;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants2DBase10HDR;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2D2160pHDR10 extends Quants2DBase10HDR {
    public Quants2D2160pHDR10() {
        super(HEVC, HDR10, "UHD4K/HDR10", "U4K_HDR10");
    }
}
