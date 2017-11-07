package band.full.testing.video.core;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    public Plane(int width, int height, int fill) {
        this(width, height);
        Arrays.fill(pixels, (short) fill);
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

        for (int iy = y1; iy < y2; iy++) {
            int base = iy * width;
            Arrays.fill(pixels, base + x1, base + x2, (short) value);
        }
    }

    /** Lossless target, verify 100% matching with intent. */
    public void verifyRect(int x, int y, int w, int h, int expected) {
        verifyRect(x, y, w, h, expected, 0, 0);
    }

    public void verifyRect(int x, int y, int w, int h, int expected,
            int deviation, double maxMisses) {
        int x1 = limit(x, width);
        int y1 = limit(y, height);
        int x2 = limit(x + w, width);
        int y2 = limit(y + h, height);

        int intMisses = (int) ((x2 - x1) * (y2 - y1) * maxMisses);

        verifyRect(x, y, w, h, expected, deviation, intMisses);
    }

    public void verifyRect(int x, int y, int w, int h, int expected,
            int deviation, int maxMisses) {
        int x1 = limit(x, width);
        int y1 = limit(y, height);
        int x2 = limit(x + w, width);
        int y2 = limit(y + h, height);

        int count = 0, total = (y2 - y1) * (x2 - x1);

        for (int iy = y1; iy < y2; iy++) {
            int base = iy * width;
            count += verify(base + x1, base + x2, expected, deviation);
        }

        assertFalse(count + maxMisses < total);
    }

    private int verify(int from, int to, int expected, int deviation) {
        int count = 0;

        for (int i = from; i < to; i++) {
            int delta = pixels[i] - expected;

            if (delta == 0) {
                ++count;
            } else {
                assertFalse(abs(delta) > deviation);
            }
        }

        return count;
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
