package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.BlackLevelBase;

import org.junit.jupiter.api.Test;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class BlackLevel2160pHDR10 extends BlackLevelBase {
    @Test
    public void generate() {
        generate("HEVC/UHD4K/HDR10/Calibrate/Basic/BlackLevel2160pHDR10",
                HEVC, HDR10);
    }
}
