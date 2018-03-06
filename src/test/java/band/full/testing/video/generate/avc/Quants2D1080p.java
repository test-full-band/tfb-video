package band.full.testing.video.generate.avc;

import static band.full.testing.video.core.Resolution.STD_720p;
import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.executor.FxImage.amplify;
import static band.full.testing.video.executor.FxImage.transform;
import static band.full.testing.video.executor.FxImage.truncate;
import static band.full.testing.video.executor.FxImage.write;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;
import static band.full.testing.video.generate.GeneratorFactory.AVC;

import band.full.testing.video.core.FrameBuffer;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.Quants2DBase8;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class Quants2D1080p extends Quants2DBase8 {
    public Quants2D1080p() {
        super(AVC, FULLHD_MAIN8, "FullHD/Quantization", "FHD");
    }

    @Test
    public void image() throws IOException {
        FrameBuffer fb = new FrameBuffer(STD_720p, FULLHD_MAIN8.matrix);
        generate(fb, new Args("PNG", 16, true));
        write(fb, "Quants.png", rgb -> transform(amplify(rgb, 6.0)));
    }

    @Test
    public void image5bit() throws IOException {
        FrameBuffer fb = new FrameBuffer(STD_720p, FULLHD_MAIN8.matrix);
        generate(fb, new Args("PNG", 16, true));

        write(fb, "Quants5bit.png",
                rgb -> transform(amplify(truncate(rgb, 32), 6.0)));
    }

    @Test
    public void image6bit() throws IOException {
        FrameBuffer fb = new FrameBuffer(STD_720p, FULLHD_MAIN8.matrix);
        generate(fb, new Args("PNG", 16, true));

        write(fb, "Quants6bit.png",
                rgb -> transform(amplify(truncate(rgb, 64), 6.0)));
    }

    @Test
    public void image7bit() throws IOException {
        FrameBuffer fb = new FrameBuffer(STD_720p, FULLHD_MAIN8.matrix);
        generate(fb, new Args("PNG", 16, true));

        write(fb, "Quants7bit.png",
                rgb -> transform(amplify(truncate(rgb, 128), 6.0)));
    }

    @Test
    public void image8bit() throws IOException {
        FrameBuffer fb = new FrameBuffer(STD_720p, FULLHD_MAIN8.matrix);
        generate(fb, new Args("PNG", 16, true));

        write(fb, "Quants8bit.png",
                rgb -> transform(amplify(truncate(rgb, 256), 6.0)));
    }
}
