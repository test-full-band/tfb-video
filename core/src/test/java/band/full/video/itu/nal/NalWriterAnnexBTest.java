package band.full.video.itu.nal;

import static band.full.core.ArrayMath.fromHexString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NalWriterAnnexBTest {
    private static final class TestWriterAnnexB
            extends NalWriterAnnexB<NalContext, TestNALU> {
        private TestWriterAnnexB(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeHeader(RbspWriter out, TestNALU nalu) {
            out.i8(nalu.type);
        }
    }

    private static final class TestNALU extends NalUnit<NalContext> {
        public byte type;
        public byte[] bytes;

        public TestNALU(boolean zero_byte, byte type, byte[] bytes) {
            this.zero_byte = zero_byte;
            this.type = type;
            this.bytes = bytes;
        }

        @Override
        public boolean isZeroByteRequired() {
            return false;
        }

        @Override
        public String getTypeString() {
            return null;
        }

        @Override
        public String getHeaderParamsString() {
            return null;
        }

        @Override
        public void read(NalContext context, RbspReader in) {}

        @Override
        public void write(NalContext context, RbspWriter out) {
            out.writeTrailingBits(bytes);
        }

        @Override
        public void print(NalContext context, RbspPrinter out) {}
    }

    @Test
    public void testInsertEmulationPrevention() throws IOException {
        assertRbspBytes("00000001_5C", "");
        assertRbspBytes("00000001_5C_80", "80");
        assertRbspBytes("00000001_5C_01", "01");
        assertRbspBytes("00000001_5C_04", "04");
        assertRbspBytes("00000001_5C_0080", "0080");
        assertRbspBytes("00000001_5C_0001", "0001");
        assertRbspBytes("00000001_5C_A080", "A080");

        assertRbspBytes("00000001_5C_000003", "0000");
        assertRbspBytes("00000001_5C_80_000003", "80_0000");
        assertRbspBytes("00000001_5C_01_000003", "01_0000");
        assertRbspBytes("00000001_5C_000003_00", "0000_00");
        assertRbspBytes("00000001_5C_000003_01", "0000_01");
        assertRbspBytes("00000001_5C_000003_02", "0000_02");
        assertRbspBytes("00000001_5C_000003_03", "0000_03");
        assertRbspBytes("00000001_5C_000004", "000004");
        assertRbspBytes("00000001_5C_80_000003_00", "80_0000_00");
        assertRbspBytes("00000001_5C_80_000003_01", "80_0000_01");
        assertRbspBytes("00000001_5C_80_000003_02", "80_0000_02");
        assertRbspBytes("00000001_5C_80_000003_03", "80_0000_03");
        assertRbspBytes("00000001_5C_80_000004", "80_000004");

        assertRbspBytes("00000001_5C_000003_000003",
                "0000_0000");

        assertRbspBytes("00000001_5C_000003_01_000003",
                "0000_01_0000");

        assertRbspBytes("00000001_5C_000004_000003",
                "000004_0000");

        assertRbspBytes("00000001_5C_80_000003_000003",
                "80_0000_0000");

        assertRbspBytes("00000001_5C_000003_01_000003",
                "0000_01_0000");

        assertRbspBytes("00000001_5C_80_000003_000003_02",
                "80_0000_0000_02");

        assertRbspBytes("00000001_5C_80_000003_000003_02_000003",
                "80_0000_0000_02_0000");

        assertRbspBytes("00000001_5C_000003_00000001_5C_000003",
                "0000", "0000");

        assertRbspBytes("00000001_5C_80000003_00000001_5C_000080",
                "800000", "000080");

        assertRbspBytes("00000001_5C_000080_00000001_5C_80000003",
                "000080", "800000");
    }

    private static final byte NALU_TYPE = 0x5C;

    private static void assertRbspBytes(String expected, String... rbsps)
            throws IOException {
        var out = new ByteArrayOutputStream();
        try (var writer = new TestWriterAnnexB(out)) {
            for (var hex : rbsps) {
                var rbsp = fromHexString(hex);
                writer.write(null, new TestNALU(true, NALU_TYPE, rbsp));
            }
        }
        assertArrayEquals(fromHexString(expected), out.toByteArray());
    }
}
