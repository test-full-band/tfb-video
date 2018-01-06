package band.full.testing.video.generate;

import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;

/**
 * @author Igor Malinin
 */
public abstract class GeneratorBase {
    public void generate(String name, GeneratorFactory factory,
            EncoderParameters parameters) {
        factory.generate(name, parameters, this::encode, this::verify);
    }

    protected abstract void encode(EncoderY4M e);

    protected abstract void verify(DecoderY4M d);
}
