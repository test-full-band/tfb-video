package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.UHD4K_MAIN10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.QuantizationBase10;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quantization2160pBT2020_10 extends QuantizationBase10 {
    public Quantization2160pBT2020_10() {
        super(HEVC, UHD4K_MAIN10, "UHD4K/BT2020_10/Quantization",
                "QuantsBT2020_10");
    }
}
