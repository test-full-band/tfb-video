package band.full.testing.video.itu;

import static band.full.testing.video.itu.BT1886.TRUE_BLACK_TRANSFER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestBT1886 {
    @Test
    public void transfer() {
        BT1886 t = TRUE_BLACK_TRANSFER;

        assertEquals(0.0, t.eotf(0.0));
        assertEquals(1.0, t.eotf(1.0));

        assertEquals(0.0, t.oetf(0.0));
        assertEquals(1.0, t.oetf(1.0));

        assertEquals(0.5, t.oetf(t.eotf(0.5)));
    }
}
