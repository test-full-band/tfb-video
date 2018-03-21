package band.full.testing.video.generate.hevc.fhd;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.generator.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generator.Quants2DBase8;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2D1080pBT709 extends Quants2DBase8 {
    public Quants2D1080pBT709() {
        super(HEVC, FULLHD_MAIN8, "FullHD/Quants2D", "U4K");
    }
}
