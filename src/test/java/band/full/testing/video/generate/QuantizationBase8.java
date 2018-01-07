package band.full.testing.video.generate;

import band.full.testing.video.executor.GenerateVideo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public abstract class QuantizationBase8 extends QuantizationBase {
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
}
