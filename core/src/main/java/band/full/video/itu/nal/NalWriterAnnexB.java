package band.full.video.itu.nal;

import static band.full.video.itu.nal.NalBuffer.getByteIndex;
import static java.lang.System.arraycopy;

import java.io.IOException;
import java.io.OutputStream;

public abstract class NalWriterAnnexB<C extends NalContext,
        U extends NalUnit<C>> implements AutoCloseable {
    private final NalBuffer buf;

    private final OutputStream out;

    public NalWriterAnnexB(OutputStream out) throws IOException {
        this.buf = new NalBuffer();
        this.out = out;
    }

    /** for unit testing */
    NalWriterAnnexB(NalBuffer buf) throws IOException {
        this.buf = buf;
        this.out = null;
    }

    @Override
    public void close() throws IOException {
        flush();
        out.close();
    }

    public void flush() throws IOException {
        out.write(buf.bytes, buf.offset, buf.getByteIndex());
        buf.offset = buf.pos = buf.end = 0;
    }

    public void write(C context, U nalu) throws IOException {
        writeStartCodePrefix(nalu.zero_byte);

        RbspWriter out = new RbspWriter(buf);

        if (buf.getByteShift() != 0)
            throw new IllegalStateException("RBSP buffer is unaligned!");

        writeHeader(out, nalu);
        nalu.write(context, out);
        insertEmulationPreventionBytes();
        if (buf.pos >= 524_288) { // 64kB*8bit
            flush();
        }
    }

    private void writeStartCodePrefix(boolean zero_byte) {
        int index = buf.getByteIndex();
        if (zero_byte) {
            buf.bytes[index++] = 0;
            buf.pos += 8;
        }
        buf.bytes[index++] = 0;
        buf.bytes[index++] = 0;
        buf.bytes[index++] = 1;
        buf.end = buf.pos += 24;
    }

    protected abstract void writeHeader(RbspWriter out, U nalu);

    void insertEmulationPreventionBytes() throws IOException {
        int count = countEmulationPreventionByte();
        int pos = buf.getByteIndex();
        buf.checkWrite(count << 3);
        byte[] bytes = buf.bytes;

        int end = pos;
        for (; count > 0;) {
            while (bytes[--pos] != 0x00 || (bytes[pos + 1] & 0xFC) != 0) {
            }
            int z = pos + 1, n = 1;
            for (; bytes[--pos] == 0x00; n++) {
            }
            ++pos;

            if (n > 1) {
                arraycopy(bytes, z, bytes, z + count, end - z);

                int i = z + count;
                if ((n & 1) != 0) {
                    bytes[--i] = 0x00;
                }

                n >>= 1;

                for (; n > 0; n--) {
                    bytes[--i] = 0x03;
                    bytes[--i] = 0x00;
                    bytes[--i] = 0x00;
                    --count;
                }

                end = pos;
            }
        }
    }

    private int countEmulationPreventionByte() {
        byte[] bytes = buf.bytes;

        int pos = getByteIndex(buf.end);
        int end = buf.getByteIndex() - 1;

        int count = 0;
        while (pos < end) {
            if (bytes[pos++] == 0x00 && bytes[pos++] == 0x00) {
                if (pos == end + 1 || (bytes[pos] & 0xFC) == 0) {
                    ++count;
                }
            }
        }
        return count;
    }
}
