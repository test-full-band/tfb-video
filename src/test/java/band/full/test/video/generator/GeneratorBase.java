package band.full.test.video.generator;

import static java.lang.Math.round;
import static java.util.Collections.emptyList;
import static java.util.stream.IntStream.range;

import band.full.core.Resolution;
import band.full.video.buffer.Framerate;
import band.full.video.encoder.DecoderY4M;
import band.full.video.encoder.EncoderParameters;
import band.full.video.encoder.EncoderY4M;
import band.full.video.encoder.MuxerMP4;
import band.full.video.itu.ColorMatrix;
import band.full.video.itu.TransferCharacteristics;

import java.io.File;
import java.io.IOException;

/**
 * @author Igor Malinin
 */
public abstract class GeneratorBase<A> {
    protected static final int PATTERN_SECONDS = 30;
    protected static final int INTRO_SECONDS = 5;
    protected static final int BODY_SECONDS = PATTERN_SECONDS - INTRO_SECONDS;

    public final GeneratorFactory factory;
    public final EncoderParameters params;
    public final String folder, pattern;

    // direct access to commonly used parameters
    public final Resolution resolution;
    public final ColorMatrix matrix;
    public final Framerate framerate;
    public final TransferCharacteristics transfer;
    public final int width, height, gop;

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

        gop = round(framerate.rate);
    }

    public void generate(A args) {
        String pattern = getPattern(args);
        File dir = factory.greet(getFolder(args), pattern);
        try {
            MuxerMP4 muxer = new MuxerMP4(dir,
                    pattern, factory.brand, emptyList());

            generate(muxer, dir, args);
            String mp4 = muxer.mux();
            muxer.deleteInputs();
            DecoderY4M.decode(dir, mp4, params, d -> verify(d, args));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void generate(MuxerMP4 muxer, File dir, A args)
            throws IOException, InterruptedException {
        encode(muxer, dir, args, null, PATTERN_SECONDS);
    }

    public void encode(MuxerMP4 muxer, File dir,
            A args, String phase, int repeat)
            throws IOException, InterruptedException {
        String pattern = getPattern(args);
        String name = phase == null ? pattern : pattern + "-" + phase;

        String out = factory.encode(dir, name, params,
                e -> encode(e, args, phase));

        range(0, repeat).forEach(i -> muxer.addInput(out));
    }

    protected String getFolder(A args) {
        return factory.folder + '/' + folder;
    }

    protected String getPattern(A args) {
        return pattern;
    }

    protected abstract void encode(EncoderY4M e, A args, String phase);

    protected abstract void verify(DecoderY4M d, A args);
}
