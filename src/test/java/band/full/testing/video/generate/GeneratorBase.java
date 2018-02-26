package band.full.testing.video.generate;

import band.full.testing.video.core.Framerate;
import band.full.testing.video.core.Resolution;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.itu.ColorMatrix;

/**
 * @author Igor Malinin
 */
public abstract class GeneratorBase {
    public final GeneratorFactory factory;
    public final EncoderParameters params;
    public final String folder, pattern;

    // direct access to commonly used parameters
    public final Resolution resolution;
    public final ColorMatrix matrix;
    public final Framerate framerate;
    public final int width, height;

    public GeneratorBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        this.factory = factory;
        this.params = params;
        this.folder = folder;
        this.pattern = pattern;

        resolution = params.resolution;
        matrix = params.matrix;
        framerate = params.framerate;

        width = resolution.width;
        height = resolution.height;
    }

    public void generate() {
        factory.generate(getFileName(), params, this::encode, this::verify);
    }

    protected String getFileName() {
        return factory.name() + '/' + folder + '/' + pattern;
    }

    protected abstract void encode(EncoderY4M e);

    protected abstract void verify(DecoderY4M d);
}
