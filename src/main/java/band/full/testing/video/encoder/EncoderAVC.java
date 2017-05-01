package band.full.testing.video.encoder;

import java.io.IOException;

import band.full.testing.video.core.Framerate;
import band.full.testing.video.core.Resolution;

/**
 * @author Igor Malinin
 */
public class EncoderAVC extends EncoderY4M {
    public EncoderAVC(String name, Resolution resolution, Framerate fps,
            int bitdepth) throws IOException {
        super(name, resolution, fps, bitdepth);
    }

    @Override
    public boolean checkBitdepth(int depth) {
        return depth == 8;
    }

    @Override
    public String getExecutable() {
        return "x264";
    }

    @Override
    public String getFFMpegFormat() {
        return "avc";
    }
}
