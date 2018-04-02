package band.full.video.buffer;

import java.time.Duration;

/**
 * @author Igor Malinin
 */
public class Framerate {
    public static final Framerate FPS_23_976 =
            new Framerate("24000:1001", 24_000f / 1_001f);

    public static final Framerate FPS_24 = new Framerate("24:1", 24f);

    public static final Framerate FPS_29_97 =
            new Framerate("30000:1001", 30_000f / 1_001f);

    public static final Framerate FPS_30 = new Framerate("30:1", 30f);

    public static final Framerate FPS_59_94 =
            new Framerate("60000:1001", 60_000f / 1_001f);

    public static final Framerate FPS_60 = new Framerate("60:1", 60f);

    public static int toFrames(float rate, Duration duration) {
        return (int) (rate * duration.toNanos() / 1_000_000_000);
    }

    private final String str;
    public final float rate;

    Framerate(String str, float rate) {
        this.str = str;
        this.rate = rate;
    }

    public int toFrames(Duration duration) {
        return toFrames(rate, duration);
    }

    @Override
    public String toString() {
        return str;
    }
}
