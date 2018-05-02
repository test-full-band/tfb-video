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

    int checkRead(int bits) {
        if (pos + bits > end) throw new IllegalStateException(
                "End of RBSP, only " + (end - pos)
                        + " bits available, requested: " + bits);

        int off = pos;
        pos = off + bits;
        return off;
    }

    int checkWrite(int bits) {
        int pos = end + bits;
        if (pos <= bytes.length << 3) {
            end = pos;
            return 0;
        }

        int diff = 0; // TODO
        // = move(bytes.length - end + start < 65536
        // ? new byte[bytes.length * 2]
        // : bytes);

        end += bits;
        return diff;
    }

    private int move(byte[] dest) {
        int diff = offset;
        end -= offset;
        arraycopy(bytes, offset, bytes = dest, 0, end);
        offset = 0;
        return diff;
    }
}
