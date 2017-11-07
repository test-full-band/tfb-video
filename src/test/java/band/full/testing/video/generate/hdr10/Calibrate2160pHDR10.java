package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.core.Quantizer.round;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.executor.GenerateVideo.Type.LOSSLESS;
import static band.full.testing.video.itu.BT2020.BT2020_10bit;
import static band.full.testing.video.smpte.ST2084.PQ;

import band.full.testing.video.color.TransferFunctions;
import band.full.testing.video.encoder.EncoderHDR10;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.executor.FxDisplay;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.CalibrationBase;

import org.junit.jupiter.api.Test;

/**
 * Calibration box fills.
 *
 * @author Igor Malinin
 */
@GenerateVideo(LOSSLESS)
public class Calibrate2160pHDR10 extends CalibrationBase {
    @Override
    protected String getFilePath() {
        return "HEVC/UHD4K/HDR10/Calibrate/Win";
    }

    @Override
    protected EncoderParameters getEncoderParameters() {
        return HDR10;
    }

    @Override
    protected TransferFunctions getTransferFunctions() {
        return PQ;
    }

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

        EncoderHDR10.encode(name,
                e -> encode(e, window, yCode),
                d -> verify(d, window, yCode));
    }

    public static void main(String[] args) {
        Calibrate2160pHDR10 instance = new Calibrate2160pHDR10();

        FxDisplay.show(instance.getEncoderParameters().resolution,
                () -> instance.overlay(10, 512));
    }
}
