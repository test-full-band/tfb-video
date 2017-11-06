package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Quantizer.round;
import static band.full.testing.video.core.Window.proportional;
import static band.full.testing.video.core.Window.square;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.executor.GenerateVideo.Type.LOSSLESS;
import static band.full.testing.video.itu.BT2020.BT2020_10bit;
import static band.full.testing.video.itu.BT2020.PRIMARIES;
import static band.full.testing.video.smpte.ST2084.PQ;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.time.Duration.ofMinutes;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.paint.Color.gray;
import static javafx.scene.text.Font.font;

import band.full.testing.video.color.CIEXYZ;
import band.full.testing.video.color.CIExy;
import band.full.testing.video.color.Matrix3x3;
import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.core.Resolution;
import band.full.testing.video.core.Window;
import band.full.testing.video.encoder.EncoderHDR10;
import band.full.testing.video.executor.FxDisplay;
import band.full.testing.video.executor.GenerateVideo;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Calibration box fills.
 *
 * @author Igor Malinin
 */
@GenerateVideo(LOSSLESS)
public class Calibrate2160pHDR10 {
    private static final String PATH = "HEVC/UHD4K/HDR10/Calibrate/Win";
    private static final Duration DURATION = ofMinutes(1);
    private static final Matrix3x3 RGB2XYZ = PRIMARIES.getRGBtoXYZ();

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
        // show brightest and darkest patterns in the beginning
        grayscale(window, -1, 940);
        grayscale(window, 0, 64);

        int gradations = 20;
        double amp = 1.0 / gradations;
        for (int i = 1; i <= gradations; i++) {
            grayscale(window, i, round(BT2020_10bit.toLumaCode(amp * i)));
        }
    }

    private void grayscale(int window, int sequence, int yCode) {
        String name = getFileName(window, sequence, yCode);

        EncoderHDR10.encode(name, e -> {
            Resolution resolution = e.encoderParameters.resolution;
            double area = window / 100.0;

            Window win = window < 50
                    ? square(resolution, area)
                    : proportional(resolution, area);

            CanvasYCbCr canvas = e.newCanvas();
            canvas.Y.fillRect(win.x, win.y, win.width, win.height, yCode);
            canvas.overlay(overlay(window, yCode));

            e.render(DURATION, () -> canvas);
        });
    }

    private static String getFileName(int window, int sequence, int yCode) {
        double ye = BT2020_10bit.fromLumaCode(yCode);

        String seq = sequence < 0 ? "$$"
                : ye < 0.995 ? format("%02.0f", ye * 100.0) : "X0";

        return PATH + format("%02d/Gray%d-HDR10-%s-Y%03d", window, window,
                seq, yCode);
    }

    private static Parent overlay(int window, int yCode) {
        double ye = BT2020_10bit.fromLumaCode(yCode);
        double yo = PQ.eotf(ye);
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

    private static CIExy getColor(double yo) {
        double w = yo <= 0 ? 1 : yo; // fake color for pure black
        double[] rgb = {w, w, w};
        return new CIEXYZ(RGB2XYZ.multiply(rgb)).CIExy();
    }

    public static void main(String[] args) {
        FxDisplay.show(HDR10.resolution, () -> overlay(10, 512));
    }
}
