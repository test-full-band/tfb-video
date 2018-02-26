package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.UHD4K_MAIN8;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.Quants3DBase;

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
        super(HEVC, UHD4K_MAIN8, "UHD4K/BT709/Quants3D", "Quants3D-4K709");
    }

    @ParameterizedTest
    @MethodSource("params")
    public void quants(Args args) {
        generate(args);
    }
}
