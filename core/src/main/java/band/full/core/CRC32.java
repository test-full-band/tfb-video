package band.full.core;

import static band.full.core.ArrayMath.toHexString;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.setAll;

public class CRC32 {
    public static final CRC32 IEEE = new CRC32(true, true, -1, -1, 0x04C11DB7);

    public static final CRC32 MPEG2 =
            new CRC32(false, false, -1, 0, IEEE.polynomial);

    public final boolean reflectIn, reflectOut;
    public final int init, finalXOR;
    public final int polynomial;
    private final int table[];

    public CRC32(boolean reflectIn, boolean reflectOut,
            int init, int finalXOR, int polynomial) {
        this.reflectIn = reflectIn;
        this.reflectOut = reflectOut;
        this.init = init;
        this.finalXOR = finalXOR;
        this.polynomial = polynomial;

        setAll(table = new int[256], this::calculate);
    }

    int calculate(int index) {
        int b = reflectIn ? reflect8(index) : index, value = 0;

        for (int j = 0x80; j != 0; j >>= 1) {
            int bit = value & MIN_VALUE;
            value <<= 1;

            if ((b & j) != 0) {
                bit ^= MIN_VALUE;
            }

            if (bit != 0) {
                value ^= polynomial;
            }
        }

        return reflectIn ? reflect32(value) : value;
    }

    /**
     * @see http://graphics.stanford.edu/~seander/bithacks.html
     */
    private static int reflect8(int b) {
        long l = (((b & 0xFF) * 0x80200802L) & 0x0884422110L) * 0x0101010101L;
        return (int) (l >> 32 & 0xFF);
    }

    private static int reflect32(int in) {
        return reflect8((byte) in) << 24 | reflect8((byte) (in >> 8)) << 16 |
                reflect8((byte) (in >> 16)) << 8 | reflect8((byte) (in >> 24));
    }

    public int update(int value, byte[] buf) {
        return update(value, buf, 0, buf.length);
    }

    public int update(int value, byte[] buf, int offset, int length) {
        int curValue = value;

        if (reflectIn) {
            for (int i = offset, end = offset + length; i < end; i++) {
                curValue = updateR(curValue, buf[i]);
            }
        } else {
            for (int i = offset, end = offset + length; i < end; i++) {
                curValue = updateN(curValue, buf[i]);
            }
        }

        return curValue;
    }

    public int update(int value, byte b) {
        return reflectIn ? updateR(value, b) : updateN(value, b);
    }

    private int updateR(int value, byte b) {
        return table[(value ^ b) & 0xFF] ^ (value >>> 8);
    }

    private int updateN(int value, byte b) {
        return table[((value >>> 24) ^ b) & 0xFF] ^ (value << 8);
    }

    public int finalize(int value) {
        return (reflectOut == reflectIn ? value : reflect32(value)) ^ finalXOR;
    }

    public int checksum(byte[] buf) {
        return finalize(update(init, buf, 0, buf.length));
    }

    public int checksum(byte[] buf, int offset, int length) {
        return finalize(update(init, buf, offset, length));
    }

    public static void main(String[] args) {
        byte[] test = ArrayMath.fromHexString(
                "08090840613670AE60007F1FC7F1FC7F"
                        + "612007FF003FF0007FFD414E0E7151F81"
                        + "EC44F2543079ED1ABE2BED445150BCBEB"
                        + "3F726B0FB10F51C51A4F98BA5E21060D4"
                        + "10780536979468209E9F23A5800000539"
                        + "E7C84B3023390719412B1894625C5F1A1"
                        + "C11EEF09A1EF642B496CE3746396CD454"
                        + "234961EA44A6BFCB7EA3493144AF0FA2A"
                        + "778F32E3CCB84974B516877649D3A73E4"
                        + "45B96B6B000000AEE83886D3DA08B0AE1"
                        + "258DF33057DA905330C3B32D849A1221A"
                        + "9B1E42746B5EABB969B6B7CDFE59EF9C7"
                        + "112ED57E04165B849B98D28DEF8389A59"
                        + "02CDF2CD7B215457E2C8E80000037A9B1"
                        + "DA6003192000001C883920F8004F78000"
                        + "0076A0C3FC60013DA00000392B380001C"
                        + "CB12B3FC93777092B3A1EE00000200000"
                        + "010000000100000000B6A92F301A28504"
                        + "17F0030C800001539EACCCCD1EFD80720"
                        + "00A8E5A38103EE710A883008007FFA000"
                        + "0C0282180080080080080010002027157"
                        + "1500");
        System.out.println(
                "reflect 0xF0 = " + ArrayMath.toHexString(reflect8(0xF0)));
        System.out.println(
                "reflect 0x0F = " + ArrayMath.toHexString(reflect8(0x0F)));
        int x = 0;
        for (int i = 0; i < 1000000; i++) {
            x += CRC32.IEEE.update(-1, test);
        }
        long start = currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            x += CRC32.IEEE.update(-1, test);
        }
        int crc = CRC32.MPEG2.checksum(test);
        System.out.println((currentTimeMillis() - start) + " ms");
        System.out.println(x);
        System.out.println(toHexString(crc)); // 3C7257FB
    }
}
