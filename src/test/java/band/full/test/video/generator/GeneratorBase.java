package band.full.test.video.generator;

import band.full.core.Resolution;
import band.full.video.buffer.Framerate;
import band.full.video.encoder.DecoderY4M;
import band.full.video.encoder.EncoderParameters;
import band.full.video.encoder.EncoderY4M;
import band.full.video.itu.ColorMatrix;
import band.full.video.itu.TransferCharacteristics;

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
    public final TransferCharacteristics transfer;
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

        transfer = matrix.transfer;

        width = resolution.width;
        height = resolution.height;
    }

    public void generate() {
        factory.generate(getFileName(), params, this::encode, this::verify);
    }

    protected String getFileName() {
        return factory.folder + '/' + folder + '/' + pattern;
    }

    protected abstract void encode(EncoderY4M e);

    protected abstract void verify(DecoderY4M d);
}
