package band.full.testing.video.generate.hdr10;

import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.executor.GenerateVideo.Type.LOSSLESS;
import static band.full.testing.video.generator.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generator.ChromaSubsamplingBase;

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
