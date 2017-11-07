package band.full.testing.video.generate;

import static java.time.Duration.ofSeconds;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.color;
import static javafx.scene.paint.Color.gray;
import static javafx.scene.text.Font.font;

import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.core.Plane;
import band.full.testing.video.core.Resolution;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.itu.YCbCr;

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
public abstract class QuantizationBase {
    protected static final Duration DURATION = ofSeconds(30);

    /** Number of rows have to be an odd number - center row is neutral. */
    protected static final int ROWS = 17;
    protected static final int COLS = 32;

    protected abstract String getFilePath();

    protected abstract Resolution getResolution();

    protected abstract YCbCr getVideoParameters();

    protected void quants(EncoderY4M e, int yMin, boolean redChroma) {
        CanvasYCbCr canvas = e.newCanvas();

        Plane chroma = redChroma ? canvas.Cr : canvas.Cb;

        bandsY(canvas.Y, yMin);
        bandsC(chroma, canvas.parameters.ACHROMATIC - ROWS / 2);

        marks(canvas, yMin, redChroma);

        e.render(DURATION, () -> canvas);
    }

    /**
     * Fills passed Y plane so that each vertical column has individual luma
     * code value starting from <code>yMin</code> and increment of 1 for every
     * next column.
     */
    private void bandsY(Plane luma, int yMin) {
        range(0, COLS).forEach(col -> {
            int x = getX(luma.width, col);
            int w = getW(luma.width, col);
            luma.fillRect(x, 0, w, luma.height, yMin + col);
        });
    }

    /**
     * Fills passed Cr or Cb plane so that each horizontal row has individual
     * chroma code value centered around <code>ACHROMATIC</code> code point and
     * increment of 1 for every next row.
     */
    private void bandsC(Plane chroma, int cMin) {
        range(0, ROWS).forEach(row -> {
            int y = getY(chroma.height, row);
            int h = getH(chroma.height, row);
            chroma.fillRect(0, y, chroma.width, h, cMin + row);
        });
    }

    protected void verify(DecoderY4M d, int yMin, boolean redChroma) {
        d.read(c -> verify(c, yMin, redChroma));
    }

    protected void verify(CanvasYCbCr canvas, int yMin, boolean redChroma) {
        range(0, ROWS).forEach(row -> {
            range(0, COLS).parallel().forEach(col -> {
                int yCode = yMin + col;
                int cCode = canvas.parameters.ACHROMATIC - ROWS / 2 + row;

                if (isValidColor(yCode, cCode, redChroma)
                        && !isMarked(col, row)) {
                    verify(canvas.Y, col, row, yCode);

                    verify(redChroma ? canvas.Cr : canvas.Cb,
                            col, row, cCode);

                    verify(redChroma ? canvas.Cb : canvas.Cr,
                            col, row, canvas.parameters.ACHROMATIC);
                }
            });
        });
    }

    // Cut 1 pixel around block to exclude markings from calculations
    private void verify(Plane plane, int col, int row, int expected) {
        plane.verifyRect(
                getX(plane.width, col) + 1,
                getY(plane.height, row) + 1,
                getW(plane.width, col) - 2,
                getH(plane.height, row) - 2,
                expected, 1, 0.01);
        // near-lossless target, allow up to 1% tiny single-step misses
    }

    /**
     * Draws marks around the screen forming virtual grid. The grid is to hint
     * the observer where bands separation is to be expected in case they are
     * displayed without a loss of resolution.
     */
    private void marks(CanvasYCbCr canvas, int yMin, boolean redChroma) {
        int c0 = canvas.parameters.ACHROMATIC;
        Plane Y = canvas.Y;

        int markY = yMin + canvas.parameters.YMIN * 3;

        // TODO Mark length: change 8 to proportional to screen size

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
    protected Parent overlay(int yMin, int cMid, boolean redChroma) {
        int yMax = yMin + COLS - 1;
        int midRow = ROWS / 2;
        int midCol = COLS / 2;

        int cMin = cMid - midRow;
        int cMax = cMid + midRow;

        YCbCr params = getVideoParameters();

        Color color = yMin > params.YMIN * 3 ? BLACK
                : gray(params.fromLumaCode(yMin + params.YMIN * 6));

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
                if (!isValidColor(yCode, cCode, redChroma)) {
                    grid.getChildren().add(
                            text("X", color, yCode - yMin, cCode - cMin));
                }
            }
        }

        grid.setBackground(EMPTY);

        return grid;
    }

    private boolean isMarked(int col, int row) {
        if (col < 2 || col >= COLS - 2) return row == ROWS / 2;
        if (row < 2 || row >= ROWS - 2) return col == COLS / 2;
        return false;
    }

    protected boolean isValidColor(int yCode, int cCode, boolean redChroma) {
        YCbCr params = getVideoParameters();

        double y = params.fromLumaCode(yCode);
        double c = params.fromChromaCode(cCode);

        double r = params.getR(y, redChroma ? c : 0.0);
        double b = params.getB(y, redChroma ? 0.0 : c);
        double g = params.getG(y, b, r);

        return r >= 0.0 && g >= 0.0 && b >= 0.0;
    }

    private Label text(String text, Color color, int col, int row) {
        Resolution resolution = getResolution();

        Label l = new Label(text);
        l.setFont(font(resolution.height / 54));
        l.setTextFill(color);
        l.setTextAlignment(TextAlignment.CENTER);
        l.setAlignment(Pos.CENTER);

        l.relocate(
                getX(resolution.width, col),
                getY(resolution.height, row));

        l.setPrefSize(
                getW(resolution.width, col),
                getH(resolution.height, row));

        return l;
    }

    private static int getX(int width, int col) {
        return width * col / COLS;
    }

    private static int getW(int width, int col) {
        return getX(width, col + 1) - getX(width, col);
    }

    private static int getY(int height, int row) {
        return height * row / ROWS;
    }

    private static int getH(int height, int row) {
        return getY(height, row + 1) - getY(height, row);
    }
}
