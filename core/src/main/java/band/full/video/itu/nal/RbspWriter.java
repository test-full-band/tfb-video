package band.full.video.itu.nal;

import static band.full.video.itu.nal.NalBuffer.getByteIndex;
import static band.full.video.itu.nal.NalBuffer.getByteShift;
import static java.lang.System.arraycopy;

public class RbspWriter {
    private final NalBuffer buf;

    RbspWriter(NalBuffer buf) {
        this.buf = buf;
    }

    public RbspWriter(byte[] rbsp) {
        buf = new NalBuffer(rbsp);
    }

    public byte[] bytes() {
        int size = buf.getByteIndex();
        byte[] bytes = new byte[size];
        arraycopy(buf.bytes, 0, bytes, 0, size);
        return bytes;
    }

    public boolean isByteAligned() {
        return buf.isByteAligned();
    }

    public static int countUEbits(int ue) {
        return leadingUEbits(ue) * 2 + 1;
    }

    static int leadingUEbits(int ue) {
        int leadingZeroBits = 0;
        for (int ue1 = ue + 1; ue1 > 1; ue1 >>= 1) {
            ++leadingZeroBits;
        }
        return leadingZeroBits;
    }

    public void ue(int ue) {
        int leadingZeroBits = leadingUEbits(ue);
        u(leadingZeroBits + 1, 1);
        u(leadingZeroBits, ue + 1 - (1 << leadingZeroBits));
    }

    public void se(int se) {
        ue(se2ue(se));
    }

    public static int se2ue(int se) {
        return se > 0 ? se * 2 - 1 : se * -2;
    }

    public void writeBytes(byte[] bytes) {
        // TODO aligned case - fast
        for (byte b : bytes) {
            writeInt(8, b);
        }
    }

    public void writeTrailingBits(byte[] bytes) {
        int len = bytes.length, bits = len << 3;
        int pos = buf.checkWrite(bits);

        if (!isByteAligned()) {
            writeTrailingBitsUnaligned(bytes, pos);
            return;
        }

        arraycopy(bytes, 0, buf.bytes, getByteIndex(pos), len);
    }

    void writeTrailingBitsUnaligned(byte[] bytes, int pos) {
        buf.pos = pos; // TODO optimize

        for (int i = 0; i < bytes.length - 1; i++) {
            i8(bytes[i]);
        }

        int shift = buf.getByteShift();
        int bits = 8 - shift;
        int mask = (1 << shift) - 1;
        byte value = bytes[bytes.length - 1];
        if ((value & mask) == 0) {
            u(bits, value >> shift);
        } else {
            i8(value);
            u(bits, 0);
        }
    }

    public void u1(boolean flag) {
        int pos = buf.checkWrite(1);

        int index = getByteIndex(pos);
        int shift = getByteShift(pos);

        if (flag) {
            buf.bytes[index] |= (0x80 >> shift);
        } else {
            buf.bytes[index] &= ~(0x80 >> shift);
        }
    }

    public void u2(int value) {
        writeInt(2, value);
    }

    public void u3(int value) {
        writeInt(3, value);
    }

    public void u4(int value) {
        writeInt(4, value);
    }

    public void u5(int value) {
        writeInt(5, value);
    }

    public void u6(int value) {
        writeInt(6, value);
    }

    public void u7(int value) {
        writeInt(7, value);
    }

    public void u8(int value) {
        writeInt(8, value);
    }

    public void u9(int value) {
        writeInt(9, value);
    }

    public void u10(int value) {
        writeInt(10, value);
    }

    public void u11(int value) {
        writeInt(11, value);
    }

    public void u12(int value) {
        writeInt(12, value);
    }

    public void u13(int value) {
        writeInt(13, value);
    }

    public void u14(int value) {
        writeInt(14, value);
    }

    public void u15(int value) {
        writeInt(15, value);
    }

    public void u16(int value) {
        writeInt(16, value);
    }

    public void u17(int value) {
        writeInt(17, value);
    }

    public void u18(int value) {
        writeInt(18, value);
    }

    public void u19(int value) {
        writeInt(19, value);
    }

    public void u20(int value) {
        writeInt(20, value);
    }

    public void u21(int value) {
        writeInt(21, value);
    }

    public void u22(int value) {
        writeInt(22, value);
    }

    public void u23(int value) {
        writeInt(23, value);
    }

    public void u24(int value) {
        writeInt(24, value);
    }

    public void u25(int value) {
        writeInt(25, value);
    }

    public void u26(int value) {
        writeInt(26, value);
    }

    public void u27(int value) {
        writeInt(27, value);
    }

    public void u28(int value) {
        writeInt(28, value);
    }

    public void u29(int value) {
        writeInt(29, value);
    }

    public void u30(int value) {
        writeInt(30, value);
    }

    public void u31(int value) {
        writeInt(31, value);
    }

    public void u32(long value) {
        writeInt(32, (int) value);
    }

    public void i2(int value) {
        writeInt(2, value);
    }

    public void i3(int value) {
        writeInt(3, value);
    }

    public void i4(int value) {
        writeInt(4, value);
    }

    public void i5(int value) {
        writeInt(5, value);
    }

    public void i6(int value) {
        writeInt(6, value);
    }

    public void i7(int value) {
        writeInt(7, value);
    }

    public void i8(int value) {
        writeInt(8, value);
    }

    public void i9(int value) {
        writeInt(9, value);
    }

    public void i10(int value) {
        writeInt(10, value);
    }

    public void i11(int value) {
        writeInt(11, value);
    }

    public void i12(int value) {
        writeInt(12, value);
    }

    public void i13(int value) {
        writeInt(13, value);
    }

    public void i14(int value) {
        writeInt(14, value);
    }

    public void i15(int value) {
        writeInt(15, value);
    }

    public void i16(int value) {
        writeInt(16, value);
    }

    public void i17(int value) {
        writeInt(17, value);
    }

    public void i18(int value) {
        writeInt(18, value);
    }

    public void i19(int value) {
        writeInt(19, value);
    }

    public void i20(int value) {
        writeInt(20, value);
    }

    public void i21(int value) {
        writeInt(21, value);
    }

    public void i22(int value) {
        writeInt(22, value);
    }

    public void i23(int value) {
        writeInt(23, value);
    }

    public void i24(int value) {
        writeInt(24, value);
    }

    public void i25(int value) {
        writeInt(25, value);
    }

    public void i26(int value) {
        writeInt(26, value);
    }

    public void i27(int value) {
        writeInt(27, value);
    }

    public void i28(int value) {
        writeInt(28, value);
    }

    public void i29(int value) {
        writeInt(29, value);
    }

    public void i30(int value) {
        writeInt(30, value);
    }

    public void i31(int value) {
        writeInt(31, value);
    }

    public void i32(int value) {
        writeInt(32, value);
    }

    public void i64(long value) {
        writeLong(64, value);
    }

    public void u(int bits, long value) {
        assert (bits < 32);
        writeLong(bits, value);
    }

    public void i(int bits, int value) {
        assert (bits <= 32);
        u(bits, value);
    }

    public void writeULong(int bits, long value) {
        assert (bits < 64);
        writeLong(bits, value);
    }

    public void writeSLong(int bits, long value) {
        assert (bits <= 64);
        writeLong(bits, value);
    }

    public void writeInt(int bits, int value) {
        int pos = buf.checkWrite(bits), lim = buf.pos - 1;

        int accumulator = value;

        // go backwards from least significant bits of value
        int index = getByteIndex(lim);
        int shift = 7 - getByteShift(lim);

        if (shift + bits < 8) { // all bits inside one byte
            int mask = (1 << bits) - 1;
            accumulator &= mask;
            byte b = buf.bytes[index];
            b &= ~(mask << shift);
            b |= accumulator << shift;
            buf.bytes[index] = b;
            return;
        }

        ++lim;

        if (shift != 0) { // tail align
            int l = 8 - shift;
            int mask = (1 << l) - 1;
            int a = accumulator & mask;
            accumulator >>= l;
            byte b = buf.bytes[index];
            b &= ~(mask << shift);
            b |= a << shift;
            buf.bytes[index--] = b;
            lim -= l;
        }

        while (lim - pos >= 8) {
            buf.bytes[index--] = (byte) accumulator;
            accumulator >>= 8;
            lim -= 8;
        }

        int sb = lim - pos;
        if (sb != 0) { // tail
            int mask = (1 << sb) - 1;
            byte b = buf.bytes[index];
            b &= ~mask;
            b |= accumulator & mask;
            buf.bytes[index] = b;
        }
    }

    private void writeLong(int bits, long value) {
        int pos = buf.checkWrite(bits), lim = buf.pos - 1;

        long accumulator = value;

        // go backwards from least significant bits of value
        int index = getByteIndex(lim);
        int shift = 7 - getByteShift(lim);

        if (shift + bits < 8) { // all bits inside one byte
            int mask = (1 << bits) - 1;
            accumulator &= mask;
            byte b = buf.bytes[index];
            b &= ~(mask << shift);
            b |= accumulator << shift;
            buf.bytes[index] = b;
            return;
        }

        ++lim;

        if (shift != 0) { // tail align
            int l = 8 - shift;
            int mask = (1 << l) - 1;
            long a = accumulator & mask;
            accumulator >>= l;
            byte b = buf.bytes[index];
            b &= ~(mask << shift);
            b |= a << shift;
            buf.bytes[index--] = b;
            lim -= l;
        }

        while (lim - pos >= 8) {
            buf.bytes[index--] = (byte) accumulator;
            accumulator >>= 8;
            lim -= 8;
        }

        int sb = lim - pos;
        if (sb != 0) { // tail
            int mask = (1 << sb) - 1;
            byte b = buf.bytes[index];
            b &= ~mask;
            b |= accumulator & mask;
            buf.bytes[index] = b;
        }
    }
}
