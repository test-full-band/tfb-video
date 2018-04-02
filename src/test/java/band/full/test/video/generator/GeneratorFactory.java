package band.full.test.video.generator;

import band.full.video.encoder.DecoderY4M;
import band.full.video.encoder.EncoderAVC;
import band.full.video.encoder.EncoderHEVC;
import band.full.video.encoder.EncoderParameters;
import band.full.video.encoder.EncoderY4M;

import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public enum GeneratorFactory {
    AVC(EncoderAVC::encode, "H.264 AVC"),
    HEVC(EncoderHEVC::encode, "H.265 HEVC");

    public final Encoder encoder;
    public final String folder;

    GeneratorFactory(Encoder encoder, String folder) {
        this.encoder = encoder;
        this.folder = folder;
    }

    public void generate(String name, EncoderParameters ep,
            Consumer<EncoderY4M> ec, Consumer<DecoderY4M> dc) {
        encoder.encode(name, ep, ec);
        DecoderY4M.decode(name, ep, dc);
    }

    public <A> void generate(String name, EncoderParameters ep, A args,
            ParametrizedConsumer<EncoderY4M, A> ec,
            ParametrizedConsumer<DecoderY4M, A> dc) {
        encoder.encode(name, ep, e -> ec.accept(e, args));
        DecoderY4M.decode(name, ep, d -> dc.accept(d, args));
    }

    @FunctionalInterface
    interface Encoder {
        void encode(String name, EncoderParameters ep,
                Consumer<EncoderY4M> consumer);
    }

    @FunctionalInterface
    interface ParametrizedConsumer<T, A> {
        void accept(T t, A args);
    }
}
