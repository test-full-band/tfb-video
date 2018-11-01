package band.full.video.smpte;

import static band.full.video.smpte.ST2084.L_MAX;
import static band.full.video.smpte.ST2084.PQ;
import static java.lang.Double.MAX_VALUE;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestST2084 {
    /** Key known points and out-of-range clamping */
    @Test
    public void checkFromLinear() {
        assertEquals(0.0, PQ.fromLinear(-MAX_VALUE));
        assertEquals(0.0, PQ.fromLinear(-1.0));
        assertEquals(0.0, PQ.fromLinear(0.0), 1e-6);
        assertEquals(1.0, PQ.fromLinear(1.0));
        assertEquals(1.99, PQ.fromLinear(MAX_VALUE), 1e-2);
    }

    /** Key known points and out-of-range clamping */
    @Test
    public void checkToLinear() {
        assertEquals(0.0, PQ.toLinear(-MAX_VALUE));
        assertEquals(0.0, PQ.toLinear(-1.0));
        assertEquals(0.0, PQ.toLinear(0.0));
        assertEquals(92.24570899406527 / L_MAX, PQ.toLinear(0.5), 1e-14);
        assertEquals(1.0, PQ.toLinear(1.0));
        assertEquals(7e17, PQ.toLinear(1.99), 2e16);
        assertEquals(POSITIVE_INFINITY, PQ.toLinear(2.0));
        assertEquals(POSITIVE_INFINITY, PQ.toLinear(MAX_VALUE));
    }

    /** Symmetry in working range */
    @Test
    public void symmetry() {
        assertEquals(0.0, PQ.toLinear(PQ.fromLinear(0.0)));
        assertEquals(0.1, PQ.toLinear(PQ.fromLinear(0.1)), 1e-14);
        assertEquals(0.5, PQ.toLinear(PQ.fromLinear(0.5)), 1e-14);
        assertEquals(0.9, PQ.toLinear(PQ.fromLinear(0.9)), 1e-13);
        assertEquals(1.0, PQ.toLinear(PQ.fromLinear(1.0)));
    }
}
