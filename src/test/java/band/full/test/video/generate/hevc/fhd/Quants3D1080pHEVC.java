package band.full.test.video.generate.hevc.fhd;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.FULLHD_MAIN8;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants3DBase;

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
        super(HEVC, FULLHD_MAIN8, "FullHD/Quants3D", "FHD");
    }

    @ParameterizedTest
    @MethodSource("params")
    public void quants(Args args) {
        generate(args);
    }
}
