package band.full.testing.video.generate;

import static java.lang.Math.max;
import static java.time.Duration.ofSeconds;
import static java.util.stream.IntStream.range;
import static javafx.scene.layout.Background.EMPTY;
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
import javafx.scene.text.TextAlignment;

/**
 * Patterns for setting up black level brightness.
 *
 * @author Igor Malinin
 */
public class BlackLevelBase extends GeneratorBase {
    protected static final Duration DURATION = ofSeconds(20);
    protected static final int COLS = 16;

    @Override
    protected void encode(EncoderY4M e) {
        CanvasYCbCr canvas = e.newCanvas();

        patches(canvas);
        marks(e.parameters, canvas);

        e.render(DURATION, () -> canvas);
    }

    /**
     * Fills passed Y plane so that each vertical column has individual luma
     * code value starting from <code>yMin</code> and increment of 1 for every
     * next column.
     */
    private void patches(CanvasYCbCr canvas) {
        YCbCr matrix = canvas.matrix;

        range(0, COLS).forEach(col -> {
            Plane luma = canvas.Y;
            int x = getX(luma.width, col);
            int w = getW(luma.width, col);
            luma.fillRect(x, 0, w, luma.height, getLuma(matrix, col));
        });
    }

    @Override
    protected void verify(DecoderY4M d) {
        d.read(c -> verify(c));
    }

    protected void verify(CanvasYCbCr canvas) {
        YCbCr matrix = canvas.matrix;

        range(0, COLS).parallel().forEach(col -> {
            verify(canvas.Y, col, getLuma(matrix, col));
            verify(canvas.Cb, col, matrix.ACHROMATIC);
            verify(canvas.Cr, col, matrix.ACHROMATIC);
        });
    }

    // Cut some pixels inside block to exclude markings from calculations
    private void verify(Plane plane, int col, int expected) {
        int h = getLabelH(plane.height);

        plane.verifyRect(
                getX(plane.width, col) + 1, h,
                getW(plane.width, col) - 2, plane.height - h * 2,
                expected, 1, 0.01);
        // near-lossless target, allow up to 1% tiny single-step misses
    }

    /**
     * Draws marks around the screen forming virtual grid. The grid is to hint
     * the observer where bands separation is to be expected in case they are
     * displayed without a loss of resolution.
     */
    private void marks(EncoderParameters params, CanvasYCbCr canvas) {
        canvas.overlay(overlay(params));
    }

    protected static Parent overlay(EncoderParameters params) {
        Resolution resolution = params.resolution;
        YCbCr matrix = params.matrix;

        Pane grid = new Pane();

        range(0, COLS).mapToObj(col -> top(resolution, matrix, col))
                .forEach(grid.getChildren()::add);

        range(0, COLS).mapToObj(col -> bottom(resolution, matrix, col))
                .forEach(grid.getChildren()::add);

        grid.setBackground(EMPTY);

        return grid;
    }

    private static Label top(Resolution res, YCbCr matrix, int col) {
        Label l = text(res, matrix, col);
        l.relocate(getX(res.width, col), 0);
        return l;
    }

    private static Label bottom(Resolution res, YCbCr matrix, int col) {
        Label l = text(res, matrix, col);
        l.relocate(getX(res.width, col), res.height - l.getPrefHeight());
        return l;
    }

    private static Label text(Resolution res, YCbCr matrix, int col) {
        Label l = new Label(Integer.toString(getLuma(matrix, col)));
        l.setFont(font(res.height / 54));
        l.setTextFill(gray(matrix.fromLumaCode(matrix.YMIN * 4)));
        l.setTextAlignment(TextAlignment.CENTER);
        l.setAlignment(Pos.CENTER);
        l.setPrefSize(getW(res.width, col), getLabelH(res.height));
        return l;
    }

    private static int getLabelH(int height) {
        return height / 15;
    }

    private static int getLuma(YCbCr matrix, int col) {
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
        FxDisplay.show(BlackLevelBase::overlay);
    }
}
