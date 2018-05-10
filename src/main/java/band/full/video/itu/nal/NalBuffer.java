package band.full.video.itu.nal;

import static java.lang.System.arraycopy;

public final class NalBuffer {
    /** NAL/RBSP read/write extensible byte buffer */
    byte[] bytes;

    /** Start of used buffer bytes */
    int offset;

    /** Current bit position for read/write */
    int pos;

    /** End bit position for read/write */
    int end;

    NalBuffer() {
        this(new byte[1_048_576]); // 1MB = 8Mb
    }

    NalBuffer(byte[] bytes) {
        this.bytes = bytes;
    }

    public static int getByteIndex(int pos) {
        return pos >> 3;
    }

    public static int getByteShift(int pos) {
        return pos & 0b111;
    }

    int getByteIndex() {
        return getByteIndex(pos);
    }

    int getByteShift() {
        return getByteShift(pos);
    }

    public boolean isByteAligned() {
        return getByteShift() == 0;
    }

    /**
     * @return <code>pos</code> cursor before it is moved
     */
    int checkRead(int bits) {
        if (pos + bits > end) throw new IllegalStateException(
                "End of RBSP, only " + (end - pos)
                        + " bits available, requested: " + bits);

        int off = pos;
        pos = off + bits;
        return off;
    }

    /**
     * @return <code>pos</code> cursor before it is moved
     */
    int checkWrite(int bits) {
        int p = pos + bits;
        if (p <= bytes.length << 3) {
            int off = pos;
            pos = p;
            return off;
        }

        int bp = p + 7 >> 3;
        move(bytes.length - bp + offset < 65536
                ? dest(bp - offset)
                : bytes);

        int off = pos;
        pos += bits;
        return off;
    }

    private void move(byte[] dest) {
        int diff = offset << 3;
        pos -= diff;
        end -= diff;
        arraycopy(bytes, offset, bytes = dest, 0, pos + 7 >> 3);
        offset = 0;
    }

    private byte[] dest(int capacity) {
        if (bytes.length < capacity) {
            int length = bytes.length << 1;
            while (length < capacity) {
                length <<= 1;
            }
            return new byte[length];
        }

        return bytes;
    }
}
