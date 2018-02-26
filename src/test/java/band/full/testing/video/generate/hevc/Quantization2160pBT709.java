package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.UHD4K_MAIN8;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.QuantizationBase8;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quantization2160pBT709 extends QuantizationBase8 {
    public Quantization2160pBT709() {
        super(HEVC, UHD4K_MAIN8, "UHD4K/BT709/Quantization", "QuantsBT709");
    }
}
