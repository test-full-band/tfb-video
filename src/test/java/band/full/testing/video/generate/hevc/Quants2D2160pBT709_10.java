package band.full.testing.video.generate.hevc;

import static band.full.testing.video.core.Framerate.FPS_23_976;
import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;
import static band.full.testing.video.itu.BT709.BT709_10bit;

import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.Quants2DBase10;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2D2160pBT709_10 extends Quants2DBase10 {
    private static final EncoderParameters UHD4K_BT709b10 =
            new EncoderParameters(STD_2160p, BT709_10bit, FPS_23_976);

    public Quants2D2160pBT709_10() {
        super(HEVC, UHD4K_BT709b10, "UHD4K/BT709_10/Quants2D", "U4K_10");
    }
}
