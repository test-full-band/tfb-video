package band.full.testing.video.core;

import static java.util.stream.IntStream.range;

import java.util.Arrays;

/**
 * @author Igor Malinin
 */
public class Plane {
    public final int width;
    public final int height;

    public final short[] pixels;

    public Plane(int width, int height) {
        this.width = width;
        this.height = height;

        pixels = new short[width * height];
    }

    public int get(int x, int y) {
        return pixels[y * width + x];
    }

    public void set(int x, int y, int value) {
        pixels[y * width + x] = (short) value;
    }

    public void fillRect(int x, int y, int w, int h, int value) {
        int x1 = limit(x, width);
        int y1 = limit(y, height);
        int x2 = limit(x + w, width);
        int y2 = limit(y + h, height);

        range(y1, y2).forEach(iy -> {
            int base = iy * width;
            Arrays.fill(pixels, base + x1, base + x2, (short) value);
        });
    }

    public void calculate(PointToValue producer) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                set(x, y, producer.get(x, y));
            }
        }
    }

    public void fill(int value) {
        Arrays.fill(pixels, (short) value);
    }

    private int limit(int value, int limit) {
        return (value < 0) ? 0 : (value >= limit) ? limit : value;
    }
}
