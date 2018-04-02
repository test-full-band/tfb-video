package band.full.testing.video.generate.png;

import static band.full.testing.video.encoder.EncoderParameters.HD_MAIN;
import static band.full.testing.video.executor.FxImage.amplify;
import static band.full.testing.video.executor.FxImage.round;
import static band.full.testing.video.executor.FxImage.transform;
import static band.full.testing.video.executor.FxImage.write;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;

import band.full.testing.video.core.Dither;
import band.full.testing.video.core.FrameBuffer;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generator.Quants2DBase;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Generates amplified example PNGs for testing color bands separation /
 * quantization step uniformity patterns.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class Quants2DPNG extends Quants2DBase {
    private final double AMP = 4.0; // intensity amplification factor

    public Quants2DPNG() {
        super(null, HD_MAIN, null, null);
    }

    @Test
    public void image() throws IOException {
        var fb = newFrameBuffer();
        generate(fb, new Args("PNG", 16, true));

        write(fb, "Quants8to10.png",
                rgb -> transform(amplify(rgb, AMP)));
    }

    @Test
    public void image5round() throws IOException {
        var fb = newFrameBuffer();
        generate(fb, new Args("PNG", 16, true));

        write(fb, "Quants5round.png",
                rgb -> transform(amplify(round(rgb, 32), AMP)));
    }

    @Test
    public void image6round() throws IOException {
        var fb = newFrameBuffer();
        generate(fb, new Args("PNG", 16, true));

        write(fb, "Quants6round.png",
                rgb -> transform(amplify(round(rgb, 64), AMP)));
    }

    @Test
    public void image7round() throws IOException {
        var fb = newFrameBuffer();
        generate(fb, new Args("PNG", 16, true));

        write(fb, "Quants7round.png",
                rgb -> transform(amplify(round(rgb, 128), AMP)));
    }

    @Test
    public void image8round() throws IOException {
        var fb = newFrameBuffer();
        generate(fb, new Args("PNG", 16, true));

        write(fb, "Quants8round.png",
                rgb -> transform(amplify(round(rgb, 256), AMP)));
    }

    @Test
    public void image8tpdf() throws IOException {
        var dither = new Dither.TPDF();
        var fb = newFrameBuffer();
        generate(fb, new Args("PNG", 16, true));

        write(fb, "Quants8tpdf.png",
                rgb -> transform(amplify(round(rgb, 256, dither), AMP)));
    }

    private FrameBuffer newFrameBuffer() {
        return new FrameBuffer(resolution, matrix);
    }
}
