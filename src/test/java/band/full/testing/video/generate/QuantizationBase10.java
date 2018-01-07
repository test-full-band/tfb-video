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
public abstract class QuantizationBase10 extends QuantizationBase {
    @ParameterizedTest
    @ValueSource(ints = {64, 96})
    public void quantsNearBlack(int yCode) {
        quants("NearBlack", yCode); // 32
    }

    @ParameterizedTest
    @ValueSource(ints = {128, 160})
    public void quantsDarkGray(int yCode) {
        quants("DarkGray", yCode); // 32
    }

    @ParameterizedTest
    @ValueSource(ints = {192, 256})
    public void quantsGray(int yCode) {
        quants("Gray", yCode); // 64
    }

    @ParameterizedTest
    @ValueSource(ints = {320, 384})
    public void quantsLightGray(int yCode) {
        quants("LightGray", yCode); // 64
    }

    @ParameterizedTest
    @ValueSource(ints = {448, 512})
    public void quantsNearWhite(int yCode) {
        quants("NearWhite", yCode); // 64
    }

    @ParameterizedTest
    @ValueSource(ints = {576, 640})
    public void quantsBright(int yCode) {
        quants("Bright", yCode); // 64
    }

    @ParameterizedTest
    @ValueSource(ints = {704, 768})
    public void quantsBrighter(int yCode) {
        quants("Brighter", yCode); // 64
    }

    @ParameterizedTest
    @ValueSource(ints = {832, 876})
    public void quantsBrightest(int yCode) {
        quants("Brightest", yCode); // 44
    }
}
