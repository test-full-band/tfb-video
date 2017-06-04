package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Resolution.STD_2160p;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.time.Duration.ofMinutes;
import static java.util.Arrays.fill;

import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.encoder.EncoderHDR10;
import band.full.testing.video.generate.GenerateVideo;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Duration;

/**
 * Testing quality of chroma upsampling.
 *
 * @author Igor Malinin
 */
@Category(GenerateVideo.class)
public class ChromaSubsampling2160pHDR10 {
    private static final String PATH = "HEVC/UHD4K/HDR10/Chroma";
    private static final Duration DURATION = ofMinutes(1);

    private static final int CENTER_X = STD_2160p.width / 2;
    private static final int CENTER_Y = STD_2160p.height / 2;
    private static final double MAX_DISTANCE = CENTER_Y;
    private static final double RANGE = 64; // Fmax / Fmin

    /**
     * Black & White luma reference for concentric sine circles
     */
    @Test
    public void concentricBlackWhiteSine() throws Exception {
        EncoderHDR10.encode(PATH + "/ChromaHDR10-BlackWhiteReference", e -> {
            CanvasYCbCr c = e.newCanvas();

            int black = c.parameters.YMIN;
            double yAmp = (512 - black) / 2;

            c.Y.calculate((x, y) -> black + (int) ((1f - yCos(x, y)) * yAmp));

            short achromatic = (short) c.parameters.ACHROMATIC;

            fill(c.Cb.pixels, achromatic);
            fill(c.Cr.pixels, achromatic);

            e.render(DURATION, () -> c);
        });
    }

    /**
     * Still video consisting of concentric circles of varying width alternating
     * red-black-blue-black. This way Y channel has twice the resolution of
     * chroma channels.
     */
    @Test
    public void concentricRedBlueSine() throws Exception {
        EncoderHDR10.encode(PATH + "/ChromaHDR10-RedBlueSine", e -> {
            CanvasYCbCr c = e.newCanvas();

            int black = c.parameters.YMIN;
            double yAmp = (512 - black) / 2;

            c.Y.calculate((x, y) -> black + (int) ((1f - yCos(x, y)) * yAmp));

            int achromatic = c.parameters.ACHROMATIC;

            // TODO: Find correct amplitudes according to DCI-P3 primaries
            c.Cb.calculate((x, y) -> achromatic + (int) (cSin(x, y) * 400f));
            c.Cr.calculate((x, y) -> achromatic - (int) (cSin(x, y) * 400f));

            e.render(DURATION, () -> c);
        });
    }

    /** luma sweep */
    private double yCos(int x, int y) {
        double r = r(x, y);
        if (r > MAX_DISTANCE) return 0;
        // if (true) return 0; // Do we need to modulate luma?

        double w1 = PI * MAX_DISTANCE / RANGE; // double frequency
        double L = 1.0 / log(RANGE);

        return cos(w1 * L * (exp(r / MAX_DISTANCE / L) - 1.0));
    }

    /** chroma half frequency sweep */
    private double cSin(int x, int y) {
        double r = r(x * 2, y * 2);
        if (r > MAX_DISTANCE) return 0;

        double w1 = PI * MAX_DISTANCE / RANGE / 2.0;
        double L = 1.0 / log(RANGE);

        return sin(w1 * L * (exp(r / MAX_DISTANCE / L) - 1.0));
    }

    /** radius from screen center */
    private static double r(int x, int y) {
        int dX = CENTER_X - x;
        int dY = CENTER_Y - y;
        return sqrt(dX * dX + dY * dY);
    }
}
