package band.full.testing.video.generate.hevc;

import static band.full.testing.video.core.Resolution.STD_1080p;
import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.executor.GenerateVideo.Type.LOSSLESS;
import static java.time.Duration.ofSeconds;

import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.encoder.EncoderHEVC;
import band.full.testing.video.executor.GenerateVideo;

import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * Demonstrating sub-sample detail and gradients with different dithering
 * methods and different bit depths.
 *
 * @author Igor Malinin
 */
@GenerateVideo(LOSSLESS)
// @Ignore("Requires lossless encode and reducing bitrate to at least 100Mb/s")
public class Gradients1080pHEVC {
    @Test
    public void gradients() {
        gradients("HEVC/Gradients1080p06"); // 6 bit demo
    }

    public void gradients(String name) {
        EncoderHEVC.encode(name, FULLHD_MAIN8, e -> {
            CanvasYCbCr c = e.newCanvas();
            c.Cb.fill(c.parameters.ACHROMATIC);
            c.Cr.fill(c.parameters.ACHROMATIC);
            e.render(ofSeconds(30), () -> gradients(c));
        });
    }

    /** Render with new dither per frame */
    private CanvasYCbCr gradients(CanvasYCbCr canvas) {
        canvas.Y.calculate(this::fn);
        return canvas;
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
