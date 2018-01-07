package band.full.testing.video.generate.avc;

import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.executor.GenerateVideo.Type.MAIN;
import static band.full.testing.video.generate.GeneratorFactory.AVC;
import static java.lang.String.format;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.QuantizationBase;
import band.full.testing.video.generate.QuantizationBase8;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo(MAIN)
public class Quantization1080p extends QuantizationBase8 {
    @Override
    protected void quants(String name, int yCode) {
        generate(name, yCode, AVC, FULLHD_MAIN8);
    }

    @Override
    protected String getFileName(QuantizationBase.Args args) {
        return format("AVC/FullHD/Quantization/Quants1080p-Y%03d%s-%s",
                args.yMin, args.redChroma ? "Cr" : "Cb", args.suffix);
    }
}
