package band.full.testing.video.generate.hevc;

import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.itu.BT709.BT709_8bit;
import static java.lang.String.format;

import band.full.testing.video.core.Resolution;
import band.full.testing.video.encoder.EncoderHEVC;
import band.full.testing.video.executor.FxDisplay;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.QuantizationBase;
import band.full.testing.video.itu.YCbCr;

import org.junit.jupiter.api.Test;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quantization2160pBT709_10 extends QuantizationBase {
    @Test
    public void quants() {
        quants("NearBlack", 64, 96); // 32
        quants("DarkGray", 128, 160); // 32
        quants("Gray", 192, 256); // 64
        quants("LightGray", 320, 384); // 64
        quants("NearWhite", 448, 512); // 64
        quants("Bright", 576, 640); // 64
        quants("Brighter", 704, 768); // 64
        quants("Brightest", 832, 876); // 44
    }

    private void quants(String name, int... yCodes) {
        for (int yCode : yCodes) {
            String prefix =
                    getFilePath() + format("/QuantsBT709_10-Y%03d", yCode);

            EncoderHEVC.encode(prefix + "Cb-" + name,
                    e -> quants(e, yCode, false),
                    d -> verify(d, yCode, false));

            EncoderHEVC.encode(prefix + "Cr-" + name,
                    e -> quants(e, yCode, true),
                    d -> verify(d, yCode, true));
        }
    }

    @Override
    protected String getFilePath() {
        return "HEVC/UHD4K/BT709_10/Quantization";
    }

    @Override
    protected Resolution getResolution() {
        return STD_2160p;
    }

    @Override
    protected YCbCr getVideoParameters() {
        return BT709_8bit;
    }

    public static void main(String[] args) {
        Quantization2160pBT709_10 instance = new Quantization2160pBT709_10();

        FxDisplay.show(instance.getResolution(),
                () -> instance.overlay(64, 512, false));
    }
}
