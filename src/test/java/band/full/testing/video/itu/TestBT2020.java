package band.full.testing.video.itu;

import static band.full.testing.video.itu.BT2020.BT2020_10bit;
import static band.full.testing.video.itu.BT2020.BT2020_12bit;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestBT2020 {
    @Test
    public void values() {
        assertEquals(0.2627, BT2020_10bit.RY, 5e-5);
        assertEquals(0.6780, BT2020_10bit.GY, 5e-5);
        assertEquals(0.0593, BT2020_10bit.BY, 5e-5);

        assertEquals(1.8814, BT2020_10bit.BCD, 5e-5);
        assertEquals(1.4746, BT2020_10bit.RCD, 5e-5);
    }

    @Test
    public void bitdepth10() {
        assertEquals(64, BT2020_10bit.YMIN);
        assertEquals(940, BT2020_10bit.YMAX);
        assertEquals(64, BT2020_10bit.CMIN);
        assertEquals(960, BT2020_10bit.CMAX);
        assertEquals(512, BT2020_10bit.ACHROMATIC);
    }

    @Test
    public void bitdepth12() {
        assertEquals(256, BT2020_12bit.YMIN);
        assertEquals(3760, BT2020_12bit.YMAX);
        assertEquals(256, BT2020_12bit.CMIN);
        assertEquals(3840, BT2020_12bit.CMAX);
        assertEquals(2048, BT2020_12bit.ACHROMATIC);
    }
}
