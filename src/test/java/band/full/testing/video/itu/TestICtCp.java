package band.full.testing.video.itu;

import static band.full.testing.video.itu.BT2020.PRIMARIES;
import static band.full.testing.video.itu.ColorRange.LIMITED;
import static band.full.testing.video.smpte.ST2084.PQ;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestICtCp {
    private static final ICtCp PARAMS = new ICtCp(9, PQ, PRIMARIES, 12);

    @Test
    public void valuesFull() {
        assertEquals(0, PARAMS.YMIN);
        assertEquals(4095, PARAMS.YMAX);
        assertEquals(1, PARAMS.CMIN);
        assertEquals(4095, PARAMS.CMAX);
        assertEquals(2048, PARAMS.ACHROMATIC);
    }

    @Test
    public void valuesLimited() {
        ICtCp params = new ICtCp(9, PQ, PRIMARIES, 12, LIMITED);

        assertEquals(256, params.YMIN);
        assertEquals(3760, params.YMAX);
        assertEquals(256, params.CMIN);
        assertEquals(3840, params.CMAX);
        assertEquals(2048, params.ACHROMATIC);
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
        // TODO
    }

    @Test
    public void gray() {
        // TODO
    }

    @Test
    public void white() {
        // TODO
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
        // TODO
    }
}
