package band.full.video.itu.nal;

import static java.lang.Math.max;
import static java.lang.System.arraycopy;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public abstract class NalReaderAVCC<C extends NalContext,
        U extends NalUnit<C>> extends NalReader<C, U> {
    private byte[] buf = new byte[1_048_576]; // 1MB = 8Mb

    /** RBSP read state: start of unread length prefixed nal_unit() bytes */
    private int offset;

    /** RBSP read state: end of unread buffer bytes */
    private int limit;

    private boolean eof;

    public NalReaderAVCC(InputStream in) throws IOException {
        super(in);
    }

    @Override
    public U read() throws IOException {
        var in = nalu();
        return in == null ? null : create(context(), in);
    }

    RbspReader nalu() throws IOException {
        if (eof && offset == limit) return null;

        loadBuffer(4);

        if (eof && offset == limit) return null;

        int length = buf[offset++] << 24 | (buf[offset++] & 0xFF) << 16
                | (buf[offset++] & 0xFF) << 8 | (buf[offset++] & 0xFF);

        loadBuffer(length);

        int end = offset + length;
        while (limit < end) {
            int n = in.read(buf, limit, buf.length - limit);
            if (n < 0) {
                eof = true;
                break;
            }
            limit += n;
        }

        int j = removeEmulationPreventionBytes(buf, offset, end);
        var in = new RbspReader(buf, offset, j - offset);
        offset += length;
        return in;
    }

    private void loadBuffer(int bytes) throws IOException {
        if (offset + bytes <= limit) return; // bytes are already in the buffer

        if (bytes > buf.length) {
            int size = buf.length << 1;
            while (size < bytes) {
                size <<= 1;
            }
            move(new byte[size]);
        } else if (offset == limit || offset + max(bytes, 65536) > buf.length) {
            move(buf); // just compact the buffer
        }

        int end = offset + bytes;

        while (limit < end) {
            int n = in.read(buf, limit, buf.length - limit);
            if (n < 0) {
                eof = true;
                break;
            }
            limit += n;
        }

        if (limit < end) throw new EOFException();
    }

    private void move(byte[] dest) {
        int length = limit - offset;
        arraycopy(buf, offset, buf = dest, offset = 0, limit = length);
    }
}
