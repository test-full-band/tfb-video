package band.full.testing.video.core;

import java.time.Duration;

/**
 * @author Igor Malinin
 */
public class Framerate {
    public static final Framerate FPS_23_976
            = new Framerate("24000:1001", 24000f / 1001f);

    private final String str;
    public final float rate;

    Framerate(String str, float rate) {
        this.str = str;
        this.rate = rate;
    }

    public int toFrames(Duration duration) {
        return (int) (rate * duration.toNanos() / 1_000_000_000);
    }

    @Override
    public String toString() {
        return str;
    }
}
