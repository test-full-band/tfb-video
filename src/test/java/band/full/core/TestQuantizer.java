package band.full.core;

import static band.full.core.Quantizer.round;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestQuantizer {
    @Test
    public void zero() {
        assertEquals(0, round(-0.1));
        assertEquals(0, round(0.0));
        assertEquals(0, round(0.1));
    }

    @Test
    public void negative() {
        assertEquals(-1, round(-1.1));
        assertEquals(-1, round(-1.0));
        assertEquals(-1, round(-0.9));
    }

    @Test
    public void positive() {
        assertEquals(1, round(0.9));
        assertEquals(1, round(1.0));
        assertEquals(1, round(1.1));
    }

    @Test
    public void limits() {
        assertEquals(Integer.MIN_VALUE, round(Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, round(Integer.MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, round(Long.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, round(Long.MAX_VALUE));

        assertEquals(Integer.MIN_VALUE, round(Double.NEGATIVE_INFINITY));
        assertEquals(Integer.MAX_VALUE, round(Double.POSITIVE_INFINITY));
    }
}
