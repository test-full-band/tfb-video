package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Quantizer.round;
import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.smpte.ST2084.PQ;
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
import band.full.testing.video.itu.YCbCr;

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
    private static final double RANGE = 32; // Fmax / Fmin

    /**
     * Black & White concentric sine circles
     */
    @Test
    public void concentricBlackWhiteSineE() throws Exception {
        EncoderHDR10.encode(PATH + "/ChromaHDR10-BlackWhiteCodeSineE", e -> {
            CanvasYCbCr c = e.newCanvas();
            YCbCr params = c.parameters;

            int grayY = round(params.toLumaCode(0.25));

            c.Y.calculate((x, y) -> {
                double radius = r(x, y);

                return (radius > MAX_DISTANCE) ? grayY : round(
                        params.toLumaCode(0.25 * (1.0 - cosineSweep(radius))));
            });

            short achromatic = (short) c.parameters.ACHROMATIC;

            fill(c.Cb.pixels, achromatic);
            fill(c.Cr.pixels, achromatic);

            e.render(DURATION, () -> c);
        });
    }

    @Test
    public void concentricBlackWhiteSineO() throws Exception {
        EncoderHDR10.encode(PATH + "/ChromaHDR10-BlackWhiteSineO", e -> {
            CanvasYCbCr c = e.newCanvas();
            YCbCr params = c.parameters;

            int grayY = round(params.toLumaCode(0.25));

            double amp = PQ.eotf(0.5) / 2.0;

            c.Y.calculate((x, y) -> {
                double radius = r(x, y);

                return (radius > MAX_DISTANCE) ? grayY : round(
                        params.toLumaCode(
                                PQ.oetf(amp * (1.0 - cosineSweep(radius)))));
            });

            short achromatic = (short) c.parameters.ACHROMATIC;

            fill(c.Cb.pixels, achromatic);
            fill(c.Cr.pixels, achromatic);

            e.render(DURATION, () -> c);
        });
    }

    /**
     * Concentric circles of varying width alternating Red and Blue with half
     * the resolution of the Y channel.
     */
    @Test
    // @Ignore("Find correct amplitudes according to DCI-P3 primaries")
    public void concentricRedBlueSineE() throws Exception {
        EncoderHDR10.encode(PATH + "/ChromaHDR10-RedBlueSineE", e -> {
            CanvasYCbCr c = e.newCanvas();
            YCbCr params = c.parameters;

            int grayY = round(params.toLumaCode(0.25));
            int achromatic = params.ACHROMATIC;

            for (int y = 0; y < c.Y.height; y++) {
                boolean hasChromaY = (y & 1) == 0;

                for (int x = 0; x < c.Y.width; x++) {
                    boolean hasChromaX = (x & 1) == 0;

                    double radius = r(x, y);

                    if (radius > MAX_DISTANCE) {
                        c.Y.set(x, y, grayY);

                        if (hasChromaX && hasChromaY) {
                            int cx = x >> 1, cy = y >> 1;

                            c.Cb.set(cx, cy, achromatic);
                            c.Cr.set(cx, cy, achromatic);
                        }
                    } else {
                        double sin = 0.25 * sineSweepHalf(radius);

                        double R = 0.25 + sin;
                        double B = 0.25 - sin;

                        double Y = params.getY(R, 0.0, B);
                        c.Y.set(x, y, round(params.toLumaCode(Y)));

                        if (hasChromaX && hasChromaY) {
                            int cx = x >> 1, cy = y >> 1;

                            double Cb = params.getCb(Y, B);
                            c.Cb.set(cx, cy, round(params.toChromaCode(Cb)));

                            double Cr = params.getCr(Y, R);
                            c.Cr.set(cx, cy, round(params.toChromaCode(Cr)));
                        }
                    }
                }
            }

            e.render(DURATION, () -> c);
        });
    }

    /** luma log sine sweep */
    private double cosineSweep(double r) {
        double w1 = PI * MAX_DISTANCE / RANGE; // double frequency
        double L = 1.0 / log(RANGE);

        return cos(w1 * L * (exp(r / MAX_DISTANCE / L) - 1.0));
    }

    /** chroma half frequency log sine sweep */
    private double sineSweepHalf(double r) {
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
