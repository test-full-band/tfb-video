package band.full.testing.video.smpte;

import static band.full.testing.video.smpte.ST2084.L_MAX;
import static band.full.testing.video.smpte.ST2084.PQ;
import static java.lang.Double.MAX_VALUE;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Igor Malinin
 */
public class TestST2084 {
    /**
     * Opto-Electrical Transfer Function<br>
     * key known points and out-of-range clamping
     */
    @Test
    public void checkOETF() {
        assertEquals(0.0, PQ.oetf(-MAX_VALUE), 0.0);
        assertEquals(0.0, PQ.oetf(-1.0), 0.0);
        assertEquals(0.0, PQ.oetf(0.0), 1e-6);
        assertEquals(1.0, PQ.oetf(1.0), 0.0);
        assertEquals(1.99, PQ.oetf(MAX_VALUE), 1e-2);
    }

    /**
     * Electro-Optical Transfer Function<br>
     * key known points and out-of-range clamping
     */
    @Test
    public void checkEOTF() {
        assertEquals(0.0, PQ.eotf(-MAX_VALUE), 0.0);
        assertEquals(0.0, PQ.eotf(-1.0), 0.0);
        assertEquals(0.0, PQ.eotf(0.0), 0.0);
        assertEquals(92.24570899406527 / L_MAX, PQ.eotf(0.5), 1e-14);
        assertEquals(1.0, PQ.eotf(1.0), 0.0);
        assertEquals(7e17, PQ.eotf(1.99), 2e16);
        assertEquals(POSITIVE_INFINITY, PQ.eotf(2.0), 0.0);
        assertEquals(POSITIVE_INFINITY, PQ.eotf(MAX_VALUE), 0.0);
    }

    /** Symmetry in working range */
    @Test
    public void symmetry() {
        assertEquals(0.0, PQ.eotf(PQ.oetf(0.0)), 0.0);
        assertEquals(0.1, PQ.eotf(PQ.oetf(0.1)), 1e-14);
        assertEquals(0.5, PQ.eotf(PQ.oetf(0.5)), 1e-14);
        assertEquals(0.9, PQ.eotf(PQ.oetf(0.9)), 1e-13);
        assertEquals(1.0, PQ.eotf(PQ.oetf(1.0)), 0.0);
    }
}
