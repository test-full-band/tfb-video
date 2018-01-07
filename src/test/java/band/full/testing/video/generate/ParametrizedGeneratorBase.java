package band.full.testing.video.generate;

import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;

/**
 * @author Igor Malinin
 */
public abstract class ParametrizedGeneratorBase<A> {
    public void generate(GeneratorFactory factory,
            EncoderParameters ep, A args) {
        factory.generate(getFileName(args), ep, args,
                this::encode, this::verify);
    }

    protected abstract String getFileName(A args);

    protected abstract void encode(EncoderY4M e, A args);

    protected abstract void verify(DecoderY4M d, A args);
}
