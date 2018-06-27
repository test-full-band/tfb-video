package band.full.test.video.generate.png;

import static band.full.test.video.executor.FxImage.amplify;
import static band.full.test.video.executor.FxImage.offset;
import static band.full.test.video.executor.FxImage.transform;
import static band.full.test.video.executor.FxImage.write;
import static band.full.test.video.executor.GenerateVideo.Type.MAIN;
import static band.full.video.encoder.EncoderParameters.HD_MAIN;

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
    private static final double AMP = 16.0; // intensity amplification factor
    private final double OFF;

    public BlackPLUGEPNG() {
        super(null, HD_MAIN, null, null);
        OFF = matrix.fromLumaCode(matrix.YMIN + step1); // intensity offset
    }

    @Test
    public void image() throws IOException {
        var fb = newFrameBuffer();
        draw(fb);
        write(fb, "BlackPLUGE.png", rgb -> transform(amplify(rgb, AMP)));
    }

    @Test
    public void imageDark() throws IOException {
        var fb = newFrameBuffer();
        draw(fb);
        write(fb, "BlackPLUGEdark.png",
                rgb -> transform(amplify(offset(rgb, -OFF), AMP)));
    }

    @Test
    public void imageBright() throws IOException {
        var fb = newFrameBuffer();
        draw(fb);
        write(fb, "BlackPLUGEbright.png",
                rgb -> transform(amplify(offset(rgb, OFF), AMP)));
    }

    private FrameBuffer newFrameBuffer() {
        return new FrameBuffer(resolution, matrix);
    }
}
