package band.full.testing.video.generator;

import static band.full.testing.video.itu.BT1886.TRUE_BLACK_TRANSFER;
import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.text.Font.font;

import band.full.testing.video.core.FrameBuffer;
import band.full.testing.video.core.Plane;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.executor.FxImage;

import java.time.Duration;
import java.util.function.DoubleUnaryOperator;

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
class Quants2DBase extends ParametrizedGeneratorBase<Quants2DBase.Args> {
    protected static final Duration DURATION_INTRO = ofSeconds(5);
    protected static final Duration DURATION = ofSeconds(25);

    /** Number of rows have to be an odd number - center row is neutral. */
    public static final int ROWS = 17;
    public static final int COLS = 32;

    public static class Args {
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
    Quants2DBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, "Quants2D-" + pattern);
    }

    @Override
    protected String getFileName(Args args) {
        return factory.folder + '/' + folder + '/' + pattern +
                format("-Y%03d%s-%s", args.yMin,
                        args.redChroma ? "Cr" : "Cb", args.suffix);
    }

    protected void quants(String suffix, int yMin) {
        generate(new Args(suffix, yMin, false));
        generate(new Args(suffix, yMin, true));
    }

    @Override
    protected void encode(EncoderY4M e, Args args) {
        var fb = e.newFrameBuffer();
        generate(fb, args);
        e.render(DURATION_INTRO, () -> fb);

        fb.clear();
        var chroma = args.redChroma ? fb.V : fb.U;
        bandsY(fb.Y, args.yMin);
        bandsC(chroma, matrix.ACHROMATIC - ROWS / 2);
        e.render(DURATION, () -> fb);
    }

    protected void generate(FrameBuffer fb, Args args) {
        var chroma = args.redChroma ? fb.V : fb.U;
        bandsY(fb.Y, args.yMin);
        bandsC(chroma, matrix.ACHROMATIC - ROWS / 2);
        marks(fb, args);
    }

    /**
     * Fills passed Y plane so that each vertical column has individual luma
     * code value starting from <code>yMin</code> and increment of 1 for every
     * next column.
     */
    private void bandsY(Plane luma, int yMin) {
        range(0, COLS).forEach(col -> {
            int x = getColX(width, col);
            int w = getColW(width, col);
            luma.fillRect(x, 0, w, height, yMin + col);
        });
    }

    /**
     * Fills passed Cr or Cb plane so that each horizontal row has individual
     * chroma code value centered around <code>ACHROMATIC</code> code point and
     * increment of 1 for every next row.
     */
    private static void bandsC(Plane chroma, int cMin) {
        range(0, ROWS).forEach(row -> {
            int y = getRowY(chroma.height, row);
            int h = getRowH(chroma.height, row);
            chroma.fillRect(0, y, chroma.width, h, cMin + row);
        });
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        d.read(fb -> range(0, ROWS).forEach(
                row -> range(0, COLS).forEach(
                        col -> verify(fb, args, row, col))));
    }

    protected void verify(FrameBuffer fb, Args args, int row, int col) {
        if (isMarked(col, row)) return; // do not verify cells with markings

        int yCode = args.yMin + col;
        int cCode = matrix.ACHROMATIC - ROWS / 2 + row;

        boolean redChroma = args.redChroma;
        int uCode = redChroma ? matrix.ACHROMATIC : cCode;
        int vCode = redChroma ? cCode : matrix.ACHROMATIC;

        // do not verify cells with markings
        if (matrix.isNominal(yCode, uCode, vCode)) {
            verify(fb.Y, col, row, yCode);
            verify(fb.U, col, row, redChroma ? matrix.ACHROMATIC : cCode);
            verify(fb.V, col, row, redChroma ? cCode : matrix.ACHROMATIC);
        }
    }

    // Cut 1 pixel around block to exclude markings from calculations
    private void verify(Plane plane, int col, int row, int expected) {
        plane.verifyRect(
                getColX(plane.width, col) + 1, getRowY(plane.height, row) + 1,
                getColW(plane.width, col) - 2, getRowH(plane.height, row) - 2,
                expected, 1, 0.01);
        // near-lossless target, allow up to 1% tiny single-step misses
    }

    /**
     * Draws marks around the screen forming virtual grid. The grid is to hint
     * the observer where bands separation is to be expected in case they are
     * displayed without a loss of resolution.
     */
    private void marks(FrameBuffer fb, Args args) {
        int c0 = matrix.ACHROMATIC;

        int markY = args.yMin + matrix.YMIN * 3;
        int markLength = width / 480;

        // luma marks: top / bottom
        rangeClosed(0, COLS).forEach(i -> {
            int x = getColX(width, i) - 1;
            int y = height - markLength;
            fb.fillRect(x, 0, 2, markLength, markY, c0, c0);
            fb.fillRect(x, y, 2, markLength, markY, c0, c0);
        });

        // chroma marks: left / right
        rangeClosed(0, ROWS).forEach(i -> {
            int x = width - markLength;
            int y = getRowY(height, i) - 1;
            fb.fillRect(0, y, markLength, 2, markY, c0, c0);
            fb.fillRect(x, y, markLength, 2, markY, c0, c0);
        });

        FxImage.overlay(overlay(args.yMin, c0, args.redChroma), fb);
    }

    // arrows: https://www.w3schools.com/charsets/ref_utf_arrows.asp
    protected Parent overlay(int yMin, int cMid, boolean redChroma) {
        int yMax = yMin + COLS - 1;
        int midRow = ROWS / 2;
        int midCol = COLS / 2;

        int cMin = cMid - midRow;
        int cMax = cMid + midRow;

        var color = getTextFill(yMax);

        String cName = redChroma ? "C'r" : "C'b";

        var grid = new Pane(
                text("Y'\nC" + yMin, color, 0, midRow),
                text("→", color, 1, midRow),
                text("→", color, COLS - 2, midRow),
                text("Y'\nC" + yMax, color, COLS - 1, midRow),
                text(cName + "\nC" + cMin, color, midCol, 0),
                text("↓", color, midCol, 1),
                text("↓", color, midCol, ROWS - 2),
                text(cName + "\nC" + cMax, color, midCol, ROWS - 1));

        color = Color.color(0.125, 0.0, 0.0);

        for (int yCode = yMin; yCode <= yMax; yCode++) {
            for (int cCode = cMin; cCode <= cMax; cCode++) {
                int uCode = redChroma ? matrix.ACHROMATIC : cCode;
                int vCode = redChroma ? cCode : matrix.ACHROMATIC;
                if (!matrix.isNominal(yCode, uCode, vCode)) {
                    grid.getChildren().add(
                            text("X", color, yCode - yMin, cCode - cMin));
                }
            }
        }

        grid.setBackground(EMPTY);

        return grid;
    }

    protected Color getTextFill(int y) {
        double ye = matrix.fromLumaCode(y);

        DoubleUnaryOperator eotfi = matrix.transfer.isDefinedByEOTF()
                ? matrix.transfer::fromLinear
                : TRUE_BLACK_TRANSFER::eotfi;

        double peak = matrix.transfer.getNominalDisplayPeakLuminance();
        double minY = eotfi.applyAsDouble(1.0 / peak);

        return ye > minY ? Color.BLACK : Color.gray(ye + minY);
    }

    private static boolean isMarked(int col, int row) {
        if (col < 2 || col >= COLS - 2) return row == ROWS / 2;
        if (row < 2 || row >= ROWS - 2) return col == COLS / 2;
        return false;
    }

    private Label text(String text, Color color, int col, int row) {
        var l = new Label(text);
        l.setFont(font(height / 54));
        l.setTextFill(color);
        l.setTextAlignment(TextAlignment.CENTER);
        l.setAlignment(Pos.CENTER);
        l.relocate(getColX(width, col), getRowY(height, row));
        l.setPrefSize(getColW(width, col), getRowH(height, row));

        return l;
    }

    private static int getColX(int width, int col) {
        return width * col / COLS;
    }

    private static int getColW(int width, int col) {
        return getColX(width, col + 1) - getColX(width, col);
    }

    private static int getRowY(int height, int row) {
        return height * row / ROWS;
    }

    private static int getRowH(int height, int row) {
        return getRowY(height, row + 1) - getRowY(height, row);
    }
}
