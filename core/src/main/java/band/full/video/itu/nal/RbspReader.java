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

    public int available() {
        return buf.end - buf.pos;
    }

    public boolean isByteAligned() {
        return buf.isByteAligned();
    }

    public int ue() {
        int leadingZeroBits = 0;
        while (!u1()) {
            ++leadingZeroBits;
        }

        return leadingZeroBits == 0 ? 0
                : (1 << leadingZeroBits) - 1 + readInt(leadingZeroBits, false);
    }

    public int se() {
        return ue2se(ue());
    }

    public static int ue2se(int ue) {
        int e = ue >> 1;
        return (ue & 0x1) == 0 ? -e : e + 1;
    }

    public byte[] readBytes(int size) {
        // TODO aligned case - fast
        byte[] array = new byte[size];
        for (int i = 0; i < size; i++) {
            array[i] = (byte) readInt(8, true);
        }

        return array;
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
            result[i] = i8();
        }

        if (lim < buf.end) {
            int bits = buf.end - buf.pos;
            int xx = readInt(bits, false);
            result[lim] = (byte) (xx << 8 - bits);
        }

        return result;
    }

    public boolean u1() {
        int pos = buf.checkRead(1);

        int index = getByteIndex(pos);
        int shift = getByteShift(pos);

        return (buf.bytes[index] & (0x80 >> shift)) != 0;
    }

    public byte u2() {
        return readUByte(2);
    }

    public byte u3() {
        return readUByte(3);
    }

    public byte u4() {
        return readUByte(4);
    }

    public byte u5() {
        return readUByte(5);
    }

    public byte u6() {
        return readUByte(6);
    }

    public byte u7() {
        return readUByte(7);
    }

    public short u8() {
        return readUShort(8);
    }

    public short u9() {
        return readUShort(9);
    }

    public short u10() {
        return readUShort(10);
    }

    public short u11() {
        return readUShort(11);
    }

    public short u12() {
        return readUShort(12);
    }

    public short u13() {
        return readUShort(13);
    }

    public short u14() {
        return readUShort(14);
    }

    public short u15() {
        return readUShort(15);
    }

    public int u16() {
        return readUInt(16);
    }

    public int u17() {
        return readUInt(17);
    }

    public int u18() {
        return readUInt(18);
    }

    public int u19() {
        return readUInt(19);
    }

    public int u20() {
        return readUInt(20);
    }

    public int u21() {
        return readUInt(21);
    }

    public int u22() {
        return readUInt(22);
    }

    public int u23() {
        return readUInt(23);
    }

    public int u24() {
        return readUInt(24);
    }

    public int u25() {
        return readUInt(25);
    }

    public int u26() {
        return readUInt(26);
    }

    public int u27() {
        return readUInt(27);
    }

    public int u28() {
        return readUInt(28);
    }

    public int u29() {
        return readUInt(29);
    }

    public int u30() {
        return readUInt(30);
    }

    public int u31() {
        return readUInt(31);
    }

    public long u32() {
        return readULong(32);
    }

    public byte i2() {
        return readByte(2);
    }

    public byte i3() {
        return readByte(3);
    }

    public byte i4() {
        return readByte(4);
    }

    public byte i5() {
        return readByte(5);
    }

    public byte i6() {
        return readByte(6);
    }

    public byte i7() {
        return readByte(7);
    }

    public byte i8() {
        return readByte(8);
    }

    public short i9() {
        return readShort(9);
    }

    public short i10() {
        return readShort(10);
    }

    public short i11() {
        return readShort(11);
    }

    public short i12() {
        return readShort(12);
    }

    public short i13() {
        return readShort(13);
    }

    public short i14() {
        return readShort(14);
    }

    public short i15() {
        return readShort(15);
    }

    public short i16() {
        return readShort(16);
    }

    public int i17() {
        return readInt(17);
    }

    public int i18() {
        return readInt(18);
    }

    public int i19() {
        return readInt(19);
    }

    public int i20() {
        return readInt(20);
    }

    public int i21() {
        return readInt(21);
    }

    public int i22() {
        return readInt(22);
    }

    public int i23() {
        return readInt(23);
    }

    public int i24() {
        return readInt(24);
    }

    public int i25() {
        return readInt(25);
    }

    public int i26() {
        return readInt(26);
    }

    public int i27() {
        return readInt(27);
    }

    public int i28() {
        return readInt(28);
    }

    public int i29() {
        return readInt(29);
    }

    public int i30() {
        return readInt(30);
    }

    public int i31() {
        return readInt(31);
    }

    public int i32() {
        return readInt(32);
    }

    public long i64() {
        return readLong(64);
    }

    public byte readUByte(int bits) {
        assert (bits < 8);
        return (byte) readInt(bits, false);
    }

    public byte readByte(int bits) {
        assert (bits <= 8);
        return (byte) readInt(bits, true);
    }

    public short readUShort(int bits) {
        assert (bits < 16);
        return (short) readInt(bits, false);
    }

    public short readShort(int bits) {
        assert (bits <= 16);
        return (short) readInt(bits, true);
    }

    public int readUInt(int bits) {
        assert (bits < 32);
        return readInt(bits, false);
    }

    public int readInt(int bits) {
        assert (bits <= 32);
        return readInt(bits, true);
    }

    public long readULong(int bits) {
        assert (bits < 64);
        return readLong(bits, false);
    }

    public long readLong(int bits) {
        assert (bits <= 64);
        return readLong(bits, true);
    }

    private int readInt(int bits, boolean sign) {
        int pos = buf.checkRead(bits), lim = buf.pos;

        int index = getByteIndex(pos);
        int shift = getByteShift(pos);

        int accumulator = sign && (buf.bytes[index] & (0x80 >> shift)) != 0
                ? -1 // initialize negative number read to all ones
                : 0; // reading either unsigned or positive number

        int sp = 8 - shift;
        int sb = sp - bits;
        if (sb > 0) { // all bits inside one byte
            int mask = (1 << bits) - 1;
            accumulator <<= bits; // needed for negative value
            accumulator |= buf.bytes[index] >> sb & mask;
            return accumulator;
        }

        if (shift != 0) { // head align
            int mask = (1 << sp) - 1;
            accumulator <<= sp; // for negative value when sign==true
            accumulator |= buf.bytes[index++] & mask;
            pos += sp;
        }

        for (sb = lim - pos; sb >= 8; sb = lim - pos) {
            accumulator <<= 8;
            accumulator |= buf.bytes[index++] & 0xFF;
            pos += 8;
        }

        if (sb != 0) { // tail
            accumulator <<= sb;
            accumulator |= (buf.bytes[index] & 0xFF) >> 8 - sb;
        }

        return accumulator;
    }

    private long readLong(int bits, boolean sign) {
        int pos = buf.checkRead(bits), lim = buf.pos;

        int index = getByteIndex(pos);
        int shift = getByteShift(pos);

        long accumulator = sign && (buf.bytes[index] & (0x80 >> shift)) != 0
                ? -1 // initialize negative number read to all ones
                : 0; // reading either unsigned or positive number

        int sp = 8 - shift;
        int sb = sp - bits;
        if (sb > 0) { // all bits inside one byte
            int mask = (1 << bits) - 1;
            accumulator <<= bits; // needed for negative value
            accumulator |= buf.bytes[index] >> sb & mask;
            return accumulator;
        }

        if (shift != 0) { // head align
            int mask = (1 << sp) - 1;
            accumulator <<= sp; // for negative value when sign==true
            accumulator |= buf.bytes[index++] & mask;
            pos += sp;
        }

        for (sb = lim - pos; sb >= 8; sb = lim - pos) {
            accumulator <<= 8;
            accumulator |= buf.bytes[index++] & 0xFF;
            pos += 8;
        }

        if (sb != 0) { // tail
            accumulator <<= sb;
            accumulator |= (buf.bytes[index] & 0xFF) >> 8 - sb;
        }

        return accumulator;
    }
}
