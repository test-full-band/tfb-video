package band.full.test.video.patterns.basic.fhd;

import static band.full.test.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants3DBase;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants3D1080pHEVC extends Quants3DBase {
    public Quants3D1080pHEVC() {
        super(HEVC, FULLHD_MAIN8, "FullHD/BT709", "FHD");
    }
}
