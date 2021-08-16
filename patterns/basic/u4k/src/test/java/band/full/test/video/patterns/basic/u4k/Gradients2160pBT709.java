package band.full.test.video.patterns.basic.u4k;

import static band.full.core.Resolution.STD_2160p;
import static band.full.test.video.encoder.EncoderParameters.UHD4K_MAIN8;
import static band.full.test.video.executor.GenerateVideo.Type.LOSSLESS;
import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static java.time.Duration.ofSeconds;

import band.full.test.video.executor.GenerateVideo;
import band.full.video.buffer.FrameBuffer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Demonstrating sub-sample detail and gradients with different dithering
 * methods and different bit depths.
 *
 * @author Igor Malinin
 */
@GenerateVideo(LOSSLESS)
@Disabled("Requires lossless encode and  reducing bitrate to at least 100Mb/s")
public class Gradients2160pBT709 {
    @Test
    public void gradients() throws IOException, InterruptedException {
        gradients("Gradients-06"); // 6 bit demo
    }

    public void gradients(String name)
            throws IOException, InterruptedException {
        File dir = HEVC.greet("Gradients", name);
        HEVC.encode(dir, name, UHD4K_MAIN8, e -> {
            FrameBuffer fb = e.newFrameBuffer();
            e.render(ofSeconds(30), () -> gradients(fb));
        });
    }

    /** Render with new dither per frame */
    private FrameBuffer gradients(FrameBuffer fb) {
        fb.Y.calculate(this::fn);
        return fb;
    }

    private static final int REMOVE_BITS = 2;
    private static final int REMOVE_BITS_RATIO = 1 << REMOVE_BITS;
    private static final int RANGE = 40;
    private static final int WIDTH = STD_2160p.width();
    private static final double INT_DIVIDER = 1L << 32;

    private static final Random PRNG = new Random();

    private static final double rnd() {
        return PRNG.nextInt() / INT_DIVIDER;
    }

    private int fn(int x, int y) {
        double val = ((double) x) / WIDTH * RANGE;

        if (y < 540) {
            val = ((int) (val * 2.0)) / 2.0; // emulate 1 additional bit
            val += rnd();

            return 64 + ((int) val) * REMOVE_BITS_RATIO;
        }

        if (y == 540 || y == 1080 || y == 1620) return 16;
        if (y > 1080) {
            val += rnd(); // RPDF
        }
        if (y > 1620) {
            val += rnd(); // 2RPDF = TPDF
        }

        return 64 + ((int) val) * REMOVE_BITS_RATIO;
    }
}
