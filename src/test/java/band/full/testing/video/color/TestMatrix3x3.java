package band.full.testing.video.color;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

/**
 * @author Igor Malinin
 */
public class TestMatrix3x3 {
    @Test
    public void invert() {
        Matrix3x3 a = new Matrix3x3(
                1.0, 2.0, 3.0,
                2.0, 5.0, 3.0,
                1.0, 0.0, 8.0);

        Matrix3x3 b = a.invert();

        Matrix3x3 expected = new Matrix3x3(
                -40.0, 16.0, 9.0,
                13.0, -5.0, -3.0,
                5.0, -2.0, -1.0);

        assertArrayEquals(expected.values[0], b.values[0], 0.0);
        assertArrayEquals(expected.values[1], b.values[1], 0.0);
        assertArrayEquals(expected.values[2], b.values[2], 0.0);

        Matrix3x3 c = b.invert();

        assertArrayEquals(a.values[0], c.values[0], 0.0);
        assertArrayEquals(a.values[1], c.values[1], 0.0);
        assertArrayEquals(a.values[2], c.values[2], 0.0);
    }

    @Test
    public void multiply() {
        Matrix3x3 a = new Matrix3x3(
                1.0, 2.0, 3.0,
                4.0, 5.0, 6.0,
                7.0, 8.0, 9.0);

        Matrix3x3 b = a.multiply(a);

        Matrix3x3 expected = new Matrix3x3(
                30.0, 36.0, 42.0,
                66.0, 81.0, 96.0,
                102.0, 126.0, 150.0);

        assertArrayEquals(expected.values[0], b.values[0], 0.0);
        assertArrayEquals(expected.values[1], b.values[1], 0.0);
        assertArrayEquals(expected.values[2], b.values[2], 0.0);
    }

    @Test
    public void symmetry() {
        Matrix3x3 matrix = new Matrix3x3(
                1.0, 2.0, 3.0,
                2.0, 5.0, 3.0,
                1.0, 0.0, 8.0);

        Matrix3x3 inverse = matrix.invert();

        Matrix3x3 mult = matrix.multiply(inverse);

        Matrix3x3 identity = new Matrix3x3(
                1.0, 0.0, 0.0,
                0.0, 1.0, 0.0,
                0.0, 0.0, 1.0);

        assertArrayEquals(identity.values[0], mult.values[0], 0.0);
        assertArrayEquals(identity.values[1], mult.values[1], 0.0);
        assertArrayEquals(identity.values[2], mult.values[2], 0.0);
    }
}
