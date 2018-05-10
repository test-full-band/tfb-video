package band.full.video.itu.nal;

import static band.full.video.itu.nal.NalBuffer.getByteIndex;
import static band.full.video.itu.nal.NalBuffer.getByteShift;
import static java.lang.System.arraycopy;

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

    public static int countUEbits(int ue) {
        return leadingUEbits(ue) * 2 + 1;
    }

    private static int leadingUEbits(int ue) {
        int leadingZeroBits = 0;
        for (int ue1 = ue + 1; ue1 > 1; ue1 >>= 1) {
            ++leadingZeroBits;
        }
        return leadingZeroBits;
    }

    public void writeUE(int ue) {
        int leadingZeroBits = leadingUEbits(ue);
        writeU(leadingZeroBits + 1, 1);
        writeU(leadingZeroBits, ue + 1 - (1 << leadingZeroBits));
    }

    public void writeSE(int se) {
        writeUE(UE(se));
    }

    public static int UE(int se) {
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
            writeS8(bytes[i]);
        }

        int shift = buf.getByteShift();
        int bits = 8 - shift;
        int mask = (1 << shift) - 1;
        byte value = bytes[bytes.length - 1];
        if ((value & mask) == 0) {
            writeU(bits, value >> shift);
        } else {
            writeS8(value);
            writeU(bits, 0);
        }
    }

    public void writeS8(byte b) {
        writeU(8, b);
    }

    public void writeS16(short s) {
        writeU(16, s);
    }

    public void writeS32(int value) {
        writeInt(32, value);
    }

    public void writeU(int bits, long value) {
        assert (bits < 32);
        writeLong(bits, value);
    }

    public void writeS(int bits, int value) {
        assert (bits <= 32);
        writeU(bits, value);
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

    public void writeS64(long value) {
        writeLong(64, value);
    }

    public void writeULong(int bits, long value) {
        assert (bits < 64);
        writeLong(bits, value);
    }

    public void writeSLong(int bits, long value) {
        assert (bits <= 64);
        writeLong(bits, value);
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
