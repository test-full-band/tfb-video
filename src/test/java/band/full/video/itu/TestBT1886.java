package band.full.video.itu;

import static band.full.video.itu.BT1886.TRUE_BLACK_TRANSFER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import band.full.video.itu.BT1886;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestBT1886 {
    @Test
    public void transferTrueBlack() {
        BT1886 t = TRUE_BLACK_TRANSFER;

        assertEquals(0.0, t.eotf(0.0));
        assertEquals(1.0, t.eotf(1.0));

        assertEquals(0.0, t.eotfi(0.0));
        assertEquals(1.0, t.eotfi(1.0));

        assertEquals(0.5, t.eotfi(t.eotf(0.5)));
    }

    @Test
    @Disabled("Fix it!") // TODO
    public void transfer05() {
        BT1886 t = BT1886.transfer(0.5, 100.0);

        assertEquals(0.005, t.eotf(0.0));
        assertEquals(1.0, t.eotf(1.0));

        assertEquals(0.0, t.eotfi(0.005));
        assertEquals(1.0, t.eotfi(1.0));

        assertEquals(0.5, t.eotfi(t.eotf(0.5)));
    }
}
