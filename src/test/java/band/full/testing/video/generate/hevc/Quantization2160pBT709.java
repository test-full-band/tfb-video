package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.UHD4K_MAIN8;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;
import static java.lang.String.format;

import band.full.testing.video.generate.QuantizationBase;
import band.full.testing.video.generate.QuantizationBase8;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
public abstract class Quantization2160pBT709 extends QuantizationBase8 {
    @Override
    protected void quants(String name, int yCode) {
        generate(name, yCode, HEVC, UHD4K_MAIN8);
    }

    @Override
    protected String getFileName(QuantizationBase.Args args) {
        return format("HEVC/UHD4K/BT709/Quantization/QuantsBT709-Y%03d%s-%s",
                args.yMin, args.redChroma ? "Cr" : "Cb", args.suffix);
    }
}
