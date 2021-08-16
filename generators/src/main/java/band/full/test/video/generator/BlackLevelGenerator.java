package band.full.test.video.generator;

import static java.lang.Math.max;
import static java.util.stream.IntStream.range;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.paint.Color.gray;
import static javafx.scene.text.Font.font;

import band.full.core.Resolution;
import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;
import band.full.test.video.executor.FrameVerifier;
import band.full.test.video.executor.FxDisplay;
import band.full.test.video.executor.FxImage;
import band.full.video.buffer.FrameBuffer;
import band.full.video.buffer.Plane;
import band.full.video.itu.ColorMatrix;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;

/**
 * Patterns for setting up black level brightness.
 *
 * @author Igor Malinin
 */
public class BlackLevelGenerator extends GeneratorBase<Void> {
    protected static final int COLS = 16;

    public BlackLevelGenerator(GeneratorFactory factory,
            EncoderParameters params, NalUnitPostProcessor<Void> processor,
            MuxerFactory muxer, String folder, String group) {
        super(factory, params, processor, muxer, folder, "BlackLevel", group);
    }

    @Override
    protected void encode(EncoderY4M e, Void args, String phase) {
        var fb = e.newFrameBuffer();

        patches(fb);
        marks(fb);

        e.render(gop, () -> fb);
    }

    /**
     * Fills passed Y plane so that each vertical column has individual luma
     * code value starting from <code>yMin</code> and increment of 1 for every
     * next column.
     */
    private void patches(FrameBuffer fb) {
        var matrix = fb.matrix;

        range(0, COLS).forEach(col -> {
            Plane luma = fb.Y;
            int x = getX(width, col);
            int w = getW(width, col);
            luma.fillRect(x, 0, w, luma.height, getLuma(matrix, col));
        });
    }

    @Override
    protected void verify(DecoderY4M d, Void args) {
        d.read(fb -> verify(fb));
    }

    protected void verify(FrameBuffer fb) {
        var matrix = fb.matrix;

        range(0, COLS).forEach(col -> {
            verify(fb.Y, col, getLuma(matrix, col));
            verify(fb.U, col, matrix.ACHROMATIC);
            verify(fb.V, col, matrix.ACHROMATIC);
        });
    }

    // Cut some pixels inside block to exclude markings from calculations
    private void verify(Plane plane, int col, int expected) {
        int h = getLabelH(plane.height);

        // near-lossless target, allow up to 0.1% tiny single-step misses
        FrameVerifier.verifyRect(expected, plane,
                getX(plane.width, col) + 1, h,
                getW(plane.width, col) - 2, plane.height - h * 2,
                1, 0.001);
    }

    /**
     * Draws marks around the screen forming virtual grid. The grid is to hint
     * the observer where bands separation is to be expected in case they are
     * displayed without a loss of resolution.
     */
    private void marks(FrameBuffer fb) {
        FxImage.overlay(overlay(params), fb);
    }

    protected static Parent overlay(EncoderParameters params) {
        var resolution = params.resolution;
        var matrix = params.matrix;

        Pane grid = new Pane();

        range(0, COLS).mapToObj(col -> top(resolution, matrix, col))
                .forEach(grid.getChildren()::add);

        range(0, COLS).mapToObj(col -> bottom(resolution, matrix, col))
                .forEach(grid.getChildren()::add);

        grid.setBackground(EMPTY);

        return grid;
    }

    private static Label top(Resolution res, ColorMatrix matrix, int col) {
        Label l = text(res, matrix, col);
        l.relocate(getX(res.width(), col), 0);
        return l;
    }

    private static Label bottom(Resolution res, ColorMatrix matrix, int col) {
        Label l = text(res, matrix, col);
        l.relocate(getX(res.width(), col), res.height() - l.getPrefHeight());
        return l;
    }

    private static Label text(Resolution res, ColorMatrix matrix, int col) {
        Label l = new Label(Integer.toString(getLuma(matrix, col)));
        l.setFont(font(res.height() / 54));
        l.setTextFill(gray(matrix.fromLumaCode(matrix.YMIN * 4)));
        l.setTextAlignment(TextAlignment.CENTER);
        l.setAlignment(Pos.CENTER);
        l.setPrefSize(getW(res.width(), col), getLabelH(res.height()));
        return l;
    }

    private static int getLabelH(int height) {
        return height / 15;
    }

    private static int getLuma(ColorMatrix matrix, int col) {
        return max(0, matrix.YMIN - 5) + col;
    }

    private static int getX(int width, int col) {
        return width * col / COLS + width / 240;
    }

    private static int getW(int width, int col) {
        return getX(width, col + 1) - getX(width, col) - width / 120;
    }

    /** Quickly display overlay window */
    public static void main(String[] args) {
        FxDisplay.show(BlackLevelGenerator::overlay);
    }
}
