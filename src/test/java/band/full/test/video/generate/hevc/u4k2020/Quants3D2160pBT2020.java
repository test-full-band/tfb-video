package band.full.test.video.generate.hevc.u4k2020;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.UHD4K_MAIN10;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants3DBase;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants3D2160pBT2020 extends Quants3DBase {
    public Quants3D2160pBT2020() {
        super(HEVC, UHD4K_MAIN10, "UHD4K/BT2020_10/Quants3D", "U4K_2020");
    }
}
