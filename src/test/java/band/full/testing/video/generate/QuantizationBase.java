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
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.executor.FxDisplay;
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
public abstract class QuantizationBase
        extends ParametrizedGeneratorBase<QuantizationBase.Args> {
    protected static final Duration DURATION = ofSeconds(30);

    /** Number of rows have to be an odd number - center row is neutral. */
    public static final int ROWS = 17;
    public static final int COLS = 32;

    public class Args {
        public final String suffix;
        public final int yMin;
        public final boolean redChroma;

        public Args(String suffix, int yMin, boolean redChroma) {
            this.suffix = suffix;
            this.yMin = yMin;
            this.redChroma = redChroma;
        }
    }

    /** only package private direct children are allowed */
    QuantizationBase() {}

    protected abstract void quants(String suffix, int yCode);

    protected void generate(String suffix, int yMin,
            GeneratorFactory factory, EncoderParameters ep) {
        generate(factory, ep, new Args(suffix, yMin, false));
        generate(factory, ep, new Args(suffix, yMin, true));
    }

    @Override
    protected void encode(EncoderY4M e, Args args) {
        CanvasYCbCr canvas = e.newCanvas();

        Plane chroma = args.redChroma ? canvas.Cr : canvas.Cb;

        bandsY(canvas.Y, args.yMin);
        bandsC(chroma, canvas.matrix.ACHROMATIC - ROWS / 2);

        marks(e.parameters, canvas, args);

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

    @Override
    protected void verify(DecoderY4M d, Args args) {
        d.read(c -> range(0, ROWS).forEach(
                row -> range(0, COLS).parallel().forEach(
                        col -> verify(c, args, row, col))));
    }

    protected void verify(CanvasYCbCr canvas, Args args, int row, int col) {
        int c0 = canvas.matrix.ACHROMATIC;

        int yCode = args.yMin + col;
        int cCode = c0 - ROWS / 2 + row;

        if (isValidColor(canvas.matrix, yCode, cCode, args.redChroma)
                && !isMarked(col, row)) {
            verify(canvas.Y, col, row, yCode);
            verify(args.redChroma ? canvas.Cr : canvas.Cb, col, row, cCode);
            verify(args.redChroma ? canvas.Cb : canvas.Cr, col, row, c0);
        }
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
    private void marks(EncoderParameters ep, CanvasYCbCr canvas, Args args) {
        int c0 = canvas.matrix.ACHROMATIC;
        Plane Y = canvas.Y;

        int markY = args.yMin + canvas.matrix.YMIN * 3;
        int markLength = canvas.Y.width / 480;

        // luma marks: top / bottom
        rangeClosed(0, COLS).forEach(i -> {
            int x = getX(Y.width, i) - 1;
            int y = Y.height - markLength;
            canvas.fillRect(x, 0, 2, markLength, markY, c0, c0);
            canvas.fillRect(x, y, 2, markLength, markY, c0, c0);
        });

        // chroma marks: left / right
        rangeClosed(0, ROWS).forEach(i -> {
            int x = Y.width - markLength;
            int y = getY(Y.height, i) - 1;
            canvas.fillRect(0, y, markLength, 2, markY, c0, c0);
            canvas.fillRect(x, y, markLength, 2, markY, c0, c0);
        });

        canvas.overlay(overlay(ep, args.yMin, c0, args.redChroma));
    }

    // arrows: https://www.w3schools.com/charsets/ref_utf_arrows.asp
    protected static Parent overlay(EncoderParameters params,
            int yMin, int cMid, boolean redChroma) {
        int yMax = yMin + COLS - 1;
        int midRow = ROWS / 2;
        int midCol = COLS / 2;

        int cMin = cMid - midRow;
        int cMax = cMid + midRow;

        YCbCr matrix = params.matrix;

        Color color = yMin > matrix.YMIN * 3 ? BLACK
                : gray(matrix.fromLumaCode(yMin + matrix.YMIN * 6));

        String cName = redChroma ? "C'r" : "C'b";

        Resolution res = params.resolution;

        Pane grid = new Pane(
                text(res, "Y'\nC" + yMin, color, 0, midRow),
                text(res, "→", color, 1, midRow),
                text(res, "→", color, COLS - 2, midRow),
                text(res, "Y'\nC" + yMax, color, COLS - 1, midRow),
                text(res, cName + "\nC" + cMin, color, midCol, 0),
                text(res, "↓", color, midCol, 1),
                text(res, "↓", color, midCol, ROWS - 2),
                text(res, cName + "\nC" + cMax, color, midCol, ROWS - 1));

        color = color(0.125, 0.0, 0.0);

        for (int yCode = yMin; yCode <= yMax; yCode++) {
            for (int cCode = cMin; cCode <= cMax; cCode++) {
                if (!isValidColor(matrix, yCode, cCode, redChroma)) {
                    grid.getChildren().add(
                            text(res, "X", color, yCode - yMin, cCode - cMin));
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

    protected static boolean isValidColor(YCbCr matrix,
            int yCode, int cCode, boolean redChroma) {
        double y = matrix.fromLumaCode(yCode);
        double c = matrix.fromChromaCode(cCode);

        double r = matrix.getR(y, redChroma ? c : 0.0);
        double b = matrix.getB(y, redChroma ? 0.0 : c);
        double g = matrix.getG(y, b, r);

        return r >= 0.0 && g >= 0.0 && b >= 0.0;
    }

    private static Label text(Resolution res, String text, Color color,
            int col, int row) {
        Label l = new Label(text);
        l.setFont(font(res.height / 54));
        l.setTextFill(color);
        l.setTextAlignment(TextAlignment.CENTER);
        l.setAlignment(Pos.CENTER);
        l.relocate(getX(res.width, col), getY(res.height, row));
        l.setPrefSize(getW(res.width, col), getH(res.height, row));

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

    public static void main(String[] args) {
        FxDisplay.show(params -> overlay(params, 16, 128, false));
    }
}
