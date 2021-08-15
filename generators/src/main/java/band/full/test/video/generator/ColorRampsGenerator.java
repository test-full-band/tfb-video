package band.full.test.video.generator;

import static band.full.core.ArrayMath.multiply;
import static band.full.core.Quantizer.round;

import band.full.core.color.DCI_P3;
import band.full.core.color.Primaries;
import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;
import band.full.video.buffer.FrameBuffer;
import band.full.video.itu.BT2020;
import band.full.video.itu.BT709;

/**
 * @author Igor Malinin
 */
public class ColorRampsGenerator extends GeneratorBase<Void> {
    protected ColorRampsGenerator(GeneratorFactory factory,
            EncoderParameters params, NalUnitPostProcessor<Void> processor,
            MuxerFactory muxer, String folder, String group) {
        super(factory, params, processor, muxer, folder, "ColorRamps", group);
    }

    @Override
    protected void encode(EncoderY4M e, Void args, String phase) {
        var fb = e.newFrameBuffer();

        if (primaries == BT709.PRIMARIES) {
            ramps709(fb);
        } else {
            rampsWCG(fb);
        }

        e.render(gop, () -> fb);
    }

    private void ramps709(FrameBuffer fb) {
        int y = 0, h = height / 6;

        y = ramp(fb, y, h, primaries, 1.0, 0.0, 0.0); // R
        y = ramp(fb, y, h, primaries, 1.0, 1.0, 0.0); // Y
        y = ramp(fb, y, h, primaries, 0.0, 1.0, 0.0); // G
        y = ramp(fb, y, h, primaries, 0.0, 1.0, 1.0); // C
        y = ramp(fb, y, h, primaries, 0.0, 0.0, 1.0); // B
        y = ramp(fb, y, h, primaries, 1.0, 0.0, 1.0); // M
    }

    private void rampsWCG(FrameBuffer fb) {
        int y = 0, h = height / 12;

        y = ramp(fb, y, h, BT2020.PRIMARIES, 1.0, 0.0, 0.0); // R
        y = ramp(fb, y, h, DCI_P3.PRIMARIES_D65, 1.0, 0.0, 0.0); // R
        y = ramp(fb, y, h, BT709.PRIMARIES, 1.0, 0.0, 0.0); // R
        y = ramp(fb, y, h, BT2020.PRIMARIES, 1.0, 1.0, 0.0); // Y
        y = ramp(fb, y, h, BT2020.PRIMARIES, 0.0, 1.0, 0.0); // G
        y = ramp(fb, y, h, DCI_P3.PRIMARIES_D65, 0.0, 1.0, 0.0); // G
        y = ramp(fb, y, h, BT709.PRIMARIES, 0.0, 1.0, 0.0); // G
        y = ramp(fb, y, h, BT2020.PRIMARIES, 0.0, 1.0, 1.0); // C
        y = ramp(fb, y, h, BT2020.PRIMARIES, 0.0, 0.0, 1.0); // B
        y = ramp(fb, y, h, DCI_P3.PRIMARIES_D65, 0.0, 0.0, 1.0); // B
        y = ramp(fb, y, h, BT709.PRIMARIES, 0.0, 0.0, 1.0); // B
        y = ramp(fb, y, h, BT2020.PRIMARIES, 1.0, 0.0, 1.0); // M
    }

    int ramp(FrameBuffer fb, int y, int h, Primaries p, double... rgb) {
        double[] buf = new double[3];
        p.RGBtoXYZ.multiply(rgb, buf);
        matrix.XYZtoRGB.multiply(buf, buf);

        double peak = transfer.getNominalDisplayPeakLuminance();
        double max = peak > 400.0 ? transfer.fromLinear(400.0 / peak) : 1.0;

        double w = (width - 1) / max;
        double[] tmp = new double[3];

        for (int x = 0; x < width; x++) {
            multiply(buf, tmp, transfer.toLinear(x / w));
            matrix.toCodes(matrix.fromLinearRGB(tmp, tmp), tmp);
            fb.fillRect(x, y + 2, 1, h - 4, round(tmp));
        }

        if (peak > 100.0) {
            double sdr = transfer.fromLinear(100.0 / peak);
            fb.fillRect((int) ((width - 1) * sdr / max) - 1, 0, 2, height,
                    0, matrix.ACHROMATIC, matrix.ACHROMATIC);
        }

        return y + h;
    }

    @Override
    protected void verify(DecoderY4M d, Void args) {
        d.read(fb -> {}); // TODO
    }
}
