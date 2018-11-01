package band.full.test.video.generator;

import static java.util.function.Function.identity;

import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.executor.GenerateVideo;

import java.util.stream.Stream;

/**
 * Testing color bands separation / quantization step uniformity.
 * <p>
 * SDR 8bit specialized version.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public abstract class Quants2DBase8 extends Quants2DBase {
    protected Quants2DBase8(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder, group);
    }

    @Override
    protected Stream<Args> args() {
        return Stream.of(
                quants("NearBlack", 16),
                quants("DarkGray", 40, 64),
                quants("Gray", 96, 128),
                quants("LightGray", 160, 192),
                // NB! The last video data range code is 254!
                quants("NearWhite", 223) // 254-32+1
        ).flatMap(identity());
    }
}
