package band.full.video.itu.nal;

import static band.full.video.itu.nal.RbspReader.SE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RbspReaderTest {
    @Test
    public void testReadU1() {
        RbspReader reader = reader(0b01001101, 0b01001101);

        for (int i = 0; i < 2; i++) {
            assertEquals(false, reader.readU1());
            assertEquals(true, reader.readU1());
            assertEquals(false, reader.readU1());
            assertEquals(false, reader.readU1());
            assertEquals(true, reader.readU1());
            assertEquals(true, reader.readU1());
            assertEquals(false, reader.readU1());
            assertEquals(true, reader.readU1());
        }
    }

    @Test
    public void testReadUByte() {
        RbspReader reader = reader(0b01001101, 0b01001101, 0b01001101);

        assertEquals(0b010, reader.readUByte(3));
        assertEquals(0b011, reader.readUByte(3));
        assertEquals(0b010, reader.readUByte(3));
        assertEquals(0b100, reader.readUByte(3));
        assertEquals(0b110, reader.readUByte(3));
        assertEquals(0b101, reader.readUByte(3));
        assertEquals(0b001, reader.readUByte(3));
        assertEquals(0b101, reader.readUByte(3));
    }

    @Test
    public void testReadUInt() {
        RbspReader reader = reader(0b01001101, 0b01001101,
                0b01001101, 0b01001101, 0b01001101);

        assertEquals(0b01001101010011010100, reader.readUInt(20));
        assertEquals(0b11010100110101001101, reader.readUInt(20));
    }

    @Test
    public void testReadULong() {
        RbspReader reader = reader(0b01001101, 0b01001101,
                0b01001101, 0b01001101, 0b01001101, 0b01001101,
                0b01001101, 0b01001101, 0b01001101, 0b01001101);

        assertEquals(0b01001101010011010100110101001101010011010100L,
                reader.readULong(44));

        assertEquals(0b110101001101010011010100110101001101L,
                reader.readULong(36));
    }

    @Test
    public void testReadUE() {
        assertUE(0, bytes(0b10000000));
        assertUE(1, bytes(0b01000000));
        assertUE(2, bytes(0b01100000));
        assertUE(3, bytes(0b00100000));
        assertUE(4, bytes(0b00101000));
        assertUE(5, bytes(0b00110000));
        assertUE(6, bytes(0b00111000));
        assertUE(7, bytes(0b00010000));
        assertUE(8, bytes(0b00010010));
        assertUE(9, bytes(0b00010100));
        assertUE(10, bytes(0b00010110));
        assertUE(11, bytes(0b00011000));
        assertUE(12, bytes(0b00011010));
        assertUE(13, bytes(0b00011100));
        assertUE(14, bytes(0b00011110));
    }

    private void assertUE(int expected, byte[] in) {
        assertEquals(expected, reader(in).readUE());
    }

    @Test
    public void testSE() {
        assertEquals(0, SE(0));
        assertEquals(1, SE(1));
        assertEquals(-1, SE(2));
        assertEquals(2, SE(3));
        assertEquals(-2, SE(4));
        assertEquals(3, SE(5));
        assertEquals(-3, SE(6));
    }

    // @Test
    // public void testTrailingBitsAligned() {
    // assertTrailingBits(bytes(), bytes());
    //
    // assertTrailingBits(bytes(0x80), bytes(0x80));
    // assertTrailingBits(bytes(0x01), bytes(0x01));
    //
    // assertTrailingBits(bytes(0x55, 0x80), bytes(0x55, 0x80));
    // assertTrailingBits(bytes(0x55, 0x01), bytes(0x55, 0x01));
    //
    // assertTrailingBits(bytes(0x80), bytes(0x55, 0x80), 8);
    // assertTrailingBits(bytes(0x01), bytes(0x55, 0x01), 8);
    // }
    //
    // @Test
    // public void testTrailingBitsUnaligned1() {
    // assertTrailingBits(bytes(), bytes(), 1, 0x02);
    //
    // assertTrailingBits(bytes(0x00), bytes(0x00), 1, 0x02);
    // assertTrailingBits(bytes(0x40), bytes(0x80), 1, 0x02);
    // assertTrailingBits(bytes(0x00, 0x80), bytes(0x01), 1, 0x02);
    //
    // assertTrailingBits(bytes(0x00, 0x00),
    // bytes(0x00, 0x00), 1, 0x02);
    //
    // assertTrailingBits(bytes(0x00, 0x40),
    // bytes(0x00, 0x80), 1, 0x02);
    //
    // assertTrailingBits(bytes(0x00, 0x00, 0x80),
    // bytes(0x00, 0x01), 1, 0x02);
    // }
    //
    // @Test
    // public void testTrailingBitsUnaligned7() {
    // assertTrailingBits(bytes(), bytes(), 7, 0x80);
    //
    // assertTrailingBits(bytes(0x00), bytes(0x00), 7, 0x80);
    // assertTrailingBits(bytes(0x01), bytes(0x80), 7, 0x80);
    // assertTrailingBits(bytes(0x00, 0x02), bytes(0x01), 7, 0x80);
    //
    // assertTrailingBits(bytes(0x00, 0x00),
    // bytes(0x00, 0x00), 7, 0x80);
    //
    // assertTrailingBits(bytes(0x00, 0x01),
    // bytes(0x00, 0x80), 7, 0x80);
    //
    // assertTrailingBits(bytes(0x00, 0x00, 0x02),
    // bytes(0x00, 0x01), 7, 0x80);
    // }
    //
    // private void assertTrailingBits(byte[] expected, byte[] in) {
    // RbspReader reader = reader(in);
    // assertArrayEquals(expected, reader.readTrailingBits());
    // }
    //
    // private void assertTrailingBits(byte[] expected, byte[] in, int bits) {
    // RbspReader reader = reader(in);
    // reader.readUInt(bits);
    // assertArrayEquals(expected, reader.readTrailingBits());
    // }

    private byte[] bytes(int... ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ints[i];
        }
        return bytes;
    }

    private RbspReader reader(int... ints) {
        return reader(bytes(ints));
    }

    private RbspReader reader(byte[] in) {
        return new RbspReader(in, 0, in.length);
    }
}
