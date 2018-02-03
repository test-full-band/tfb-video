package band.full.testing.video.generate;

import static band.full.testing.video.core.Quantizer.round;
import static band.full.testing.video.core.Window.proportional;
import static band.full.testing.video.core.Window.square;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.paint.Color.gray;
import static javafx.scene.text.Font.font;

import band.full.testing.video.color.CIEXYZ;
import band.full.testing.video.color.CIExy;
import band.full.testing.video.color.Primaries;
import band.full.testing.video.core.CanvasYUV;
import band.full.testing.video.core.Resolution;
import band.full.testing.video.core.Window;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.itu.ColorMatrix;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Base class for creating single-color patches in the middle of the screen with
 * specified area percentage.
 *
 * @author Igor Malinin
 */
public abstract class CalibrationBase {
    protected static final Duration DURATION = ofSeconds(30);

    protected abstract String getFilePath();

    protected abstract EncoderParameters getEncoderParameters();

    protected abstract void grayscale(int window, int sequence, int yCode);

    @Test
    public void win5grayscale() {
        grayscale(5);
    }

    @Test
    public void win10grayscale() {
        grayscale(10);
    }

    @Test
    public void win20grayscale() {
        grayscale(20);
    }

    @Test
    public void win50grayscale() {
        grayscale(50);
    }

    public void grayscale(int window) {
        ColorMatrix matrix = getEncoderParameters().matrix;

        // show brightest and darkest patterns in the beginning
        grayscale(window, -1, matrix.YMAX);
        grayscale(window, 0, matrix.YMIN);

        int gradations = 20;
        double amp = 1.0 / gradations;
        for (int i = 1; i <= gradations; i++) {
            grayscale(window, i, round(matrix.toLumaCode(amp * i)));
        }
    }

    // TODO Allow also color box patterns
    protected void encode(EncoderY4M e, int window, int yCode) {
        Window win = getWindow(window);

        CanvasYUV canvas = e.newCanvas();
        canvas.Y.fillRect(win.x, win.y, win.width, win.height, yCode);
        canvas.overlay(overlay(window, yCode));

        e.render(DURATION, () -> canvas);
    }

    protected void verify(DecoderY4M d, int window, int yCode) {
        d.read(c -> verify(c, window, yCode));
    }

    protected void verify(CanvasYUV canvas, int window, int yCode) {
        Window win = getWindow(window);

        int achromatic = getEncoderParameters().matrix.ACHROMATIC;

        // TODO near-lossless target, allow up to 1% tiny single-step misses
        canvas.verifyRect(win.x, win.y, win.width, win.height,
                yCode, achromatic, achromatic);
    }

    private Window getWindow(int window) {
        Resolution r = getEncoderParameters().resolution;
        double area = window / 100.0;

        return window < 50 ? square(r, area) : proportional(r, area);
    }

    protected String getFileName(int window, int sequence, int yCode) {
        double ye = getEncoderParameters().matrix.fromLumaCode(yCode);

        String seq = sequence < 0 ? "$$"
                : ye < 0.995 ? format("%02.0f", ye * 100.0) : "X0";

        return getFilePath() + format("%02d/Gray%d-HDR10-%s-Y%03d",
                window, window, seq, yCode);
    }

    protected Parent overlay(int window, int yCode) {
        EncoderParameters params = getEncoderParameters();
        double ye = params.matrix.fromLumaCode(yCode);
        double yo = params.matrix.transfer.eotf(ye);
        CIExy xy = getColor(yo);

        String text = format("HDR10 grayscale CIE(x=%.4f, y=%.4f) %.1f%% Y%d,"
                + " %.1f nit", xy.x, xy.y, ye * 100.0, yCode, yo * 10000.0);

        Label label = new Label(text);
        label.setFont(font(40));
        label.setTextFill(gray(max(0.25, min(0.5, ye))));

        BorderPane.setMargin(label, new Insets(20));
        BorderPane layout = new BorderPane();
        layout.setBackground(EMPTY);
        layout.setBottom(label);
        return layout;
    }

    private CIExy getColor(double yo) {
        double w = yo <= 0 ? 1 : yo; // fake color for pure black
        double[] rgb = {w, w, w};
        Primaries primaries = getEncoderParameters().matrix.primaries;
        return new CIEXYZ(primaries.getRGBtoXYZ().multiply(rgb)).CIExy();
    }
}
