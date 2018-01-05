package band.full.testing.video.itu;

import static band.full.testing.video.color.Primaries.sRGB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestYCbCr {
    private static final YCbCr PARAMS = new YCbCr(1, sRGB, 8);

    @Test
    public void values() {
        assertEquals(16, PARAMS.YMIN);
        assertEquals(235, PARAMS.YMAX);
        assertEquals(16, PARAMS.CMIN);
        assertEquals(240, PARAMS.CMAX);
        assertEquals(128, PARAMS.ACHROMATIC);
    }

    @Test
    public void fromLumaCode() {
        assertEquals(0.0, PARAMS.fromLumaCode(PARAMS.YMIN));
        assertEquals(1.0, PARAMS.fromLumaCode(PARAMS.YMAX));
    }

    @Test
    public void fromChromaCode() {
        assertEquals(-0.5, PARAMS.fromChromaCode(PARAMS.CMIN));
        assertEquals(0.5, PARAMS.fromChromaCode(PARAMS.CMAX));
        assertEquals(0.0, PARAMS.fromChromaCode(PARAMS.ACHROMATIC));
    }

    @Test
    public void toLumaCode() {
        assertEquals(PARAMS.YMIN, PARAMS.toLumaCode(0.0));
        assertEquals(PARAMS.YMAX, PARAMS.toLumaCode(1.0));
    }

    @Test
    public void toChromaCode() {
        assertEquals(PARAMS.CMIN, PARAMS.toChromaCode(-0.5));
        assertEquals(PARAMS.CMAX, PARAMS.toChromaCode(0.5));
        assertEquals(PARAMS.ACHROMATIC, PARAMS.toChromaCode(0.0));
    }

    @Test
    public void black() {
        assertEquals(0.0, PARAMS.getY(0.0, 0.0, 0.0));
        assertEquals(0.0, PARAMS.getG(0.0, 0.0, 0.0));
        assertEquals(0.0, PARAMS.getB(0.0, 0.0));
        assertEquals(0.0, PARAMS.getR(0.0, 0.0));
    }

    @Test
    public void gray() {
        assertEquals(0.5, PARAMS.getY(0.5, 0.5, 0.5), 1e-15);
        assertEquals(0.0, PARAMS.getCb(0.5, 0.5));
        assertEquals(0.0, PARAMS.getCr(0.5, 0.5));
    }

    @Test
    public void white() {
        assertEquals(1.0, PARAMS.getY(1.0, 1.0, 1.0), 1e-15);
        assertEquals(0.0, PARAMS.getCb(1.0, 1.0));
        assertEquals(0.0, PARAMS.getCr(1.0, 1.0));
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
        double y = PARAMS.getY(r, g, b);
        assertEquals(g, PARAMS.getG(y, b, r), 1e-14);
        assertEquals(b, PARAMS.getB(y, PARAMS.getCb(y, b)), 1e-14);
        assertEquals(r, PARAMS.getR(y, PARAMS.getCr(y, r)), 1e-14);
    }
}
