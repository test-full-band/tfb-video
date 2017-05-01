package band.full.testing.video.core;

/**
 * @author Igor Malinin
 */
public enum Resolution {
    STD_720p(1280, 720),
    STD_1080p(1920, 1080),
    STD_2160p(3840, 2160);

    public final int width;
    public final int height;

    private Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
