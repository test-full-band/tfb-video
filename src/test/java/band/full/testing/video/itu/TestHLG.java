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

    /** Key known points and out-of-range clamping */
    @Test
    public void checkFromLinear() {
        assertEquals(0.0, HLG.fromLinear(-MAX_VALUE));
        assertEquals(0.0, HLG.fromLinear(-1.0));
        assertEquals(0.0, HLG.fromLinear(0.0));
        assertEquals(1.0, HLG.fromLinear(1.0), 5e-9);
        assertEquals(POSITIVE_INFINITY, HLG.fromLinear(MAX_VALUE));
    }

    /** Key known points and out-of-range clamping */
    @Test
    public void checkToLinear() {
        assertEquals(0.0, HLG.toLinear(-MAX_VALUE));
        assertEquals(0.0, HLG.toLinear(-1.0));
        assertEquals(0.0, HLG.toLinear(0.0));
        assertEquals(1.0, HLG.toLinear(1.0), 5e-8);
        assertEquals(262.0, HLG.toLinear(2.0), 0.5);
        assertEquals(POSITIVE_INFINITY, HLG.toLinear(MAX_VALUE));
    }

    /** Symmetry in working range */
    @Test
    public void symmetry() {
        assertEquals(0.0, HLG.toLinear(HLG.fromLinear(0.0)));
        assertEquals(0.01, HLG.toLinear(HLG.fromLinear(0.01)), 1e-14);
        assertEquals(0.1, HLG.toLinear(HLG.fromLinear(0.1)), 1e-14);
        assertEquals(0.5, HLG.toLinear(HLG.fromLinear(0.5)), 1e-14);
        assertEquals(0.9, HLG.toLinear(HLG.fromLinear(0.9)), 1e-14);
        assertEquals(1.0, HLG.toLinear(HLG.fromLinear(1.0)), 1e-14);
    }
}
