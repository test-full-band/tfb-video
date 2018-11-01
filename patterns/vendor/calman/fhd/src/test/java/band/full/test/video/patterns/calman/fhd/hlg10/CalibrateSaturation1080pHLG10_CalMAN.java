package band.full.test.video.patterns.calman.fhd.hlg10;

import static band.full.core.Resolution.STD_1080p;
import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.buffer.Framerate.FPS_23_976;
import static band.full.video.itu.BT2100.HLG10;

import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateSaturationBase_CalMAN;

import java.util.stream.IntStream;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateSaturation1080pHLG10_CalMAN
        extends CalibrateSaturationBase_CalMAN {
    public CalibrateSaturation1080pHLG10_CalMAN() {
        super(HEVC, new EncoderParameters(STD_1080p, HLG10, FPS_23_976),
                "FullHD/HLG10", "FHD_HLG10");
    }

    @Override
    protected IntStream stimulus() {
        return IntStream.of(25, 50, 75);
    }
}
