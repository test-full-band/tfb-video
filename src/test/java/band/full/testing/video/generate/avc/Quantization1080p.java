package band.full.testing.video.generate.avc;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;
import static java.lang.String.format;

import band.full.testing.video.encoder.EncoderAVC;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.QuantizationBase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class Quantization1080p extends QuantizationBase {
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
                "AVC/FullHD/Quantization/Quants1080p-Y%03d", yCode);

        EncoderAVC.encode(prefix + "Cb-" + name, FULLHD_MAIN8,
                e -> quants(e, yCode, false),
                d -> verify(d, yCode, false));

        EncoderAVC.encode(prefix + "Cr-" + name, FULLHD_MAIN8,
                e -> quants(e, yCode, true),
                d -> verify(d, yCode, true));
    }
}
