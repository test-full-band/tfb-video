package band.full.test.video.generator;

import static band.full.core.Quantizer.round;

import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;

/**
 * TODO Gamma checking
 *
 * @author Igor Malinin
 */
public class GammaGenerator extends GeneratorBase<Void> {
    public GammaGenerator(GeneratorFactory factory,
            EncoderParameters params, NalUnitPostProcessor<Void> processor,
            MuxerFactory muxer, String folder, String group) {
        super(factory, params, processor, muxer, folder, "Gamma", group);
    }

    @Override
    protected void encode(EncoderY4M e, Void args, String phase) {
        var fb = e.newFrameBuffer();

        // half-toned horizontal lines
        for (int i = 1; i < height; i += 2) {
            fb.Y.fillRect(0, i, width, 1,
                    round(matrix.toLumaCode((height - i) / (double) height)));
        }

        // fill vertical references
        int step = width / 240, w = step / 2;
        for (int i = 0; i < width; i += step) {
            fb.Y.fillRect(i, 0, w, height,
                    round(matrix.toLumaCode(i / (double) width)));
        }

        e.render(gop, () -> fb);
    }

    @Override
    protected void verify(DecoderY4M d, Void args) {
        d.read(fb -> {});
    }
}
