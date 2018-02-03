package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.UHD4K_MAIN10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;
import static java.lang.String.format;

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
public class Quants3D2160pBT2020 extends Quants3DBase {
    @ParameterizedTest
    @MethodSource("params")
    public void quantsNearBlack(Args args) {
        generate(HEVC, UHD4K_MAIN10, args);
    }

    @Override
    protected String getFileName(Args args) {
        return format("HEVC/UHD4K/BT2020/Quantization/Quants3D1080p4K2020-%s%d",
                args.speed, args.lsb);
    }
}
