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

        return new Window((resolution.width - side) >> 1,
                (resolution.height - side) >> 1, side, side);
    }
}
