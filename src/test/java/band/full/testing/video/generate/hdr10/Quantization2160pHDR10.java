package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.itu.BT2020.BT2020_10bit;
import static java.lang.String.format;
import static java.time.Duration.ofMinutes;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.paint.Color.color;
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
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@RunWith(GenerateVideoRunner.class)
@Category(GenerateVideo.class)
public class Quantization2160pHDR10 {
    private static final String PATH = "HEVC/UHD4K/HDR10/Quantization";
    private static final Duration DURATION = ofMinutes(1);

    /** Number of rows have to be an odd number - center row is neutral. */
    private static final int ROWS = 17;
    private static final int COLS = 32;

    @Test
    public void quantsNearBlack() throws Exception {
        quants("NearBlack", 64, 96); // 32
    }

    @Test
    public void quantsDarkGray() throws Exception {
        quants("DarkGray", 128, 160); // 32
    }

    @Test
    public void quantsGray() throws Exception {
        quants("Gray", 192, 256); // 64
    }

    @Test
    public void quantsLightGray() throws Exception {
        quants("LightGray", 320, 384); // 64
    }

    @Test
    public void quantsNearWhite() throws Exception {
        quants("NearWhite", 448, 512); // 64
    }

    @Test
    public void quantsBright() throws Exception {
        quants("Bright", 576, 640); // 64
    }

    @Test
    public void quantsBrighter() throws Exception {
        quants("Brighter", 704, 768); // 64
    }

    @Test
    public void quantsBrightest() throws Exception {
        quants("Brightest", 832, 876); // 44
    }

    private void quants(String name, int... yCodes) {
        for (int yCode : yCodes) {
            String prefix = PATH + "/QuantsHDR10-Y"
                    + format("%03d", yCode);

            EncoderHDR10.encode(prefix + "Cb-" + name, e -> {
                CanvasYCbCr canvas = e.newCanvas();
                canvas.Cr.fill(e.parameters.ACHROMATIC);
                quants(canvas, e.parameters.YMIN, canvas.Cb);
                e.render(DURATION, () -> canvas);
            });

            EncoderHDR10.encode(prefix + "Cr-" + name, e -> {
                CanvasYCbCr canvas = e.newCanvas();
                canvas.Cb.fill(e.parameters.ACHROMATIC);
                quants(canvas, e.parameters.YMIN, canvas.Cr);
                e.render(DURATION, () -> canvas);
            });
        }
    }

    public void quants(CanvasYCbCr canvas, int yMin, Plane chroma) {
        bandsY(canvas.Y, yMin);
        bandsC(chroma, canvas.parameters.ACHROMATIC - (ROWS / 2));
        marks(canvas, yMin, chroma == canvas.Cr); // ~ ?? nit
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
    private void bandsC(Plane plane, int cMin) {
        range(0, ROWS).forEach(i -> {
            plane.fillRect(0, getY(plane.height, i),
                    plane.width, getH(plane.height, i), cMin + i);
        });
    }

    /**
     * Draws marks around the screen forming virtual grid. The grid is to hint
     * the observer where bands separation is to be expected in case they are
     * displayed without a loss of resolution.
     */
    private void marks(CanvasYCbCr canvas, int yMin, boolean redChroma) {
        int c0 = canvas.parameters.ACHROMATIC;
        Plane Y = canvas.Y;

        int markY = yMin + 192;

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

        canvas.overlay(overlay(yMin, c0, redChroma));
    }

    // arrows: https://www.w3schools.com/charsets/ref_utf_arrows.asp
    private static Parent overlay(int yMin, int cMid, boolean redChroma) {
        int yMax = yMin + COLS - 1;
        int midRow = ROWS / 2;
        int midCol = COLS / 2;

        int cMin = cMid - midRow;
        int cMax = cMid + midRow;

        Color color = yMin > 192 ? Color.BLACK
                : gray(BT2020_10bit.fromLumaCode(yMin + 192));

        String cName = redChroma ? "C'r" : "C'b";

        Pane grid = new Pane(
                text("Y'\nC" + yMin, color, 0, midRow),
                text("→", color, 1, midRow),
                text("→", color, COLS - 2, midRow),
                text("Y'\nC" + yMax, color, COLS - 1, midRow),
                text(cName + "\nC" + cMin, color, midCol, 0),
                text("↓", color, midCol, 1),
                text("↓", color, midCol, ROWS - 2),
                text(cName + "\nC" + cMax, color, midCol, ROWS - 1));

        color = color(0.125, 0.0, 0.0);

        for (int yCode = yMin; yCode <= yMax; yCode++) {
            for (int cCode = cMin; cCode <= cMax; cCode++) {
                double y = BT2020_10bit.fromLumaCode(yCode);
                double c = BT2020_10bit.fromChromaCode(cCode);

                double r = BT2020_10bit.getR(y, redChroma ? c : 0);
                double b = BT2020_10bit.getB(y, redChroma ? 0 : c);
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
        FxDisplay.show(HDR10.resolution, () -> overlay(64, 512, false));
    }
}
