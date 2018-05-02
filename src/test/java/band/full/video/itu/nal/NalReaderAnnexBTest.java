package band.full.video.itu.nal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class NalReaderAnnexBTest {
    private static final class TestReaderAnnexB
            extends NalReaderAnnexB<NalUnit> {
        private TestReaderAnnexB(byte[] nal) {
            super(nal);
        }

        @Override
        protected NalUnit create(boolean zero_byte, RbspReader nalu) {
            throw new NoSuchMethodError();
        }
    }

    @Test
    public void testRemoveEmulationPrevention() throws IOException {
        assertRbspBytes(bytes(0x00, 0x00),
                bytes(0x00, 0x00, 0x03));

        assertRbspBytes(bytes(0x01, 0x00, 0x00),
                bytes(0x01, 0x00, 0x00, 0x03));

        assertRbspBytes(bytes(0x00, 0x00, 0x00),
                bytes(0x00, 0x00, 0x03, 0x00));

        assertRbspBytes(bytes(0x00, 0x00, 0x01),
                bytes(0x00, 0x00, 0x03, 0x01));

        assertRbspBytes(bytes(0x00, 0x00, 0x03),
                bytes(0x00, 0x00, 0x03, 0x03));

        assertRbspBytes(bytes(0x00, 0x00, 0x00, 0x00),
                bytes(0x00, 0x00, 0x03, 0x00, 0x00));

        assertRbspBytes(bytes(0x00, 0x00, 0x01, 0x00, 0x00),
                bytes(0x00, 0x00, 0x03, 0x01, 0x00, 0x00));

        assertRbspBytes(bytes(0x00, 0x00, 0x00, 0x00),
                bytes(0x00, 0x00, 0x03, 0x00, 0x00, 0x03));

        assertRbspBytes(bytes(0x00, 0x00, 0x01, 0x00, 0x00),
                bytes(0x00, 0x00, 0x03, 0x01, 0x00, 0x00, 0x03));

        assertRbspBytes(bytes(0x00, 0x00, 0x01, 0x00, 0x00, 0x01),
                bytes(0x00, 0x00, 0x03, 0x01, 0x00, 0x00, 0x03, 0x01));

        assertRbspBytes(bytes(0x00, 0x00),
                bytes(0x00, 0x00, 0x03, 0x00, 0x00, 0x01));

        assertRbspBytes(bytes(0x00, 0x00),
                bytes(0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x01));
    }

    private void assertRbspBytes(byte[] expected, byte[] in)
            throws IOException {
        RbspReader reader = new TestReaderAnnexB(in).nalu();
        assertArrayEquals(expected, reader.readTrailingBits());
    }

    private byte[] bytes(int... ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ints[i];
        }
        return bytes;
    }
}
