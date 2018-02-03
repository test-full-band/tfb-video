package band.full.testing.video.itu;

import static band.full.testing.video.itu.BT2100.HLG;
import static java.lang.Double.MAX_VALUE;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestHLG {
    @Test
    public void values() {
        assertEquals(0.17883277, HybridLogGamma.A);
        assertEquals(0.28466892, HybridLogGamma.B);
        assertEquals(0.55991073, HybridLogGamma.C, 5e-10);
    }

    /**
     * Opto-Electrical Transfer Function<br>
     * key known points and out-of-range clamping
     */
    @Test
    public void checkOETF() {
        assertEquals(0.0, HLG.oetf(-MAX_VALUE));
        assertEquals(0.0, HLG.oetf(-1.0));
        assertEquals(0.0, HLG.oetf(0.0));
        assertEquals(1.0, HLG.oetf(1.0), 5e-9);
        assertEquals(POSITIVE_INFINITY, HLG.oetf(MAX_VALUE));
    }

    /**
     * Electro-Optical Transfer Function<br>
     * key known points and out-of-range clamping
     */
    @Test
    public void checkEOTF() {
        assertEquals(0.0, HLG.eotf(-MAX_VALUE));
        assertEquals(0.0, HLG.eotf(-1.0));
        assertEquals(0.0, HLG.eotf(0.0));
        assertEquals(1.0, HLG.eotf(1.0), 5e-8);
        assertEquals(262.0, HLG.eotf(2.0), 0.5);
        assertEquals(POSITIVE_INFINITY, HLG.eotf(MAX_VALUE));
    }

    /** Symmetry in working range */
    @Test
    public void symmetry() {
        assertEquals(0.0, HLG.eotf(HLG.oetf(0.0)));
        assertEquals(0.01, HLG.eotf(HLG.oetf(0.01)), 1e-14);
        assertEquals(0.1, HLG.eotf(HLG.oetf(0.1)), 1e-14);
        assertEquals(0.5, HLG.eotf(HLG.oetf(0.5)), 1e-14);
        assertEquals(0.9, HLG.eotf(HLG.oetf(0.9)), 1e-14);
        assertEquals(1.0, HLG.eotf(HLG.oetf(1.0)), 1e-14);
    }
}
