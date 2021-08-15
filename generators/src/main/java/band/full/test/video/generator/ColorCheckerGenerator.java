package band.full.test.video.generator;

import static band.full.core.ArrayMath.multiply;
import static band.full.core.Quantizer.round;
import static band.full.core.color.CIEXYZ.ILLUMINANT_D50;
import static band.full.core.color.ChromaticAdaptation.bradford;
import static band.full.test.video.generator.ColorChecker.CLASSIC_24;
import static band.full.test.video.generator.ColorChecker.DIGITAL_SG;
import static java.lang.Math.min;

import band.full.core.color.CIELab;
import band.full.core.color.Matrix3x3;
import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;
import band.full.video.buffer.FrameBuffer;

import java.util.List;

/**
 * @author Igor Malinin
 */
public class ColorCheckerGenerator extends GeneratorBase<Void> {
    public static class Classic24 extends ColorCheckerGenerator {
        public Classic24(GeneratorFactory factory,
                EncoderParameters params, NalUnitPostProcessor<Void> processor,
                MuxerFactory muxer, String folder, String group) {
            super(factory, params, processor, muxer,
                    folder, "ColorChecker24", group, CLASSIC_24);
        }
    }

    public static class DigitalSG extends ColorCheckerGenerator {
        public DigitalSG(GeneratorFactory factory,
                EncoderParameters params, NalUnitPostProcessor<Void> processor,
                MuxerFactory muxer, String folder, String group) {
            super(factory, params, processor, muxer,
                    folder, "ColorCheckerSG", group, DIGITAL_SG);
        }
    }

    private final Matrix3x3 ADAPTATION;
    private final List<List<CIELab>> patches;

    protected ColorCheckerGenerator(GeneratorFactory factory,
            EncoderParameters params, NalUnitPostProcessor<Void> processor,
            MuxerFactory muxer, String folder, String pattern, String group,
            List<List<CIELab>> patches) {
        super(factory, params, processor, muxer, folder, pattern, group);

        ADAPTATION = bradford(ILLUMINANT_D50, primaries.white.CIEXYZ());
        this.patches = patches;
    }

    @Override
    protected void encode(EncoderY4M e, Void args, String phase) {
        var fb = e.newFrameBuffer();

        patches(fb);

        e.render(gop, () -> fb);
    }

    private void patches(FrameBuffer fb) {
        int size = min(width / patches.size(),
                height / patches.get(0).size());

        int boxIn = (size - 16) & ~0xF;
        int boxOut = boxIn + 16;

        int x0 = (width - (boxOut) * patches.size()) / 2 + 8;
        int y0 = (height - (boxOut) * patches.get(0).size()) / 2 + 8;

        for (int i = 0; i < patches.size(); i++) {
            var col = patches.get(i);
            for (int j = 0; j < col.size(); j++) {
                double[] buf = col.get(j).CIEXYZ().array();

                double peak = transfer.getNominalDisplayPeakLuminance();
                if (peak > 100.0) { // Scale to 100 nit for HDR
                    multiply(buf, buf, 100.0 / peak);
                }

                matrix.XYZtoRGB.multiply(ADAPTATION.multiply(buf, buf), buf);

                int[] yuv = round(
                        matrix.toCodes(matrix.fromLinearRGB(buf, buf), buf));

                fb.fillRect(x0 + (i * boxOut), y0 + (j * boxOut),
                        boxIn, boxIn, yuv);
            }
        }
    }

    @Override
    protected void verify(DecoderY4M d, Void args) {
        d.read(fb -> {}); // TODO
    }
}
