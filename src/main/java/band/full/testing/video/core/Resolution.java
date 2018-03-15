package band.full.testing.video.core;

import static java.lang.String.format;

/**
 * @author Igor Malinin
 */
public class Resolution {
    public static final Resolution STD_720p = new Resolution(1280, 720);
    public static final Resolution STD_1080p = new Resolution(1920, 1080);
    public static final Resolution STD_2160p = new Resolution(3840, 2160);
    public static final Resolution STD_4320p = new Resolution(7680, 4320);

    public final int width;
    public final int height;

    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return format("[%d, %d]", width, height);
    }
}
