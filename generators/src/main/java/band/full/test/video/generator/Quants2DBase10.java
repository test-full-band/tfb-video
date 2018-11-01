package band.full.test.video.generator;

import static java.util.function.Function.identity;

import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.executor.GenerateVideo;

import java.util.stream.Stream;

/**
 * Testing color bands separation / quantization step uniformity.
 * <p>
 * SDR 10bit specialized version.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public abstract class Quants2DBase10 extends Quants2DBase {
    protected Quants2DBase10(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder, group);
    }

    @Override
    protected Stream<Args> args() {
        return Stream.of(
                quants("NearBlack", 64, 96),
                quants("DarkGray", 128, 192),
                quants("Gray", 256, 384),
                quants("LightGray", 512, 640),
                quants("NearWhite", 768, 896),
                quants("Bright", 944)
        ).flatMap(identity());
    }
}
