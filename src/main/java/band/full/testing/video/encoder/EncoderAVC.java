package band.full.testing.video.encoder;

import java.io.IOException;

/**
 * @author Igor Malinin
 */
public class EncoderAVC extends EncoderY4M {
    private EncoderAVC(String name, EncoderParameters parameters)
            throws IOException {
        super(name, parameters);
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
