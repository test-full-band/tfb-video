package band.full.test.video.generate.png;

import static band.full.test.video.encoder.EncoderParameters.HD_MAIN;
import static band.full.test.video.executor.FxImage.amplify;
import static band.full.test.video.executor.FxImage.offset;
import static band.full.test.video.executor.FxImage.transform;
import static band.full.test.video.executor.FxImage.write;
import static band.full.test.video.executor.GenerateVideo.Type.MAIN;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.BlackPLUGEGenerator;
import band.full.video.buffer.FrameBuffer;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Generates amplified example PNGs for black level setup PLUGE pattern.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class BlackPLUGEPNG extends BlackPLUGEGenerator {
    public BlackPLUGEPNG() {
        super(null, HD_MAIN, null, null, null, null);
    }

    @Test
    public void image() throws IOException {
        var fb = newFrameBuffer();
        draw(fb);
        write(fb, "BlackPLUGE.png", rgb -> transform(amplify(rgb, 12.0)));
    }

    @Test
    public void imageDark() throws IOException {
        var fb = newFrameBuffer();
        draw(fb);
        write(fb, "BlackPLUGEdark.png",
                rgb -> transform(amplify(offset(rgb, -0.02), 14.0)));
    }

    @Test
    public void imageBright() throws IOException {
        var fb = newFrameBuffer();
        draw(fb);
        write(fb, "BlackPLUGEbright.png",
                rgb -> transform(amplify(offset(rgb, 0.03), 10.0)));
    }

    private FrameBuffer newFrameBuffer() {
        return new FrameBuffer(resolution, matrix);
    }
}
