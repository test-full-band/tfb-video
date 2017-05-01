package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Framerate.FPS_23_976;
import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.itu.BT2020_10bit.ACHROMATIC;
import static band.full.testing.video.itu.BT2020_10bit.BLACK;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.time.Duration.ofSeconds;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.encoder.EncoderHDR10;
import band.full.testing.video.encoder.EncoderHEVC;
import band.full.testing.video.generate.GenerateVideo;

/**
 * Testing quality of chroma upsampling.
 * 
 * @author Igor Malinin
 */
@Category(GenerateVideo.class)
public class ChromaSubsampling2160pHDR10 {
    private static final int CENTER_X = STD_2160p.width / 2;
    private static final int CENTER_Y = STD_2160p.height / 2;
    private static final double MAX_DISTANCE = r(0, 0);
    private static final double RANGE = 64; // Fmax / Fmin

    /**
     * Still video consisting of concentric circles of varying width alternating
     * red-black-blue-black. This way Y channel has twice the resolution of
     * chroma channels.
     */
    @Test
    public void concentricRedBlueSine() throws Exception {
        CanvasYCbCr canvas = new CanvasYCbCr(STD_2160p);

        double yAmp = (512 - BLACK) / 2;
        canvas.Y.calculate((x, y) -> BLACK + (int) ((1f - yCos(x, y)) * yAmp));

        // TODO: Find correct amplitudes according to DCI-P3 primaries
        canvas.Cb.calculate((x, y) -> ACHROMATIC + (int) (cSin(x, y) * 400f));
        canvas.Cr.calculate((x, y) -> ACHROMATIC - (int) (cSin(x, y) * 400f));

        try (EncoderHEVC encoder = new EncoderHDR10("HDR10/Chroma-RedBlueSine",
                STD_2160p, FPS_23_976)) {
            encoder.render(ofSeconds(30), () -> canvas);
        }
    }

    /** luma sweep */
    private double yCos(int x, int y) {
        double w1 = PI * MAX_DISTANCE / RANGE; // double frequency
        double L = 1.0 / log(RANGE);

        return cos(w1 * L
                * (exp(r(x, y) / MAX_DISTANCE / L) - 1.0));
    }

    /** chroma half frequency sweep */
    private double cSin(int x, int y) {
        double w1 = PI * MAX_DISTANCE / RANGE / 2.0;
        double L = 1.0 / log(RANGE);

        return sin(w1 * L
                * (exp(r(x * 2, y * 2) / MAX_DISTANCE / L) - 1.0));
    }

    /** radius from screen center */
    private static double r(int x, int y) {
        int dX = CENTER_X - x;
        int dY = CENTER_Y - y;
        return sqrt(dX * dX + dY * dY);
    }
}
