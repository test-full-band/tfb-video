package band.full.video.itu.nal;

import static band.full.video.itu.nal.NalBuffer.getByteIndex;
import static band.full.video.itu.nal.NalBuffer.getByteShift;
import static java.lang.System.arraycopy;

public class RbspReader {
    private final NalBuffer buf;

    RbspReader(NalBuffer buf) {
        this.buf = buf;
    }

    public RbspReader(byte[] rbsp, int offset, int length) {
        buf = new NalBuffer();
        buf.bytes = rbsp;
        buf.offset = offset;
        buf.pos = offset << 3;
        buf.end = offset + length << 3;
    }

    public boolean isByteAligned() {
        return buf.isByteAligned();
    }

    public boolean readU1() {
        int pos = buf.checkRead(1);

        int index = getByteIndex(pos);
        int shift = getByteShift(pos);

        return (buf.bytes[index] & (0x80 >> shift)) != 0;
    }

    public int readUE() {
        int leadingZeroBits = 0;
        while (!readU1()) {
            ++leadingZeroBits;
        }

        if (leadingZeroBits == 0) return 0;

        return (1 << leadingZeroBits) - 1 + readInt(leadingZeroBits, false);
    }

    public int readSE() {
        return SE(readUE());
    }

    public static int SE(int ue) {
        int e = ue >> 1;
        return (ue & 0x1) == 0 ? -e : e + 1;
    }

    public byte readByte() {
        return (byte) readInt(8, true);
    }

    public byte[] readBytes(int size) {
        // TODO aligned case - fast
        byte[] array = new byte[size];
        for (int i = 0; i < size; i++) {
            array[i] = (byte) readInt(8, true);
        }

        return array;
    }

    public int available() {
        return buf.end - buf.pos;
    }

    public byte[] readTrailingBits() {
        if (!isByteAligned()) return readTrailingBitsUnaligned();

        int off = buf.getByteIndex();
        int len = getByteIndex(buf.end) - off;
        byte[] result = new byte[len];
        arraycopy(buf.bytes, off, result, 0, len);
        buf.pos = buf.end;

        return result;
    }

    private byte[] readTrailingBitsUnaligned() {
        byte[] result = new byte[getByteIndex(buf.end - buf.pos + 7)];

        // TODO optimize
        int lim = getByteIndex(buf.end - buf.pos);
        for (int i = 0; i < lim; i++) {
            result[i] = readByte();
        }

        if (lim < buf.end) {
            int bits = buf.end - buf.pos;
            int xx = readInt(bits, false);
            result[lim] = (byte) (xx << 8 - bits);
        }

        return result;
    }

    public byte readS8() {
        return (byte) readInt(8, true);
    }

    public byte readUByte(int bits) {
        assert (bits < 8);
        return (byte) readInt(bits, false);
    }

    public short readS16() {
        return (short) readInt(16, true);
    }

    public short readSShort(int bits) {
        assert (bits < 16);
        return (short) readInt(bits, true);
    }

    public short readUShort(int bits) {
        assert (bits < 16);
        return (short) readInt(bits, false);
    }

    public int readS32() {
        return readInt(32, true);
    }

    public int readSInt(int bits) {
        assert (bits < 32);
        return readInt(bits, true);
    }

    public int readUInt(int bits) {
        assert (bits < 32);
        return readInt(bits, false);
    }

    private int readInt(int bits, boolean sign) {
        int pos = buf.checkRead(bits), lim = buf.pos;

        int accumulator = sign ? -1 : 0;

        int index = getByteIndex(pos);
        int shift = getByteShift(pos);

        int sb = 8 - shift - bits;
        if (sb > 0) { // all bits inside one byte
            int mask = (1 << bits) - 1;
            return buf.bytes[index] >> sb & mask;
        }

        if (shift != 0) { // head align
            int mask = (1 << 8 - shift) - 1;
            accumulator += buf.bytes[index++] & mask; // TODO sign=true?
            pos += 8 - shift;
        }

        while (lim - pos >= 8) {
            accumulator <<= 8;
            accumulator += buf.bytes[index++] & 0xFF;
            pos += 8;
        }

        sb = lim - pos;
        if (sb != 0) { // tail
            accumulator <<= sb;
            accumulator += (buf.bytes[index] & 0xFF) >> 8 - sb;
        }

        return accumulator;
    }

    public long readS64() {
        return readLong(64);
    }

    public long readULong(int bits) {
        assert (bits < 64);
        return readLong(bits);
    }

    private long readLong(int bits) {
        int pos = buf.checkRead(bits), lim = buf.pos;

        long accumulator = 0;

        int index = getByteIndex(pos);
        int shift = getByteShift(pos);

        int sb = 8 - shift - bits;
        if (sb > 0) { // all bits inside one byte
            int mask = (1 << bits) - 1;
            return buf.bytes[index] >> sb & mask;
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

        return accumulator;
    }
}
