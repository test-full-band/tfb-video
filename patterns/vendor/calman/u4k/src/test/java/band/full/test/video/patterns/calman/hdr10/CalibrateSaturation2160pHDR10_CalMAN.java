package band.full.test.video.patterns.calman.hdr10;

import static band.full.test.video.encoder.EncoderParameters.HDR10;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateSaturationBase_CalMAN;

import java.util.stream.IntStream;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateSaturation2160pHDR10_CalMAN
        extends CalibrateSaturationBase_CalMAN {
    public CalibrateSaturation2160pHDR10_CalMAN() {
        super(HEVC, HDR10, "UHD4K/HDR10", "U4K_HDR10");
    }

    @Override
    protected IntStream stimulus() {
        return IntStream.of(25, 50);
    }
}
