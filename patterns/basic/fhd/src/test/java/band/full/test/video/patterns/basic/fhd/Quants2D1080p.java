package band.full.test.video.patterns.basic.fhd;

import static band.full.test.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.test.video.executor.GenerateVideo.Type.MAIN;
import static band.full.test.video.generator.GeneratorFactory.AVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants2DBase8;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class Quants2D1080p extends Quants2DBase8 {
    public Quants2D1080p() {
        super(AVC, FULLHD_MAIN8, "FullHD", "1080p");
    }
}
