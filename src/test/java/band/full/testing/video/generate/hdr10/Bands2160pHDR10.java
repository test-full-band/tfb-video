package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Framerate.FPS_23_976;
import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.itu.BT2020_10bit.ACHROMATIC;
import static band.full.testing.video.itu.BT2020_10bit.BLACK;
import static java.time.Duration.ofSeconds;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.core.Plane;
import band.full.testing.video.encoder.EncoderHDR10;
import band.full.testing.video.encoder.EncoderHEVC;
import band.full.testing.video.generate.GenerateVideo;

/**
 * Testing bands separation.
 * 
 * @author Igor Malinin
 */
@Category(GenerateVideo.class)
@UseParametersRunnerFactory
public class Bands2160pHDR10 {
    private static final int ROWS = 9; // odd number, center row is neutral
    private static final int COLS = 32;

    @Test
    public void bandsNearBlackCr() throws Exception {
        CanvasYCbCr canvas = new CanvasYCbCr(STD_2160p);
        canvas.Cb.fill(ACHROMATIC);
        bandsNearBlack("HDR10/Bands-0001-YCr", canvas, canvas.Cr);
    }

    @Test
    public void bandsNearBlackCb() throws Exception {
        CanvasYCbCr canvas = new CanvasYCbCr(STD_2160p);
        canvas.Cr.fill(ACHROMATIC);
        bandsNearBlack("HDR10/Bands-0001-YCb", canvas, canvas.Cb);
    }

    public void bandsNearBlack(String name, CanvasYCbCr canvas, Plane chroma)
            throws Exception {
        bandsY(canvas.Y);
        bandsC(chroma);
        marks(canvas);

        try (EncoderHEVC encoder = new EncoderHDR10(name,
                STD_2160p, FPS_23_976)) {
            encoder.render(ofSeconds(30), () -> canvas);
        }
    }

    private void bandsY(Plane luma) {
        int width = luma.width;
        int height = luma.height;
        int ybase = BLACK;

        range(0, COLS).forEach(i -> {
            int x = width * i / COLS, w = width * (i + 1) / COLS - x;
            luma.fillRect(x, 0, w, height, ybase + i);
        });
    }

    private void bandsC(Plane plane) {
        int width = plane.width;
        int height = plane.height;
        int cbase = ACHROMATIC - (ROWS / 2);

        range(0, ROWS).forEach(i -> {
            int y = height * i / ROWS, h = height * (i + 1) / ROWS - y;
            plane.fillRect(0, y, width, h, cbase + i);
        });
    }

    private static final int MARK_Y = 512;
    private static final int MARK_C = ACHROMATIC;

    private void marks(CanvasYCbCr canvas) {
        Plane luma = canvas.Y;

        int width = luma.width;
        int height = luma.height;

        // luma marks
        rangeClosed(0, COLS).forEach(i -> {
            int x = width * i / COLS - 1;
            canvas.fillRect(x, 0, 2, 8, MARK_Y, MARK_C, MARK_C); // top
            canvas.fillRect(x, height - 8, 2, 8, MARK_Y, MARK_C, MARK_C); // bottom
        });

        // chroma marks
        rangeClosed(0, ROWS).forEach(i -> {
            int y = height * i / ROWS - 1;
            canvas.fillRect(0, y, 8, 2, MARK_Y, MARK_C, MARK_C); // left
            canvas.fillRect(width - 8, y, 8, 2, MARK_Y, MARK_C, MARK_C); // right
        });
    }
}
