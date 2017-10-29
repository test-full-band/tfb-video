package band.full.testing.video.itu;

import static band.full.testing.video.itu.BT709.BT709;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestBT709 {
    @Test
    public void values() {
        assertEquals(0.2126, BT709.RY, 5e-5);
        assertEquals(0.7152, BT709.GY, 5e-5);
        assertEquals(0.0722, BT709.BY, 5e-5);

        assertEquals(1.8556, BT709.BCD, 5e-5);
        assertEquals(1.5748, BT709.RCD, 1e-4);
    }
}
