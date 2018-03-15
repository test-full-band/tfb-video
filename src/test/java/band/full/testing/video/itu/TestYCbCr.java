package band.full.testing.video.itu;

import static band.full.testing.video.color.Primaries.sRGB;
import static band.full.testing.video.itu.BT709.TRANSFER;
import static band.full.testing.video.itu.ColorRange.FULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestYCbCr {
    private static final YCbCr MATRIX = new YCbCr(1, TRANSFER, sRGB, 8);

    @Test
    public void valuesLimited() {
        assertEquals(1, MATRIX.VMIN);
        assertEquals(254, MATRIX.VMAX);
        assertEquals(16, MATRIX.YMIN);
        assertEquals(235, MATRIX.YMAX);
        assertEquals(16, MATRIX.CMIN);
        assertEquals(240, MATRIX.CMAX);
        assertEquals(128, MATRIX.ACHROMATIC);
    }

    @Test
    public void valuesFull() {
        YCbCr matrix = new YCbCr(1, TRANSFER, sRGB, 8, FULL);

        assertEquals(0, matrix.VMIN);
        assertEquals(255, matrix.VMAX);
        assertEquals(0, matrix.YMIN);
        assertEquals(255, matrix.YMAX);
        assertEquals(1, matrix.CMIN);
        assertEquals(255, matrix.CMAX);
        assertEquals(128, matrix.ACHROMATIC);
    }

    @Test
    public void isValidCode() {
        assertFalse(MATRIX.isNominal(15, 128, 128));
        assertTrue(MATRIX.isNominal(16, 128, 128));
        assertTrue(MATRIX.isNominal(17, 128, 128));
        assertFalse(MATRIX.isNominal(16, 127, 128));
        assertFalse(MATRIX.isNominal(16, 128, 127));
        assertFalse(MATRIX.isNominal(16, 129, 128));
        assertFalse(MATRIX.isNominal(16, 128, 129));
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
        assertEquals(0.0, MATRIX.getY(0.0, 0.0, 0.0));
        assertEquals(0.0, MATRIX.getG(0.0, 0.0, 0.0));
        assertEquals(0.0, MATRIX.getB(0.0, 0.0));
        assertEquals(0.0, MATRIX.getR(0.0, 0.0));
    }

    @Test
    public void gray() {
        assertEquals(0.5, MATRIX.getY(0.5, 0.5, 0.5), 1e-15);
        assertEquals(0.0, MATRIX.getCb(0.5, 0.5));
        assertEquals(0.0, MATRIX.getCr(0.5, 0.5));
    }

    @Test
    public void white() {
        assertEquals(1.0, MATRIX.getY(1.0, 1.0, 1.0), 1e-15);
        assertEquals(0.0, MATRIX.getCb(1.0, 1.0));
        assertEquals(0.0, MATRIX.getCr(1.0, 1.0));
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
        double y = MATRIX.getY(r, g, b);

        assertEquals(g, MATRIX.getG(y, b, r), 1e-14);
        assertEquals(b, MATRIX.getB(y, MATRIX.getCb(y, b)), 1e-14);
        assertEquals(r, MATRIX.getR(y, MATRIX.getCr(y, r)), 1e-14);
    }
}
