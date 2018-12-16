package band.full.video.itu.nal;

import static band.full.video.itu.nal.RbspWriter.se2ue;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class RbspWriterTest {
    @Test
    public void testWriteU1() {
        NalBuffer buf = new NalBuffer();
        RbspWriter writer = new RbspWriter(buf);

        range(0, 2).flatMap(i -> IntStream.of(0, 1, 0, 0, 1, 1, 0, 1))
                .forEach(b -> writer.u1(b != 0));

        assertEquals(16, buf.pos);
        range(0, 2).forEach(i -> assertEquals(0b01001101, buf.bytes[i]));
    }

    @Test
    public void testWriteU3() {
        NalBuffer buf = new NalBuffer();
        RbspWriter writer = new RbspWriter(buf);

        writer.u3(0b010);
        writer.u3(0b011);
        writer.u3(0b010);
        writer.u3(0b100);
        writer.u3(0b110);
        writer.u3(0b101);
        writer.u3(0b001);
        writer.u3(0b101);

        assertEquals(24, buf.pos);
        range(0, 3).forEach(i -> assertEquals(0b01001101, buf.bytes[i]));
    }

    @Test
    public void testWriteU20() {
        NalBuffer buf = new NalBuffer();
        RbspWriter writer = new RbspWriter(buf);

        writer.u20(0b01001101010011010100);
        writer.u20(0b11010100110101001101);

        assertEquals(40, buf.pos);
        range(0, 5).forEach(i -> assertEquals(0b01001101, buf.bytes[i]));
    }

    @Test
    public void testWriteULong() {
        NalBuffer buf = new NalBuffer();
        RbspWriter writer = new RbspWriter(buf);

        writer.writeULong(44, 0b01001101010011010100110101001101010011010100L);
        writer.writeULong(36, 0b110101001101010011010100110101001101L);

        assertEquals(80, buf.pos);
        range(0, 10).forEach(i -> assertEquals(0b01001101, buf.bytes[i]));
    }

    @Test
    public void testWriteUE() {
        assertUE(1, 0b10000000, 0);
        assertUE(3, 0b01000000, 1);
        assertUE(3, 0b01100000, 2);
        assertUE(5, 0b00100000, 3);
        assertUE(5, 0b00101000, 4);
        assertUE(5, 0b00110000, 5);
        assertUE(5, 0b00111000, 6);
        assertUE(7, 0b00010000, 7);
        assertUE(7, 0b00010010, 8);
        assertUE(7, 0b00010100, 9);
        assertUE(7, 0b00010110, 10);
        assertUE(7, 0b00011000, 11);
        assertUE(7, 0b00011010, 12);
        assertUE(7, 0b00011100, 13);
        assertUE(7, 0b00011110, 14);
    }

    private void assertUE(int bits, int expected, int ue) {
        NalBuffer buf = new NalBuffer();
        RbspWriter writer = new RbspWriter(buf);

        writer.ue(ue);

        assertEquals(bits, buf.pos);
        assertEquals((byte) expected, buf.bytes[0]);
    }

    @Test
    public void testUE() {
        assertEquals(0, se2ue(0));
        assertEquals(1, se2ue(1));
        assertEquals(2, se2ue(-1));
        assertEquals(3, se2ue(2));
        assertEquals(4, se2ue(-2));
        assertEquals(5, se2ue(3));
        assertEquals(6, se2ue(-3));
    }
}
