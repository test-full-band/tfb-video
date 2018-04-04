package band.full.test.video.generator;

import static java.time.Duration.ofSeconds;

import band.full.core.Resolution;
import band.full.video.buffer.Framerate;
import band.full.video.encoder.DecoderY4M;
import band.full.video.encoder.EncoderParameters;
import band.full.video.encoder.EncoderY4M;
import band.full.video.itu.ColorMatrix;
import band.full.video.itu.TransferCharacteristics;

import java.time.Duration;

/**
 * @author Igor Malinin
 */
public abstract class ParameterizedGeneratorBase<A> {
    protected static final Duration DURATION_INTRO = ofSeconds(5);
    protected static final Duration DURATION_BODY = ofSeconds(25);
    protected static final Duration DURATION_STATIC = ofSeconds(30);

    public final GeneratorFactory factory;
    public final EncoderParameters params;
    public final Resolution resolution;
    public final ColorMatrix matrix;
    public final Framerate framerate;
    public final TransferCharacteristics transfer;
    public final int width, height;

    public final String folder, pattern;

    public ParameterizedGeneratorBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        this.factory = factory;
        this.params = params;
        this.folder = folder;
        this.pattern = pattern;

        resolution = params.resolution;
        matrix = params.matrix;
        framerate = params.framerate;

        transfer = matrix.transfer;

        width = resolution.width;
        height = resolution.height;
    }

    public void generate(A args) {
        factory.generate(getFolder(args), getPattern(args), params, args,
                this::encode, this::verify);
    }

    protected String getFolder(A args) {
        return factory.folder + '/' + folder;
    }

    protected abstract String getPattern(A args);

    protected abstract void encode(EncoderY4M e, A args);

    protected abstract void verify(DecoderY4M d, A args);
}
