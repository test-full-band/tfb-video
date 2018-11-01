package band.full.test.video.patterns.basic.fhd.hlg10;

import static band.full.core.Resolution.STD_1080p;
import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.buffer.Framerate.FPS_23_976;
import static band.full.video.itu.BT2100.HLG10;

import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants3DBase;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants3D1080pHLG10 extends Quants3DBase {
    public Quants3D1080pHLG10() {
        super(HEVC, new EncoderParameters(STD_1080p, HLG10, FPS_23_976),
                "FullHD/HLG10/Quants3D", "FHD_HLG10");
    }
}
