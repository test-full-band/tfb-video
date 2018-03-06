package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.Quants3DBase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants3D1080pHEVC extends Quants3DBase {
    public Quants3D1080pHEVC() {
        super(HEVC, FULLHD_MAIN8, "FullHD/Quants3D", "Quants3D1080pHEVC");
    }

    @ParameterizedTest
    @MethodSource("params")
    public void quants(Args args) {
        generate(args);
    }
}
