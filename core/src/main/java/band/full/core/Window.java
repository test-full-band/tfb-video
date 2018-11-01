package band.full.core;

import static java.lang.String.format;

/**
 * @author Igor Malinin
 */
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

    public Window shrink(int pixels) {
        var p2 = pixels * 2;
        return new Window(x + pixels, y + pixels, width - p2, height - p2);
    }

    public static Window screen(Resolution resolution) {
        return new Window(0, 0, resolution.width, resolution.height);
    }
}
