package band.full.testing.video.generate;

import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderAVC;
import band.full.testing.video.encoder.EncoderHEVC;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;

import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public enum GeneratorFactory {
    AVC(EncoderAVC::encode),
    HEVC(EncoderHEVC::encode);

    public final Encoder encoder;

    GeneratorFactory(Encoder encoder) {
        this.encoder = encoder;
    }

    public void generate(String name, EncoderParameters parameters,
            Consumer<EncoderY4M> ec, Consumer<DecoderY4M> dc) {
        encoder.encode(name, parameters, ec);
        DecoderY4M.decode(name, parameters, dc);
    }

    @FunctionalInterface
    interface Encoder {
        void encode(String name, EncoderParameters parameters,
                Consumer<EncoderY4M> consumer);
    }
}
