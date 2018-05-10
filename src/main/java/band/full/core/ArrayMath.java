package band.full.core;

import java.io.ByteArrayOutputStream;
import java.util.function.DoubleUnaryOperator;

/**
 * @author Igor Malinin
 */
public class ArrayMath {
    private ArrayMath() {}

    public static double[] add(double[] in, double[] out, int mul) {
        for (int i = 0; i < in.length; i++) {
            out[i] = in[i] + mul;
        }

        return out;
    }

    public static int[] multiply(int[] in, int[] out, int mul) {
        for (int i = 0; i < in.length; i++) {
            out[i] = in[i] * mul;
        }

        return out;
    }

    public static double[] multiply(double[] in, double[] out, double mul) {
        for (int i = 0; i < in.length; i++) {
            out[i] = in[i] * mul;
        }

        return out;
    }

    public static double[] apply(double[] in, double[] out,
            DoubleUnaryOperator op) {
        for (int i = 0; i < in.length; i++) {
            out[i] = op.applyAsDouble(in[i]);
        }

        return out;
    }

    private final static String[] BINARY = {
        "0000", "0001", "0010", "0011",
        "0100", "0101", "0110", "0111",
        "1000", "1001", "1010", "1011",
        "1100", "1101", "1110", "1111"
    };

    public static String toBinaryString(byte[] bytes) {
        char[] chars = new char[bytes.length * 8];

        for (int i = 0, j = 0; i < bytes.length;) {
            int v = bytes[i++] & 0xFF;
            String b = BINARY[v >>> 4];
            chars[j++] = b.charAt(0);
            chars[j++] = b.charAt(1);
            chars[j++] = b.charAt(2);
            chars[j++] = b.charAt(3);
            b = BINARY[v & 0x0F];
            chars[j++] = b.charAt(0);
            chars[j++] = b.charAt(1);
            chars[j++] = b.charAt(2);
            chars[j++] = b.charAt(3);
        }

        return new String(chars);
    }

    private final static char[] HEX = "0123456789ABCDEF".toCharArray();

    public static String toHexString(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];

        for (int i = 0, j = 0; i < bytes.length;) {
            int v = bytes[i++] & 0xFF;
            chars[j++] = HEX[v >>> 4];
            chars[j++] = HEX[v & 0x0F];
        }

        return new String(chars);
    }

    public static byte[] fromHexString(String chars) {
        var out = new ByteArrayOutputStream(chars.length() / 2);

        for (int i = 0; i < chars.length();) {
            int msb = fromHexChar(chars.charAt(i++));
            if (msb < 0) {
                continue; // skip separator characters
            }

            int lsb = fromHexChar(chars.charAt(i++));
            if (lsb < 0) throw new IllegalArgumentException(
                    "chars[" + (i - 1) + "] = " + chars.charAt(i - 1));

            out.write((msb << 4) + lsb);
        }

        return out.toByteArray();
    }

    public static int fromHexChar(char ch) {
        if ('0' <= ch && ch <= '9') return ch - '0';
        if ('A' <= ch && ch <= 'F') return ch - 'A' + 10;
        return -1;
    }
}
