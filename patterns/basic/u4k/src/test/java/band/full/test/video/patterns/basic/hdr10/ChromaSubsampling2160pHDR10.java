package band.full.test.video.patterns.basic.hdr10;

import static band.full.test.video.encoder.EncoderParameters.HDR10;
import static band.full.test.video.executor.GenerateVideo.Type.LOSSLESS;
import static band.full.test.video.generator.GeneratorFactory.HEVC;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.ChromaSubsamplingBase;

/**
 * Testing quality of chroma upsampling.
 *
 * @author Igor Malinin
 */
@GenerateVideo(LOSSLESS)
public class ChromaSubsampling2160pHDR10 extends ChromaSubsamplingBase {
    public ChromaSubsampling2160pHDR10() {
        super(HEVC, HDR10, "UHD4K/HDR10/Chroma", "U4K_HDR10");
    }
}
