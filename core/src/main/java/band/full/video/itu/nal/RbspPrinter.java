package band.full.video.itu.nal;

import static band.full.core.ArrayMath.toHexString;
import static band.full.video.itu.nal.RbspWriter.leadingUEbits;
import static band.full.video.itu.nal.RbspWriter.se2ue;
import static java.lang.Integer.toBinaryString;
import static java.lang.Long.toBinaryString;
import static java.lang.Long.toHexString;
import static java.util.Arrays.fill;

import java.io.PrintStream;

public class RbspPrinter {
    private static final byte[] INDENT = new byte[32];
    static {
        fill(INDENT, (byte) ' ');
    }

    private static final byte[] ZPAD = new byte[63];
    static {
        fill(ZPAD, (byte) '0');
    }

    private final PrintStream out;
    private int indent;

    public RbspPrinter(PrintStream out) {
        this.out = out;
    }

    public RbspPrinter enter() {
        ++indent;
        return this;
    }

    public RbspPrinter leave() {
        --indent;
        return this;
    }

    private RbspPrinter indent() {
        out.write(INDENT, 0, indent * 2);
        return this;
    }

    private void endl() {
        out.println();
    }

    private RbspPrinter append(String str) {
        out.print(str);
        return this;
    }

    private RbspPrinter name(String name) {
        out.print(name);
        out.print(": ");
        return this;
    }

    private RbspPrinter value(boolean value) {
        out.print(value);
        return this;
    }

    private RbspPrinter value(int value) {
        out.print(value);
        return this;
    }

    private RbspPrinter value(long value) {
        out.print(value);
        return this;
    }

    private RbspPrinter bits(int bits, int value) {
        out.print("0b");
        zpad(bits, toBinaryString(value));
        return this;
    }

    private RbspPrinter bits(int bits, long value) {
        out.print("0b");
        zpad(bits, toBinaryString(value));
        return this;
    }

    private RbspPrinter ue(int ue) {
        out.print("0b");
        int leadingZeroBits = leadingUEbits(ue);
        zpad(leadingZeroBits + 1, toBinaryString(1));
        zpad(leadingZeroBits, toBinaryString(ue + 1 - (1 << leadingZeroBits)));
        return this;
    }

    private RbspPrinter hex(int bits, int value) {
        if (bits % 4 != 0) return bits(bits, value);

        out.print("0x");
        zpad(bits / 4, toHexString(value));
        return this;
    }

    private RbspPrinter hex(int bits, long value) {
        if (bits % 4 != 0) return bits(bits, value);

        out.print("0x");
        zpad(bits / 4, toHexString(value));
        return this;
    }

    private RbspPrinter hex(byte[] bytes) {
        out.print("0x");
        out.print(toHexString(bytes));
        return this;
    }

    private void zpad(int chars, String codes) {
        int length = codes.length();
        if (length > chars) {
            out.print(codes.substring(length - chars));
        } else {
            out.write(ZPAD, 0, chars - codes.length());
            out.print(codes);
        }
    }

    public void raw(String line) {
        indent().append(line).endl();
    }

    public void ue(String name, int ue) {
        indent().name(name).value(ue).append(", bits: ").ue(ue).endl();
    }

    public void se(String name, int se) {
        ue(name, se2ue(se));
    }

    public void u1(String name, boolean flag) {
        indent().name(name).value(flag).endl();
    }

    public void u2(String name, int value) {
        printU(name, 2, value);
    }

    public void u3(String name, int value) {
        printU(name, 3, value);
    }

    public void u4(String name, int value) {
        printU(name, 4, value);
    }

    public void u5(String name, int value) {
        printU(name, 5, value);
    }

    public void u6(String name, int value) {
        printU(name, 6, value);
    }

    public void u7(String name, int value) {
        printU(name, 7, value);
    }

    public void u8(String name, int value) {
        printU(name, 8, value);
    }

    public void u9(String name, int value) {
        printU(name, 9, value);
    }

    public void u10(String name, int value) {
        printU(name, 10, value);
    }

    public void u11(String name, int value) {
        printU(name, 11, value);
    }

    public void u12(String name, int value) {
        printU(name, 12, value);
    }

    public void u13(String name, int value) {
        printU(name, 13, value);
    }

    public void u14(String name, int value) {
        printU(name, 14, value);
    }

    public void u15(String name, int value) {
        printU(name, 15, value);
    }

    public void u16(String name, int value) {
        printU(name, 16, value);
    }

    public void u17(String name, int value) {
        printU(name, 17, value);
    }

    public void u18(String name, int value) {
        printU(name, 18, value);
    }

    public void u19(String name, int value) {
        printU(name, 19, value);
    }

    public void u20(String name, int value) {
        printU(name, 20, value);
    }

    public void u21(String name, int value) {
        printU(name, 21, value);
    }

    public void u22(String name, int value) {
        printU(name, 22, value);
    }

    public void u23(String name, int value) {
        printU(name, 23, value);
    }

    public void u24(String name, int value) {
        printU(name, 24, value);
    }

    public void u25(String name, int value) {
        printU(name, 25, value);
    }

    public void u26(String name, int value) {
        printU(name, 26, value);
    }

    public void u27(String name, int value) {
        printU(name, 27, value);
    }

    public void u28(String name, int value) {
        printU(name, 28, value);
    }

    public void u29(String name, int value) {
        printU(name, 29, value);
    }

    public void u30(String name, int value) {
        printU(name, 30, value);
    }

    public void u31(String name, int value) {
        printU(name, 31, value);
    }

    public void u32(String name, long value) {
        printU(name, 32, value);
    }

    public void i2(String name, int value) {
        printU(name, 2, value);
    }

    public void i3(String name, int value) {
        printU(name, 3, value);
    }

    public void i4(String name, int value) {
        printU(name, 4, value);
    }

    public void i5(String name, int value) {
        printU(name, 5, value);
    }

    public void i6(String name, int value) {
        printU(name, 6, value);
    }

    public void i7(String name, int value) {
        printU(name, 7, value);
    }

    public void i8(String name, int value) {
        printU(name, 8, value);
    }

    public void i9(String name, int value) {
        printU(name, 9, value);
    }

    public void i10(String name, int value) {
        printU(name, 10, value);
    }

    public void i11(String name, int value) {
        printU(name, 11, value);
    }

    public void i12(String name, int value) {
        printU(name, 12, value);
    }

    public void i13(String name, int value) {
        printU(name, 13, value);
    }

    public void i14(String name, int value) {
        printU(name, 14, value);
    }

    public void i15(String name, int value) {
        printU(name, 15, value);
    }

    public void i16(String name, int value) {
        printU(name, 16, value);
    }

    public void i17(String name, int value) {
        printU(name, 17, value);
    }

    public void i18(String name, int value) {
        printU(name, 18, value);
    }

    public void i19(String name, int value) {
        printU(name, 19, value);
    }

    public void i20(String name, int value) {
        printU(name, 20, value);
    }

    public void i21(String name, int value) {
        printU(name, 21, value);
    }

    public void i22(String name, int value) {
        printU(name, 22, value);
    }

    public void i23(String name, int value) {
        printU(name, 23, value);
    }

    public void i24(String name, int value) {
        printU(name, 24, value);
    }

    public void i25(String name, int value) {
        printU(name, 25, value);
    }

    public void i26(String name, int value) {
        printU(name, 26, value);
    }

    public void i27(String name, int value) {
        printU(name, 27, value);
    }

    public void i28(String name, int value) {
        printU(name, 28, value);
    }

    public void i29(String name, int value) {
        printU(name, 29, value);
    }

    public void i30(String name, int value) {
        printU(name, 30, value);
    }

    public void i31(String name, int value) {
        printU(name, 31, value);
    }

    public void i32(String name, int value) {
        printU(name, 32, value);
    }

    public void i64(String name, long value) {
        printULong(name, 64, value);
    }

    public void printU(String name, int bits, long value) {
        indent().name(name).value(value).endl();
    }

    public void printS(String name, int bits, int value) {
        indent().name(name).value(value).endl();
    }

    public void printULong(String name, int bits, long value) {
        indent().name(name).value(value).endl();
    }

    public void printSLong(String name, int bits, long value) {
        indent().name(name).value(value).endl();
    }

    public void printB(String name, int bits, int value) {
        indent().name(name).bits(bits, value).endl();
    }

    public void printB(String name, int bits, long value) {
        indent().name(name).bits(bits, value).endl();
    }

    public void printH(String name, int bits, int value) {
        indent().name(name).hex(bits, value).endl();
    }

    public void printH(String name, int bits, long value) {
        indent().name(name).hex(bits, value).endl();
    }

    public void printH(String name, byte[] bytes) {
        indent().name(name).hex(bytes).endl();
    }
}
