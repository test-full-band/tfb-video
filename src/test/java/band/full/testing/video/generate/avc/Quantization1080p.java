package band.full.testing.video.generate.avc;

import static band.full.testing.video.core.Resolution.STD_1080p;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;
import static band.full.testing.video.itu.BT709.BT709;
import static java.lang.String.format;

import band.full.testing.video.core.Resolution;
import band.full.testing.video.encoder.EncoderAVC;
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
@GenerateVideo(MAIN)
public class Quantization1080p extends QuantizationBase {
    @Test
    public void quants() {
        quants("NearBlack", 16, 32); // 16
        quants("DarkGray", 48, 64); // 16
        quants("Gray", 96, 128); // 32
        quants("LightGray", 160, 192); // 32
        quants("NearWhite", 204); // 20
        quants("Bright", 224); // 20
    }

    private void quants(String name, int... yCodes) {
        for (int yCode : yCodes) {
            String prefix = getFilePath() + format("/Quants1080p-Y%03d", yCode);

            EncoderAVC.encode(prefix + "Cb-" + name,
                    e -> quants(e, yCode, false),
                    d -> verify(d, yCode, false));

            EncoderAVC.encode(prefix + "Cr-" + name,
                    e -> quants(e, yCode, true),
                    d -> verify(d, yCode, true));
        }
    }

    @Override
    protected String getFilePath() {
        return "AVC/FullHD/Quantization";
    }

    @Override
    protected Resolution getResolution() {
        return STD_1080p;
    }

    @Override
    protected YCbCr getVideoParameters() {
        return BT709;
    }

    public static void main(String[] args) {
        Quantization1080p instance = new Quantization1080p();

        FxDisplay.show(instance.getResolution(),
                () -> instance.overlay(16, 128, false));
    }
}
