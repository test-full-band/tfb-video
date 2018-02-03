package band.full.testing.video.generate.hevc;

import static band.full.testing.video.core.Framerate.FPS_23_976;
import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;
import static band.full.testing.video.itu.BT709.BT709_10bit;
import static java.lang.String.format;

import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.QuantizationBase;
import band.full.testing.video.generate.QuantizationBase10;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quantization2160pBT709_10 extends QuantizationBase10 {
    private static final EncoderParameters UHD4K_BT709b10 =
            new EncoderParameters(STD_2160p, BT709_10bit, FPS_23_976);

    @Override
    protected void quants(String name, int yCode) {
        generate(name, yCode, HEVC, UHD4K_BT709b10);
    }

    @Override
    protected String getFileName(QuantizationBase.Args args) {
        return format(
                "HEVC/UHD4K/BT709_10/Quantization/QuantsBT709_10-Y%03d%s-%s",
                args.yMin, args.redChroma ? "Cr" : "Cb", args.suffix);
    }
}
