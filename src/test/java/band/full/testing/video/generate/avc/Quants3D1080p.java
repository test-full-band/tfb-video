package band.full.testing.video.generate.avc;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;
import static band.full.testing.video.generate.GeneratorFactory.AVC;
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
@GenerateVideo(MAIN)
public class Quants3D1080p extends Quants3DBase {
    @ParameterizedTest
    @MethodSource("params")
    public void quantsNearBlack(Args args) {
        generate(AVC, FULLHD_MAIN8, args);
    }

    @Override
    protected String getFileName(Args args) {
        return format("AVC/FullHD/Quantization/Quants3D1080p-%s%d",
                args.speed, args.lsb);
    }
}
