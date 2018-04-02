package band.full.video.buffer;

import static java.lang.Math.abs;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

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

    public void verify(Plane expected) {
        verify(expected, 0, 0);
    }

    public void verify(Plane expected, int deviation, double maxMisses) {
        int intMisses = (int) (width * height * maxMisses);

        verify(expected, deviation, intMisses);
    }

    public void verify(Plane expected, int deviation, int maxMisses) {
        int count = range(0, height).parallel().map(iy -> {
            int base = iy * width;
            return verify(base, base + width, expected.pixels, deviation);
        }).sum();

        assertFalse(count + maxMisses < width * height);
    }

    private int verify(int from, int to, short[] expected, int deviation) {
        int count = 0;

        for (int i = from; i < to; i++) {
            int delta = pixels[i] - expected[i];

            if (delta == 0) {
                ++count;
            } else {
                assertFalse(abs(delta) > deviation);
            }
        }

        return count;
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

        IntStream range = range(y1, y2);
        if (x2 - x1 > 64) { // TODO measure when to switch
            range = range.parallel();
        }

        int count = range.map(iy -> {
            int base = iy * width;
            return verify(base + x1, base + x2, expected, deviation);
        }).sum();

        assertFalse(count + maxMisses < (y2 - y1) * (x2 - x1));
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

    public void calculate(IntBinaryOperator op) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                set(x, y, op.applyAsInt(x, y));
            }
        }
    }

    public void calculateRect(int x, int y, int w, int h,
            IntBinaryOperator op) {
        for (int cy = 0; cy < h; cy++) {
            for (int cx = 0; cx < w; cx++) {
                set(cx + x, cy + y, op.applyAsInt(cx, cy));
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
