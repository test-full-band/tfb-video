package band.full.test.video.generator;

import static band.full.video.itu.BT1886.TRUE_BLACK_TRANSFER;
import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static javafx.scene.layout.Background.EMPTY;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.core.Window;
import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;
import band.full.test.video.executor.FrameVerifier;
import band.full.test.video.executor.FxImage;
import band.full.test.video.generator.Quants2DBase.Args;
import band.full.video.buffer.FrameBuffer;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Testing color bands separation / quantization step uniformity.
 * <p>
 * Use one of specialized versions.
 *
 * @author Igor Malinin
 * @see Quants2DBase8
 * @see Quants2DBase10
 * @see Quants2DBase10HDR
 */
// TODO Dynamic sweep for non-near black/white patterns (->less files)
// TODO Color sweeps
@TestInstance(PER_CLASS)
public abstract class Quants2DBase extends GeneratorBase<Args> {
    /** Number of rows have to be an odd number - center row is neutral. */
    public static final int ROWS = 15;
    public static final int COLS = 30;

    public static class Args {
        public final String suffix;
        public final int yMin;
        public final boolean redChroma;

        public Args(String suffix, int yMin, boolean redChroma) {
            this.suffix = suffix;
            this.yMin = yMin;
            this.redChroma = redChroma;
        }

        @Override
        public String toString() {
            return format("Y%03d%s %s",
                    yMin, redChroma ? "Cr" : "Cb", suffix);
        }
    }

    protected Quants2DBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder, "Quants2D", group);
    }

    /** only package private direct children are allowed */
    protected Quants2DBase(GeneratorFactory factory,
            EncoderParameters params, NalUnitPostProcessor<Args> processor,
            MuxerFactory muxer, String folder, String group) {
        super(factory, params, processor, muxer, folder, "Quants2D", group);
    }

    @Override
    @ParameterizedTest(name = "{arguments}")
    @MethodSource("args")
    public void generate(Args args) {
        super.generate(args);
    }

    protected abstract Stream<Args> args();

    protected Stream<Args> quants(String suffix, int yMin) {
        return Stream.of(
                new Args(suffix, yMin, false),
                new Args(suffix, yMin, true));
    }

    protected Stream<Args> quants(String suffix, int yMin1, int yMin2) {
        return Stream.of(
                new Args(suffix, yMin1, false),
                new Args(suffix, yMin1, true),
                new Args(suffix, yMin2, false),
                new Args(suffix, yMin2, true));
    }

    @Override
    protected String getPattern(Args args) {
        return super.getPattern(args) + format("-Y%03d%s-%s",
                args.yMin, args.redChroma ? "Cr" : "Cb", args.suffix);
    }

    @Override
    public List<String> encode(File dir, Args args)
            throws IOException, InterruptedException {
        var all = new ArrayList<String>(PATTERN_SECONDS);
        all.addAll(encode(dir, args, INTRO, INTRO_SECONDS));
        all.addAll(encode(dir, args, BODY, BODY_SECONDS));
        return all;
    }

    @Override
    protected void encode(EncoderY4M e, Args args, String phase) {
        var fb = e.newFrameBuffer();
        fill(fb, args);

        if (phase == INTRO) {
            marks(fb, args);
        }

        e.render(gop, () -> fb);
    }

    public void generate(FrameBuffer fb, Args args) {
        fill(fb, args);
        marks(fb, args);
    }

    /**
     * <p>
     * Fills Y plane so that each vertical column has individual luma code value
     * starting from <code>yMin</code> and increment of 1 for every next column.
     * <p>
     * Fills Cr or Cb plane so that each horizontal row has individual chroma
     * code value centered around <code>ACHROMATIC</code> code point and
     * increment of 1 for every next row.
     */
    private void fill(FrameBuffer fb, Args args) {
        range(0, ROWS).forEach(
                row -> range(0, COLS).forEach(
                        col -> fill(fb, args, row, col)));
    }

    @Override
    protected void verify(File dir, String mp4, Args args) {
        verify(dir, mp4, INTRO_SECONDS - 1, 2, args);
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        d.read(fb -> range(1, ROWS - 1).forEach(
                row -> range(1, COLS - 1).forEach(
                        col -> verify(fb, args, row, col))));
    }

    private void fill(FrameBuffer fb, Args args, int row, int col) {
        fb.fillRect(getWindow(row, col), getYUV(args, row, col));
    }

    protected void verify(FrameBuffer fb, Args args, int row, int col) {
        if (isMarked(col, row)) return; // do not verify cells with markings

        int[] yuv = getYUV(args, row, col);

        if (matrix.isNominal(yuv)) { // do not verify cells with markings
            FrameVerifier.verifyRect(yuv, fb,
                    getWindow(row, col).shrink(2), 1, 0.02);
        }
    }

    /**
     * Draws marks around the screen forming virtual grid. The grid is to hint
     * the observer where bands separation is to be expected in case they are
     * displayed without a loss of resolution.
     */
    private void marks(FrameBuffer fb, Args args) {
        int c0 = matrix.ACHROMATIC;

        int markY = (int) matrix.toLumaCode(getMarksLuma(args.yMin));
        int markLength = width / 480;

        // luma marks: top / bottom
        rangeClosed(0, COLS).forEach(i -> {
            int x = getColX(i) - 1;
            int y = height - markLength;
            fb.fillRect(x, 0, 2, markLength, markY, c0, c0);
            fb.fillRect(x, y, 2, markLength, markY, c0, c0);
        });

        // chroma marks: left / right
        rangeClosed(0, ROWS).forEach(i -> {
            int x = width - markLength;
            int y = getRowY(i) - 1;
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

        var color = Color.gray(getMarksLuma(yMin));

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

    protected double getMarksLuma(int yMin) {
        int yMax = yMin + COLS - 1;
        double ye = matrix.fromLumaCode(yMax);

        DoubleUnaryOperator eotfi = transfer.isDefinedByEOTF()
                ? transfer::fromLinear
                : TRUE_BLACK_TRANSFER::eotfi;

        double peak = transfer.getNominalDisplayPeakLuminance();
        double minY = eotfi.applyAsDouble(1.0 / peak);

        return ye > minY ? 0 : ye + minY;
    }

    private static boolean isMarked(int col, int row) {
        if (col < 2 || col >= COLS - 2) return row == ROWS / 2;
        if (row < 2 || row >= ROWS - 2) return col == COLS / 2;
        return false;
    }

    private Label text(String text, Color color, int col, int row) {
        return text(getWindow(row, col), color, text);
    }

    private int[] getYUV(Args args, int row, int col) {
        boolean redChroma = args.redChroma;
        int cCode = matrix.ACHROMATIC - ROWS / 2 + row;

        return new int[] {
            args.yMin + col,
            redChroma ? matrix.ACHROMATIC : cCode,
            redChroma ? cCode : matrix.ACHROMATIC
        };
    }

    private Window getWindow(int row, int col) {
        return new Window(
                getColX(col), getRowY(row),
                getColW(col), getRowH(row));
    }

    private int getColX(int col) {
        return width * col / COLS;
    }

    private int getColW(int col) {
        return getColX(col + 1) - getColX(col);
    }

    private int getRowY(int row) {
        return height * row / ROWS;
    }

    private int getRowH(int row) {
        return getRowY(row + 1) - getRowY(row);
    }
}
