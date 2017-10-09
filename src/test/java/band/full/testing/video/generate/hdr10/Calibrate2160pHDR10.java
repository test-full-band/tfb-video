package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Quantizer.round;
import static band.full.testing.video.core.Window.square;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
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
import band.full.testing.video.core.Window;
import band.full.testing.video.encoder.EncoderHDR10;
import band.full.testing.video.executor.FxDisplay;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.executor.GenerateVideoRunner;
import band.full.testing.video.itu.YCbCr;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

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
@RunWith(GenerateVideoRunner.class)
@Category(GenerateVideo.class)
public class Calibrate2160pHDR10 {
    private static final String PATH = "HEVC/UHD4K/HDR10/Calibrate/Win";
    private static final Duration DURATION = ofMinutes(1);
    private static final Matrix3x3 RGB2XYZ = PRIMARIES.getRGBtoXYZ();

    @Test
    public void win10gray() {
        // show brightest and darkest patterns in the beginning
        win10gray(-1, 940);
        win10gray(0, 64);

        int gradations = 20;
        double amp = 1.0 / gradations;
        for (int i = 1; i <= gradations; i++) {
            win10gray(i, round(BT2020_10bit.toLumaCode(amp * i)));
        }
    }

    private void win10gray(int sequence, int yCode) {
        String name = getFileName(sequence, yCode);

        EncoderHDR10.encode(name, e -> {
            Window win = square(e.encoderParameters.resolution, 0.1);
            YCbCr params = e.parameters;

            CanvasYCbCr canvas = e.newCanvas();
            canvas.Y.fill(params.YMIN);
            canvas.Y.fillRect(win.x, win.y, win.width, win.height, yCode);
            canvas.Cb.fill(params.ACHROMATIC);
            canvas.Cr.fill(params.ACHROMATIC);
            canvas.overlay(overlay(yCode));

            e.render(DURATION, () -> canvas);
        });
    }

    private static String getFileName(int sequence, int yCode) {
        double ye = BT2020_10bit.fromLumaCode(yCode);

        String seq = sequence < 0 ? "$$"
                : ye < 0.995 ? format("%02.0f", ye * 100.0) : "X0";

        return PATH + format("10/GrayHDR10-%s-Y%03d", seq, yCode);
    }

    private static Parent overlay(int yCode) {
        double ye = BT2020_10bit.fromLumaCode(yCode);
        double yo = PQ.eotf(ye);
        CIExy xy = getColor(yo);

        String text = format("HDR10 grayscale CIE(x=%.4f, y=%.4f) %.1f%% C%d,"
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
        FxDisplay.show(HDR10.resolution, () -> overlay(512));
    }
}
