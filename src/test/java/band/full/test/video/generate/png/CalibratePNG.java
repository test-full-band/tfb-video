package band.full.test.video.generate.png;

import static band.full.test.video.executor.FxImage.transform;
import static band.full.test.video.executor.FxImage.write;
import static band.full.test.video.executor.GenerateVideo.Type.MAIN;
import static band.full.video.encoder.EncoderParameters.HD_MAIN;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateColorCheckerBase;
import band.full.video.buffer.FrameBuffer;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Generates example PNG for calibration patch patterns.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class CalibratePNG extends CalibrateColorCheckerBase {
    public CalibratePNG() {
        super(null, HD_MAIN, null, null);
    }

    @Override // disable parameterization
    public void generate(Args args) {}

    @Override
    protected Stream<Args> args() {
        return Stream.empty(); // prevent video generation
    }

    @Test
    public void image() throws IOException {
        var fb = newFrameBuffer();
        generate(fb, colorchecker(10, 2, 1));
        write(fb, "Calibrate.png", rgb -> transform(rgb));
    }

    private FrameBuffer newFrameBuffer() {
        return new FrameBuffer(resolution, matrix);
    }
}
