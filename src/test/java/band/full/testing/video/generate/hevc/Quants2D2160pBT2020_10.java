package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.UHD4K_MAIN10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.Quants2DBase10;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2D2160pBT2020_10 extends Quants2DBase10 {
    public Quants2D2160pBT2020_10() {
        super(HEVC, UHD4K_MAIN10, "UHD4K/BT2020_10/Quantization",
                "U4K_BT2020_10");
    }
}
