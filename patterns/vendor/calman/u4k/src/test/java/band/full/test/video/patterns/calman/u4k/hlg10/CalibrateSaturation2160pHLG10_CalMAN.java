package band.full.test.video.patterns.calman.u4k.hlg10;

import static band.full.test.video.encoder.EncoderParameters.HLG10;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateSaturationBase_CalMAN;

import java.util.stream.IntStream;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateSaturation2160pHLG10_CalMAN
        extends CalibrateSaturationBase_CalMAN {
    public CalibrateSaturation2160pHLG10_CalMAN() {
        super(HEVC, HLG10, "UHD4K/HLG10", "U4K_HLG10");
    }

    @Override
    protected IntStream stimulus() {
        return IntStream.of(25, 50, 75);
    }
}
