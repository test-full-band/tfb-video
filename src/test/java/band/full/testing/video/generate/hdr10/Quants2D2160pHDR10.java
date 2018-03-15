package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.Quants2DBase10;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2D2160pHDR10 extends Quants2DBase10 {
    public Quants2D2160pHDR10() {
        super(HEVC, HDR10, "UHD4K/HDR10/Quants2D", "U4K_HDR10");
    }
}
