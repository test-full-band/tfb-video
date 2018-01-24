package band.full.testing.video.generate.basic;

import static java.time.Duration.ofSeconds;

import band.full.testing.video.core.CanvasYUV;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.generate.GeneratorBase;
import band.full.testing.video.itu.YCbCr;

import java.time.Duration;

/**
 * Class for creating full screen checkerboard fills.
 *
 * @author Igor Malinin
 */
public class CheckerboardGenerator extends GeneratorBase {
    protected static final Duration DURATION = ofSeconds(10);

    @Override
    protected void encode(EncoderY4M e) {
        CanvasYUV canvas = e.newCanvas();
        YCbCr matrix = canvas.matrix;

        canvas.Y.calculate(
                (x, y) -> (x + y) % 2 == 0 ? matrix.YMIN : matrix.YMAX);

        e.render(DURATION, () -> canvas);
    }

    @Override
    protected void verify(DecoderY4M d) {
        d.read(c -> {}); // TODO
    }
}
