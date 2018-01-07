package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;
import static java.lang.String.format;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.QuantizationBase;
import band.full.testing.video.generate.QuantizationBase10;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quantization2160pHDR10 extends QuantizationBase10 {
    @Override
    protected void quants(String name, int yMin) {
        generate(name, yMin, HEVC, HDR10);
    }

    @Override
    protected String getFileName(QuantizationBase.Args args) {
        return format("HEVC/UHD4K/HDR10/Quantization/QuantsHDR10-Y%03d%s-%s",
                args.yMin, args.redChroma ? "Cr" : "Cb", args.suffix);
    }
}
