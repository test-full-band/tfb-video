package band.full.testing.video.generate.base;

import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.GeneratorFactory;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2DBase8 extends Quants2DBase {
    protected Quants2DBase8(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, pattern);
    }

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
