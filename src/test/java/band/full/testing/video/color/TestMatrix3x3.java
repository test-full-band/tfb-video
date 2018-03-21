package band.full.testing.video.color;

import static java.lang.Double.MIN_VALUE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestMatrix3x3 {
    @Test
    public void invert() {
        var a = new Matrix3x3(
                1.0, 2.0, 3.0,
                2.0, 5.0, 3.0,
                1.0, 0.0, 8.0);

        var b = a.invert();

        var expected = new Matrix3x3(
                -40.0, 16.0, 9.0,
                13.0, -5.0, -3.0,
                5.0, -2.0, -1.0);

        assertArrayEquals(expected.values[0], b.values[0], MIN_VALUE);
        assertArrayEquals(expected.values[1], b.values[1], MIN_VALUE);
        assertArrayEquals(expected.values[2], b.values[2], MIN_VALUE);

        var c = b.invert();

        assertArrayEquals(a.values[0], c.values[0], MIN_VALUE);
        assertArrayEquals(a.values[1], c.values[1], MIN_VALUE);
        assertArrayEquals(a.values[2], c.values[2], MIN_VALUE);
    }

    @Test
    public void multiply() {
        var a = new Matrix3x3(
                1.0, 2.0, 3.0,
                4.0, 5.0, 6.0,
                7.0, 8.0, 9.0);

        var b = a.multiply(a);

        var expected = new Matrix3x3(
                30.0, 36.0, 42.0,
                66.0, 81.0, 96.0,
                102.0, 126.0, 150.0);

        assertArrayEquals(expected.values[0], b.values[0]);
        assertArrayEquals(expected.values[1], b.values[1]);
        assertArrayEquals(expected.values[2], b.values[2]);
    }

    @Test
    public void symmetry() {
        var matrix = new Matrix3x3(
                1.0, 2.0, 3.0,
                2.0, 5.0, 3.0,
                1.0, 0.0, 8.0);

        var inverse = matrix.invert();

        var mult = matrix.multiply(inverse);

        var identity = new Matrix3x3(
                1.0, 0.0, 0.0,
                0.0, 1.0, 0.0,
                0.0, 0.0, 1.0);

        assertArrayEquals(identity.values[0], mult.values[0]);
        assertArrayEquals(identity.values[1], mult.values[1]);
        assertArrayEquals(identity.values[2], mult.values[2]);
    }
}
