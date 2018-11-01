package band.full.test.video.patterns.basic.hdr10;

import static band.full.test.video.encoder.EncoderParameters.HDR10;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants3DBase;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants3D2160pHDR10 extends Quants3DBase {
    public Quants3D2160pHDR10() {
        super(HEVC, HDR10, "UHD4K/HDR10/Quants3D", "U4K_HDR10");
    }
}
