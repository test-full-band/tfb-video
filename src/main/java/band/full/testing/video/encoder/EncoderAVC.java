package band.full.testing.video.encoder;

import static band.full.testing.video.itu.BT709.BT709;

import band.full.testing.video.core.Framerate;
import band.full.testing.video.core.Resolution;

import java.io.IOException;

/**
 * @author Igor Malinin
 */
public class EncoderAVC extends EncoderY4M {
    private EncoderAVC(String name, Resolution resolution, Framerate fps,
            int bitdepth) throws IOException {
        super(name, resolution, fps, BT709);
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
