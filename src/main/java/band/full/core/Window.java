package band.full.core;

import static band.full.core.Resolution.STD_1080p;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

public class Window {
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public Window(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return format("Window[%d, %d, %d, %d]", x, y, width, height);
    }

    public static Window screen(Resolution resolution) {
        return new Window(0, 0, resolution.width, resolution.height);
    }

    public static Window square(Resolution resolution, double area) {
        if (area < 0.0 || area > 1.0) throw new IllegalArgumentException();

        int totalPixels = resolution.width * resolution.height;
        double windowPixels = totalPixels * area;

        // assume screen sides are divisible by 4, make patch size
        // divisible by 8 -> align to 4 pixels, only reducing size
        int mask = resolution.height <= STD_1080p.height ? ~0x7 : ~0xF;
        int side = ((int) sqrt(windowPixels)) & mask;

        return center(resolution, side, side);
    }

    public static Window proportional(Resolution resolution, double area) {
        if (area < 0.0 || area > 1.0)
            throw new IllegalArgumentException("area = " + area);

        int mask = resolution.height <= STD_1080p.height ? ~0x7 : ~0xF;
        double mult = sqrt(area);
        int width = ((int) (resolution.width * mult)) & mask;
        int height = ((int) (resolution.height * mult)) & mask;

        return center(resolution, width, height);
    }

    public static Window center(Resolution resolution, int width, int height) {
        int x = (resolution.width - width) >> 1;
        int y = (resolution.height - height) >> 1;
        return new Window(x, y, width, height);
    }
}
