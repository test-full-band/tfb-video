package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.UHD4K_MAIN8;
import static java.lang.String.format;

import band.full.testing.video.encoder.EncoderHEVC;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.QuantizationBase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quantization2160pBT709 extends QuantizationBase {
    @ParameterizedTest
    @ValueSource(ints = {16, 32})
    public void quantsNearBlack(int yCode) {
        quants("NearBlack", yCode); // 16
    }

    @ParameterizedTest
    @ValueSource(ints = {48, 64})
    public void quantsDarkGray(int yCode) {
        quants("DarkGray", yCode); // 16
    }

    @ParameterizedTest
    @ValueSource(ints = {96, 128})
    public void quantsGray(int yCode) {
        quants("Gray", yCode); // 32
    }

    @ParameterizedTest
    @ValueSource(ints = {160, 192})
    public void quantsLightGray(int yCode) {
        quants("LightGray", yCode); // 32
    }

    @ParameterizedTest
    @ValueSource(ints = 204)
    public void quantsNearWhite(int yCode) {
        quants("NearWhite", yCode); // 20
    }

    @ParameterizedTest
    @ValueSource(ints = 224)
    public void quantsBright(int yCode) {
        quants("Bright", yCode); // 20
    }

    private void quants(String name, int yCode) {
        String prefix = format(
                "HEVC/UHD4K/BT709/Quantization/QuantsBT709-Y%03d", yCode);

        EncoderHEVC.encode(prefix + "Cb-" + name, UHD4K_MAIN8,
                e -> quants(e, yCode, false),
                d -> verify(d, yCode, false));

        EncoderHEVC.encode(prefix + "Cr-" + name, UHD4K_MAIN8,
                e -> quants(e, yCode, true),
                d -> verify(d, yCode, true));
    }
}
