package band.full.video.itu.nal;

import static band.full.core.ArrayMath.fromHexString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class NalReaderAnnexBTest {
    private static final class TestReaderAnnexB
            extends NalReaderAnnexB<NalContext, NalUnit<NalContext>> {
        private TestReaderAnnexB(byte[] in) throws IOException {
            super(new ByteArrayInputStream(in));
        }

        @Override
        protected NalContext context() {
            return null;
        }

        @Override
        protected NalUnit<NalContext> create(NalContext context,
                RbspReader nalu, boolean zero_byte) {
            throw new NoSuchMethodError();
        }
    }

    @Test
    public void testRemoveEmulationPrevention() throws IOException {
        assertRbspBytes("0000", "00000001_000003");
        assertRbspBytes("010000", "00000001_01_000003");
        assertRbspBytes("000000", "00000001_000003_00");
        assertRbspBytes("000001", "00000001_000003_01");
        assertRbspBytes("000003", "00000001_000003_03");
        assertRbspBytes("0000_0000", "00000001_000003_0000");
        assertRbspBytes("000001_0000", "00000001_000003_010000");
        assertRbspBytes("0000_0000", "00000001_000003_000003");
        assertRbspBytes("000001_0000", "00000001_000003_01_000003");
        assertRbspBytes("000001_000001", "00000001_000003_01_000003_01");
        assertRbspBytes("0000", "00000001_000003_000001");
        assertRbspBytes("0000", "00000001_000003_00000001");
        // assertRbspBytes("000064_000080",
        // "00000001_000064_00000380_00000001_00");
    }

    private void assertRbspBytes(String expected, String in)
            throws IOException {
        try (TestReaderAnnexB test = new TestReaderAnnexB(fromHexString(in))) {
            RbspReader reader = test.nalu();
            assertArrayEquals(fromHexString(expected),
                    reader.readTrailingBits());
        }
    }
}
