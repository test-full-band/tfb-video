package band.full.test.video.generate.avc;

import static band.full.test.video.executor.GenerateVideo.Type.MAIN;
import static band.full.test.video.generator.GeneratorFactory.AVC;
import static band.full.video.encoder.EncoderParameters.FULLHD_MAIN8;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants3DBase;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class Quants3D1080p extends Quants3DBase {
    public Quants3D1080p() {
        super(AVC, FULLHD_MAIN8, "FullHD/Quants3D", "1080p");
    }
}
