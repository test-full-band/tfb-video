package band.full.video.itu;

import static band.full.video.itu.BT709.BT709_8bit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestBT709 {
    @Test
    public void values() {
        assertEquals(0.2126, BT709_8bit.RY, 5e-5);
        assertEquals(0.7152, BT709_8bit.GY, 5e-5);
        assertEquals(0.0722, BT709_8bit.BY, 5e-5);

        assertEquals(1.8556, BT709_8bit.BCD, 5e-5);
        assertEquals(1.5748, BT709_8bit.RCD, 1e-4);
    }
}
