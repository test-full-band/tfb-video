package band.full.testing.video.generate.avc;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;
import static band.full.testing.video.generator.GeneratorFactory.AVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generator.Quants2DBase8;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class Quants2D1080p extends Quants2DBase8 {
    public Quants2D1080p() {
        super(AVC, FULLHD_MAIN8, "FullHD/Quants2D", "1080p");
    }
}
