package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.encoder.EncoderHDR10.MASTER_DISPLAY_PRIMARIES;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.encoder.Preset.FAST;
import static band.full.testing.video.executor.GenerateVideo.Type.LOSSLESS;
import static java.lang.String.format;
import static java.time.Duration.ofMinutes;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.paint.Color.GRAY;
import static javafx.scene.text.Font.font;

import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.encoder.EncoderHDR10;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.executor.FxDisplay;
import band.full.testing.video.executor.GenerateVideo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * @author Igor Malinin
 */
@GenerateVideo(LOSSLESS)
@Disabled("Used for investigation of 2017 LG OLED mode switching logic.")
public class Calibrate2160pHDR10_LGOLED2017 {
    private static final String PATH =
            "HEVC/UHD4K/HDR10/Calibrate/LG/OLED7check";

    private static final Duration DURATION = ofMinutes(1);

    @Test
    public void oled7metaCheck() {
        win10grayscale("A", 540, 0); // 669
        win10grayscale("B", 1000, 0); // 693
        win10grayscale("C", 4000, 0); // 749
        win10grayscale("D", 10000, 0); // 749
        win10grayscale("E", 540, 540); // 669
        win10grayscale("F", 540, 1000); // 669
        win10grayscale("G", 540, 4000);
        win10grayscale("H", 540, 10000);
        win10grayscale("I", 1000, 540);
        win10grayscale("J", 1000, 1000);
        win10grayscale("K", 1000, 4000);
        win10grayscale("L", 1000, 10000);
        win10grayscale("M", 4000, 540);
        win10grayscale("N", 4000, 1000);
        win10grayscale("O", 4000, 4000);
        win10grayscale("P", 4000, 10000);
        win10grayscale("Q", 10000, 540);
        win10grayscale("R", 10000, 1000);
        win10grayscale("S", 10000, 4000);
        win10grayscale("T", 10000, 10000);
        win10grayscale("U", 2000, 700);
        win10grayscale("V", 700, 2000);
    }

    public void win10grayscale(String version, int display, int maxcll) {
        EncoderParameters fast = HDR10.withPreset(FAST);

        EncoderParameters options = (maxcll <= 0)
                ? fast.withEncoderOptions(
                        "--master-display",
                        MASTER_DISPLAY_PRIMARIES
                                + "L(" + display + "0000,0)")
                : fast.withEncoderOptions(
                        "--max-cll", format("%d,%d", maxcll, maxcll / 10),
                        "--master-display",
                        MASTER_DISPLAY_PRIMARIES
                                + "L(" + display + "0000,0)");

        EncoderHDR10.encode(PATH + version, options, e -> {
            CanvasYCbCr canvas = e.newCanvas();
            canvas.overlay(overlay(version));

            e.render(DURATION, () -> canvas);
        });
    }

    private static Parent overlay(String version) {
        Label label = new Label(version);
        label.setFont(font(1000));
        label.setTextFill(GRAY);

        BorderPane layout = new BorderPane();
        layout.setBackground(EMPTY);
        layout.setCenter(label);
        return layout;
    }

    public static void main(String[] args) {
        FxDisplay.show(STD_2160p, () -> overlay("A"));
    }
}
