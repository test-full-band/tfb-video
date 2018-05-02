package band.full.video.itu.nal;

import static band.full.video.itu.nal.NalBuffer.getByteIndex;
import static band.full.video.itu.nal.NalBuffer.getByteShift;

public class RbspWriter {
    private final NalBuffer buf;

    RbspWriter(NalBuffer buf) {
        this.buf = buf;
    }

    public boolean isByteAligned() {
        return buf.isByteAligned();
    }

    public void writeU1(boolean flag) {
        int pos = buf.checkWrite(1);

        int index = getByteIndex(pos);
        int shift = getByteShift(pos);

        if (flag) {
            buf.bytes[index] |= (0x80 >> shift);
        } else {
            buf.bytes[index] &= ~(0x80 >> shift);
        }
    }

    public void writeUE(int ue) {
        // TODO
    }

    public void writeSE(int se) {
        writeUE(UE(se));
    }

    public static int UE(int se) {
        return se > 0 ? se * 2 - 1 : se * -2;
    }

    public void writeBytes(byte[] bytes) {
        // TODO
    }

    public void writeTrailingBits(byte[] bytes) {
        if (!isByteAligned()) {
            writeTrailingBitsUnaligned(bytes);
            return;
        }

        // TODO
    }

    void writeTrailingBitsUnaligned(byte[] bytes) {
        // TODO
    }

    public void writeS8(byte b) {
        writeU(8, b);
    }

    public void writeS16(short s) {
        writeU(16, s);
    }

    public void writeS32(int i) {
        writeULong(32, i);
    }

    public void writeS(int bits, int i) {
        writeU(bits, i);
    }

    public void writeU(int bits, int i) {
        // TODO
    }

    public void writeS64(long value) {
        writeLong(64, value);
    }

    public void writeULong(int bits, long value) {
        assert (bits < 64);
        writeLong(bits, value);
    }

    private void writeLong(int bits, long value) {
        int pos = buf.checkWrite(bits), lim = pos + bits;

        long accumulator = 0;

        int index = getByteIndex(pos);
        int shift = getByteShift(pos);

        int sb = 8 - shift - bits;
        if (sb > 0) { // all bits inside one byte
            int mask = (1 << bits) - 1;
            value &= mask;
            byte bi = buf.bytes[index];
            bi &= ~(mask << sb);
            bi |= value << sb;
            buf.bytes[index] = bi;
            return;
        }

        if (shift != 0) { // lead align
            int mask = (1 << 8 - shift) - 1;
            accumulator += buf.bytes[index++] & mask;
            pos += 8 - shift;
        }

        while (lim - pos >= 8) {
            accumulator <<= 8;
            accumulator += buf.bytes[index++] & 0xFF;
            pos += 8;
        }

        sb = lim - pos;
        if (sb != 0) { // remaining
            accumulator <<= sb;
            accumulator += (buf.bytes[index] & 0xFF) >> 8 - sb;
        }

        // TODO
    }
}
