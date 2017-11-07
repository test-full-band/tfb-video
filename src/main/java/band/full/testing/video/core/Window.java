package band.full.testing.video.core;

import static java.lang.Math.sqrt;

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

    public static Window square(Resolution resolution, double area) {
        if (area < 0.0 || area > 1.0) throw new IllegalArgumentException();

        int totalPixels = resolution.width * resolution.height;
        double windowPixels = totalPixels * area;
        int side = ((int) sqrt(windowPixels)) & ~0x1; // even number

        return center(resolution, side, side);
    }

    public static Window proportional(Resolution resolution, double area) {
        if (area < 0.0 || area > 1.0) throw new IllegalArgumentException();

        double mult = sqrt(area);
        int width = ((int) (resolution.width * mult)) & ~0x1; // even number
        int height = ((int) (resolution.height * mult)) & ~0x1; // even number

        return center(resolution, width, height);
    }

    public static Window center(Resolution resolution, int width, int height) {
        int x = (resolution.width - width) >> 1;
        int y = (resolution.height - height) >> 1;
        return new Window(x, y, width, height);
    }
}
