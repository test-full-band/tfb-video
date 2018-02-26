package band.full.testing.video.generate.basic;

import static java.time.Duration.ofSeconds;

import band.full.testing.video.core.FrameBuffer;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.generate.GeneratorBase;
import band.full.testing.video.generate.GeneratorFactory;
import band.full.testing.video.itu.ColorMatrix;

import java.time.Duration;

/**
 * Class for creating full screen checkerboard fills.
 *
 * @author Igor Malinin
 */
public class CheckerboardGenerator extends GeneratorBase {
    protected static final Duration DURATION = ofSeconds(10);

    public CheckerboardGenerator(GeneratorFactory factory,
            EncoderParameters params, String folder, String name) {
        super(factory, params, folder, name);
    }

    @Override
    protected void encode(EncoderY4M e) {
        FrameBuffer fb = e.newFrameBuffer();
        ColorMatrix matrix = fb.matrix;

        fb.Y.calculate(
                (x, y) -> (x + y) % 2 == 0 ? matrix.YMIN : matrix.YMAX);

        e.render(DURATION, () -> fb);
    }

    @Override
    protected void verify(DecoderY4M d) {
        d.read(fb -> {});
    }
}
