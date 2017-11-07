package band.full.testing.video.generate;

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
import band.full.testing.video.color.TransferFunctions;
import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.core.Resolution;
import band.full.testing.video.core.Window;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;

import java.time.Duration;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
public abstract class CalibrationBase {
    protected static final Duration DURATION = ofSeconds(30);

    protected abstract String getFilePath();

    protected abstract EncoderParameters getEncoderParameters();

    // TODO Combine to be part of EncoderParameters
    protected abstract TransferFunctions getTransferFunctions();

    // TODO Allow also color box patterns
    protected void encode(EncoderY4M e, int window, int yCode) {
        Window win = getWindow(window);

        CanvasYCbCr canvas = e.newCanvas();
        canvas.Y.fillRect(win.x, win.y, win.width, win.height, yCode);
        canvas.overlay(overlay(window, yCode));

        e.render(DURATION, () -> canvas);
    }

    protected void verify(DecoderY4M d, int window, int yCode) {
        d.read(c -> verify(c, window, yCode));
    }

    protected void verify(CanvasYCbCr canvas, int window, int yCode) {
        Window win = getWindow(window);

        // TODO
        canvas.Y.verifyRect(win.x, win.y, win.width, win.height, yCode);
        // near-lossless target, allow up to 1% tiny single-step misses
    }

    private Window getWindow(int window) {
        Resolution r = getEncoderParameters().resolution;
        double area = window / 100.0;

        return window < 50 ? square(r, area) : proportional(r, area);
    }

    protected String getFileName(int window, int sequence, int yCode) {
        double ye = getEncoderParameters().parameters.fromLumaCode(yCode);

        String seq = sequence < 0 ? "$$"
                : ye < 0.995 ? format("%02.0f", ye * 100.0) : "X0";

        return getFilePath() + format("%02d/Gray%d-HDR10-%s-Y%03d",
                window, window, seq, yCode);
    }

    protected Parent overlay(int window, int yCode) {
        double ye = getEncoderParameters().parameters.fromLumaCode(yCode);
        double yo = getTransferFunctions().eotf(ye);
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
        Primaries primaries = getEncoderParameters().parameters.primaries;
        return new CIEXYZ(primaries.getRGBtoXYZ().multiply(rgb)).CIExy();
    }
}
