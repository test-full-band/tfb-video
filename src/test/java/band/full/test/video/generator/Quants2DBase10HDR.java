package band.full.test.video.generator;

import static java.util.function.Function.identity;

import band.full.test.video.executor.GenerateVideo;
import band.full.video.encoder.EncoderParameters;

import java.util.stream.Stream;

/**
 * Testing color bands separation / quantization step uniformity.
 * <p>
 * HDR PQ specialized version.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2DBase10HDR extends Quants2DBase {
    protected Quants2DBase10HDR(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, pattern);
    }

    @Override
    protected Stream<Args> args() {
        return Stream.of(
                quants("NearBlack", 64, 96),
                quants("DarkGray", 128, 192),
                quants("Gray", 256),
                quants("LightGray", 384),
                quants("NearWhite", 512),
                quants("Bright", 640),
                quants("Brighter", 768),
                quants("Brightest", 896)
        ).flatMap(identity());
    }
}
