package band.full.core;

import static band.full.core.ArrayMath.fromHexChar;
import static band.full.core.ArrayMath.fromHexString;
import static band.full.core.ArrayMath.toHexString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestArrayMath {
    @Test
    public void testToHexString() {
        assertEquals("", toHexString(bytes()));
        assertEquals("C5", toHexString(bytes(0xC5)));

        assertEquals("FEDCBA9876543210", toHexString(bytes(
                0xFE, 0xDC, 0xBA, 0x98, 0x76, 0x54, 0x32, 0x10)));
    }

    @Test
    public void testFromHexChar() {
        assertEquals(0, fromHexChar('0'));
        assertEquals(5, fromHexChar('5'));
        assertEquals(9, fromHexChar('9'));
        assertEquals(0xA, fromHexChar('A'));
        assertEquals(0xD, fromHexChar('D'));
        assertEquals(0xF, fromHexChar('F'));
        assertEquals(-1, fromHexChar('_'));
    }

    @Test
    public void testFromHexString() {
        assertArrayEquals(bytes(0xC5, 0xC5), fromHexString("C5C5"));
        assertArrayEquals(bytes(0xC5, 0xC5), fromHexString("_C5_C5_"));

        assertThrows(IllegalArgumentException.class,
                () -> fromHexString("C_"));

        assertThrows(IllegalArgumentException.class,
                () -> fromHexString("C_5"));

        assertThrows(IllegalArgumentException.class,
                () -> fromHexString("_C_"));
    }

    public static byte[] bytes(int... ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ints[i];
        }
        return bytes;
    }
}
