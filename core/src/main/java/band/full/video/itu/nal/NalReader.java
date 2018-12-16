package band.full.video.itu.nal;

import static java.lang.String.format;
import static java.lang.System.arraycopy;

import java.io.IOException;
import java.io.InputStream;

public abstract class NalReader<C extends NalContext, U extends NalUnit<C>>
        implements AutoCloseable {
    static final String INVALID_NALU = "Invalid byte sequence in nal_unit: ";

    protected final InputStream in;

    public NalReader(InputStream in) {
        this.in = in;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    public abstract U read() throws IOException;

    protected abstract C context();

    protected abstract U create(C context, RbspReader in);

    static int removeEmulationPreventionBytes(byte[] buf, int offset, int end)
            throws IOException {
        int n = findEmulationPreventionByte(buf, offset, end);
        if (n == 0) return end;

        int pos = n++;
        while (true) {
            int n2 = findEmulationPreventionByte(buf, n, end);
            if (n2 == 0) {
                int len = end - n;
                arraycopy(buf, n, buf, pos, len);
                return pos + len;
            }

            int len = n2 - n;
            arraycopy(buf, n, buf, pos, len);
            n = n2 + 1;
            pos += len;
            if (n == end) return pos;
        }
    }

    /** @return 0 if not found, otherwise position in buf of the 0x03 byte */
    private static int findEmulationPreventionByte(byte[] buf, int start,
            int end) throws IOException {
        for (int i = start, j = end - 2; i < j;) {
            if (buf[i++] != 0x00) {
                continue;
            }

            // 0x00

            if (buf[i++] != 0x00) {
                continue;
            }

            // 0x00_00

            if (buf[i] == 0x03) {
                if (i < j && (buf[i + 1] & 0b11111100) != 0)
                    // 0x00_00_03_xx where xx > 0x03
                    throw new IOException(format(
                            INVALID_NALU + "0x000003%02X",
                            buf[i + 1]));

                // 0x00_00_03_xx where xx <= 0x03
                // emulation prevention byte
                return i;
            }

            if (i < j && (buf[i] & 0b11111100) == 0)
                // 0x00_00_xx where xx < 0x03
                throw new IOException(format(
                        INVALID_NALU + "0x0000%02X", buf[i]));
        }

        return 0;
    }
}
