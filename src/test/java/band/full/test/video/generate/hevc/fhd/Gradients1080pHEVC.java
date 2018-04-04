package band.full.test.video.generate.hevc.fhd;

import static band.full.core.Resolution.STD_1080p;
import static band.full.test.video.executor.GenerateVideo.Type.LOSSLESS;
import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static java.time.Duration.ofSeconds;

import band.full.test.video.executor.GenerateVideo;
import band.full.video.buffer.FrameBuffer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * Demonstrating sub-sample detail and gradients with different dithering
 * methods and different bit depths.
 *
 * @author Igor Malinin
 */
@GenerateVideo(LOSSLESS)
@Disabled("Requires lossless encode and reducing bitrate to at least 100Mb/s")
public class Gradients1080pHEVC {
    @Test
    public void gradients() {
        gradients("Gradients1080p06"); // 6 bit demo
    }

    public void gradients(String name) {
        HEVC.generate("Gradients", name, FULLHD_MAIN8,
                e -> {
                    FrameBuffer fb = e.newFrameBuffer();
                    e.render(ofSeconds(30), () -> gradients(fb));
                }, d -> {});
    }

    /** Render with new dither per frame */
    private FrameBuffer gradients(FrameBuffer fb) {
        fb.Y.calculate(this::fn);
        return fb;
    }

    private static final int REMOVE_BITS = 2;
    private static final int REMOVE_BITS_RATIO = 1 << REMOVE_BITS;
    private static final int RANGE = 40;
    private static final int WIDTH = STD_1080p.width;
    private static final double INT_DIVIDER = 1L << 32;

    private static final Random PRNG = new Random();

    private static final double rnd() {
        return PRNG.nextInt() / INT_DIVIDER;
    }

    private int fn(int x, int y) {
        double val = ((double) x) / WIDTH * RANGE;

        if (y < 270) {
            val = ((int) (val * 2.0)) / 2.0; // emulate 1 additional bit
            val += rnd();

            return 64 + ((int) val) * REMOVE_BITS_RATIO;
        }

        if (y == 270 || y == 540 || y == 810) return 16;
        if (y > 540) {
            val += rnd(); // RPDF
        }
        if (y > 810) {
            val += rnd(); // 2RPDF = TPDF
        }

        return 64 + ((int) val) * REMOVE_BITS_RATIO;
    }
}
