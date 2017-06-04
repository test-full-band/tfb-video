package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.itu.BT2020.BT2020_10bit;
import static java.time.Duration.ofMinutes;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.paint.Color.gray;
import static javafx.scene.text.Font.font;

import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.core.Plane;
import band.full.testing.video.encoder.EncoderHDR10;
import band.full.testing.video.generate.FxDisplay;
import band.full.testing.video.generate.GenerateVideo;
import band.full.testing.video.generate.GenerateVideoRunner;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.time.Duration;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * Testing color bands for separation / step uniformity.
 *
 * @author Igor Malinin
 */
@RunWith(GenerateVideoRunner.class)
@Category(GenerateVideo.class)
public class Bands2160pHDR10 {
    private static final String PATH = "HEVC/UHD4K/HDR10/bands";
    private static final Duration DURATION = ofMinutes(1);

    /** Number of rows have to be an odd number - center row is neutral. */
    private static final int ROWS = 17;
    private static final int COLS = 32;

    @Test
    public void bandsY064CrNearBlack() throws Exception {
        EncoderHDR10.encode(PATH + "/Bands-Y064Cr-NearBlack", e -> {
            CanvasYCbCr canvas = e.newCanvas();
            canvas.Cb.fill(e.parameters.ACHROMATIC);
            bandsNearBlack(canvas, e.parameters.YMIN, canvas.Cr);
            e.render(DURATION, () -> canvas);
        });
    }

    @Test
    public void bandsY064CbNearBlack() throws Exception {
        EncoderHDR10.encode(PATH + "/Bands-Y064Cb-NearBlack", e -> {
            CanvasYCbCr canvas = e.newCanvas();
            canvas.Cr.fill(e.parameters.ACHROMATIC);
            bandsNearBlack(canvas, e.parameters.YMIN, canvas.Cb);
            e.render(DURATION, () -> canvas);
        });
    }

    @Test
    public void bandsY096CrNearBlack() throws Exception {
        EncoderHDR10.encode(PATH + "/Bands-Y096Cr-NearBlack", e -> {
            CanvasYCbCr canvas = e.newCanvas();
            canvas.Cb.fill(e.parameters.ACHROMATIC);
            bandsNearBlack(canvas, e.parameters.YMIN + COLS, canvas.Cr);
            e.render(DURATION, () -> canvas);
        });
    }

    @Test
    public void bandsY096CbNearBlack() throws Exception {
        EncoderHDR10.encode(PATH + "/Bands-Y096Cb-NearBlack", e -> {
            CanvasYCbCr canvas = e.newCanvas();
            canvas.Cr.fill(e.parameters.ACHROMATIC);
            bandsNearBlack(canvas, e.parameters.YMIN + COLS, canvas.Cb);
            e.render(DURATION, () -> canvas);
        });
    }

    @Test
    public void bandsY128CrDark() throws Exception {
        EncoderHDR10.encode(PATH + "/Bands-Y128Cr-DarkGray", e -> {
            CanvasYCbCr canvas = e.newCanvas();
            canvas.Cb.fill(e.parameters.ACHROMATIC);
            bandsNearBlack(canvas, e.parameters.YMIN + 2 * COLS, canvas.Cr);
            e.render(DURATION, () -> canvas);
        });
    }

    @Test
    public void bandsY128CbDark() throws Exception {
        EncoderHDR10.encode(PATH + "/Bands-Y128Cb-DarkGray", e -> {
            CanvasYCbCr canvas = e.newCanvas();
            canvas.Cr.fill(e.parameters.ACHROMATIC);
            bandsNearBlack(canvas, e.parameters.YMIN + 2 * COLS, canvas.Cb);
            e.render(DURATION, () -> canvas);
        });
    }

    public void bandsNearBlack(CanvasYCbCr canvas, int ybase, Plane chroma) {
        bandsY(canvas.Y, ybase);
        bandsC(chroma, canvas.parameters.ACHROMATIC - (ROWS / 2));
        marks(canvas, 256, ybase); // ~ ?? nit
    }

    /**
     * Fills passed Y plane so that each vertical column has individual luma
     * code value starting from <code>ybase</code> and increment of 1 for every
     * next column.
     */
    private void bandsY(Plane luma, int ybase) {
        range(0, COLS).forEach(i -> {
            luma.fillRect(getX(luma.width, i), 0,
                    getW(luma.width, i), luma.height, ybase + i);
        });
    }

    /**
     * Fills passed Cr or Cb plane so that each horizontal row has individual
     * chroma code value centered around <code>ACHROMATIC</code> code point and
     * increment of 1 for every next row.
     */
    private void bandsC(Plane plane, int cbase) {
        range(0, ROWS).forEach(i -> {
            plane.fillRect(0, getY(plane.height, i),
                    plane.width, getH(plane.height, i), cbase + i);
        });
    }

    /**
     * Draws marks around the screen forming virtual grid. The grid is to hint
     * the observer where bands separation is to be expected in case they are
     * displayed without a loss of resolution.
     */
    private void marks(CanvasYCbCr canvas, int markY, int ybase) {
        int c0 = canvas.parameters.ACHROMATIC;
        Plane Y = canvas.Y;

        // luma marks: top / bottom
        rangeClosed(0, COLS).forEach(i -> {
            int x = getX(Y.width, i) - 1;
            canvas.fillRect(x, 0, 2, 8, markY, c0, c0);
            canvas.fillRect(x, Y.height - 8, 2, 8, markY, c0, c0);
        });

        // chroma marks: left / right
        rangeClosed(0, ROWS).forEach(i -> {
            int y = getY(Y.height, i) - 1;
            canvas.fillRect(0, y, 8, 2, markY, c0, c0);
            canvas.fillRect(Y.width - 8, y, 8, 2, markY, c0, c0);
        });

        canvas.overlay(overlay(ybase));
    }

    // arrows: https://www.w3schools.com/charsets/ref_utf_arrows.asp
    private static Parent overlay(int yMin) {
        int yMax = yMin + COLS - 1;
        int midRow = ROWS / 2;
        int midCol = COLS / 2;

        int cMin = 512 - midRow;
        int cMax = 512 + midRow;

        Color color = yMin > 192 ? Color.BLACK
                : gray(BT2020_10bit.fromLumaCode(yMin + 192));

        Pane grid = new Pane(
                text("Y'\nC" + yMin, color, 0, midRow),
                text("→", color, 1, midRow),
                text("→", color, COLS - 2, midRow),
                text("Y'\nC" + yMax, color, COLS - 1, midRow),
                text("C'b\nC" + cMin, color, midCol, 0),
                text("↓", color, midCol, 1),
                text("↓", color, midCol, ROWS - 2),
                text("C'b\nC" + cMax, color, midCol, ROWS - 1));

        color = Color.color(0.1, 0.0, 0.0);
        for (int yCode = yMin; yCode <= yMax; yCode++) {
            for (int cCode = cMin; cCode <= cMax; cCode++) {
                double y = BT2020_10bit.fromLumaCode(yCode);
                double c = BT2020_10bit.fromChromaCode(cCode);

                double r = BT2020_10bit.getR(y, c);
                double b = BT2020_10bit.getB(y, 0);
                double g = BT2020_10bit.getG(y, b, r);

                if (r < 0 || g < 0 || b < 0) {
                    grid.getChildren().add(
                            text("X", color, yCode - yMin, cCode - cMin));
                }
            }
        }

        grid.setBackground(EMPTY);

        return grid;
    }

    private static Label text(String text, Color color, int col, int row) {
        Label l = new Label(text);
        l.setFont(font(40));
        l.setTextFill(color);
        l.setTextAlignment(TextAlignment.CENTER);
        l.setAlignment(Pos.CENTER);

        l.relocate(
                getX(STD_2160p.width, col),
                getY(STD_2160p.height, row));

        l.setPrefSize(
                getW(STD_2160p.width, col),
                getH(STD_2160p.height, row));

        return l;
    }

    private static int getX(int width, int column) {
        return width * column / COLS;
    }

    private static int getW(int width, int column) {
        return getX(width, column + 1) - getX(width, column);
    }

    private static int getY(int height, int row) {
        return height * row / ROWS;
    }

    private static int getH(int height, int row) {
        return getY(height, row + 1) - getY(height, row);
    }

    public static void main(String[] args) {
        FxDisplay.show(HDR10.resolution, () -> overlay(64));
    }
}
