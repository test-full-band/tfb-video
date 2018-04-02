package band.full.test.video.generate.hevc.u4k;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.UHD4K_MAIN8;

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
public class Quants3D2160pBT709 extends Quants3DBase {
    public Quants3D2160pBT709() {
        super(HEVC, UHD4K_MAIN8, "UHD4K/BT709/Quants3D", "U4K");
    }

    @ParameterizedTest
    @MethodSource("params")
    public void quants(Args args) {
        generate(args);
    }
}
