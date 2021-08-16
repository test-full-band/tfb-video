package band.full.core;

import static java.lang.String.format;

/**
 * @author Igor Malinin
 */
public record Window(int x, int y, int width, int height) {
    public static Window screen(Resolution resolution) {
        return new Window(0, 0, resolution.width(), resolution.height());
    }

    public Window shrink(int pixels) {
        var p2 = pixels * 2;
        return new Window(x + pixels, y + pixels, width - p2, height - p2);
    }

    @Override
    public String toString() {
        return format("Window[%d, %d, %d, %d]", x, y, width, height);
    }
}
