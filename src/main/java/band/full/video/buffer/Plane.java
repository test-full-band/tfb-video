package band.full.video.buffer;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Arrays;
import java.util.function.IntBinaryOperator;

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

    private static int limit(int value, int limit) {
        return min(max(value, 0), limit);
    }
}
