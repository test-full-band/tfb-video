package band.full.testing.video.itu;

import static band.full.testing.video.itu.BT2020.PRIMARIES;
import static band.full.testing.video.itu.ColorRange.NARROW;
import static band.full.testing.video.itu.ICtCp.ITPtoPQLMS;
import static band.full.testing.video.smpte.ST2084.PQ;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestICtCp {
    private static final ICtCp MATRIX = new ICtCp(PQ, PRIMARIES, 12);

    @Test
    public void valuesITPtoPQLMS() {
        assertEquals(1.0, ITPtoPQLMS.get(0, 0));
        assertEquals(0.009, ITPtoPQLMS.get(0, 1), 1e-3);
        assertEquals(0.111, ITPtoPQLMS.get(0, 2), 1e-3);
        assertEquals(1.0, ITPtoPQLMS.get(1, 0));
        assertEquals(-0.009, ITPtoPQLMS.get(1, 1), 1e-3);
        assertEquals(-0.111, ITPtoPQLMS.get(1, 2), 1e-3);
        assertEquals(1.0, ITPtoPQLMS.get(2, 0));
        assertEquals(0.56, ITPtoPQLMS.get(2, 1), 1e-3);
        assertEquals(-0.321, ITPtoPQLMS.get(2, 2), 1e-3);
    }

    @Test
    public void valuesFull() {
        assertEquals(0, MATRIX.YMIN);
        assertEquals(4095, MATRIX.YMAX);
        assertEquals(1, MATRIX.CMIN);
        assertEquals(4095, MATRIX.CMAX);
        assertEquals(2048, MATRIX.ACHROMATIC);
    }

    @Test
    public void valuesLimited() {
        ICtCp params = new ICtCp(PQ, PRIMARIES, 12, NARROW);

        assertEquals(256, params.YMIN);
        assertEquals(3760, params.YMAX);
        assertEquals(256, params.CMIN);
        assertEquals(3840, params.CMAX);
        assertEquals(2048, params.ACHROMATIC);
    }

    @Test
    public void fromLumaCode() {
        assertEquals(0.0, MATRIX.fromLumaCode(MATRIX.YMIN));
        assertEquals(1.0, MATRIX.fromLumaCode(MATRIX.YMAX));
    }

    @Test
    public void fromChromaCode() {
        assertEquals(-0.5, MATRIX.fromChromaCode(MATRIX.CMIN));
        assertEquals(0.5, MATRIX.fromChromaCode(MATRIX.CMAX));
        assertEquals(0.0, MATRIX.fromChromaCode(MATRIX.ACHROMATIC));
    }

    @Test
    public void toLumaCode() {
        assertEquals(MATRIX.YMIN, MATRIX.toLumaCode(0.0));
        assertEquals(MATRIX.YMAX, MATRIX.toLumaCode(1.0));
    }

    @Test
    public void toChromaCode() {
        assertEquals(MATRIX.CMIN, MATRIX.toChromaCode(-0.5));
        assertEquals(MATRIX.CMAX, MATRIX.toChromaCode(0.5));
        assertEquals(MATRIX.ACHROMATIC, MATRIX.toChromaCode(0.0));
    }

    @Test
    public void black() {
        double[] expected = {0.0, 0.0, 0.0};
        double[] black = {0.0, 0.0, 0.0};
        assertArrayEquals(expected, MATRIX.toRGB(black, black));
        assertArrayEquals(expected, MATRIX.toLinearRGB(black, black));
        assertArrayEquals(expected, MATRIX.fromRGB(black, black));
        assertArrayEquals(expected, MATRIX.fromLinearRGB(black, black));
    }

    @Test
    public void gray() {
        double[] tmp = new double[3];
        double[] rgb = {0.5, 0.5, 0.5};
        double[] itp = {0.5, 0.0, 0.0};

        assertColorEquals(0.5, 0.5, 0.5, MATRIX.toRGB(itp, tmp));
        assertColorEquals(0.5, 0.0, 0.0, MATRIX.fromRGB(rgb, tmp));
    }

    @Test
    public void white() {
        double[] tmp = new double[3];
        double[] rgb = {1.0, 1.0, 1.0};
        double[] itp = {1.0, 0.0, 0.0};

        assertColorEquals(1.0, 1.0, 1.0, MATRIX.toRGB(itp, tmp));
        assertColorEquals(1.0, 1.0, 1.0, MATRIX.toLinearRGB(itp, tmp));
        assertColorEquals(1.0, 0.0, 0.0, MATRIX.fromRGB(rgb, tmp));
        assertColorEquals(1.0, 0.0, 0.0, MATRIX.fromLinearRGB(rgb, tmp));
    }

    private void assertColorEquals(double c0, double c1, double c2,
            double[] actual) {
        assertEquals(c0, actual[0], 1e-15);
        assertEquals(c1, actual[1], 1e-15);
        assertEquals(c2, actual[2], 1e-15);
    }

    @Test
    public void red() {
        symmetry(0.5, 0.0, 0.0);
        symmetry(1.0, 0.0, 0.0);
    }

    @Test
    public void green() {
        symmetry(0.0, 0.5, 0.0);
        symmetry(0.0, 1.0, 0.0);
    }

    @Test
    public void blue() {
        symmetry(0.0, 0.0, 0.5);
        symmetry(0.0, 0.0, 1.0);
    }

    private void symmetry(double r, double g, double b) {
        symmetryE(r, g, b);
        symmetryO(r, g, b);
    }

    private void symmetryE(double r, double g, double b) {
        double[] rgb = {r, g, b};

        MATRIX.fromRGB(rgb, rgb);
        MATRIX.toRGB(rgb, rgb);

        assertEquals(r, rgb[0], 5e-6);
        assertEquals(g, rgb[1], 5e-6);
        assertEquals(b, rgb[2], 5e-6);
    }

    private void symmetryO(double r, double g, double b) {
        double[] rgb = {r, g, b};

        MATRIX.fromLinearRGB(rgb, rgb);
        MATRIX.toLinearRGB(rgb, rgb);

        assertEquals(r, rgb[0], 5e-13);
        assertEquals(g, rgb[1], 5e-13);
        assertEquals(b, rgb[2], 5e-13);
    }
}
