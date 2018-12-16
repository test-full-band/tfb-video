package band.full.video.dolby;

import static java.lang.Float.intBitsToFloat;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

public abstract class VdrRpuDataPayload implements Structure<RpuHeader> {
    protected int readCoefS(RpuHeader header, RbspReader in) {
        if (header.coefficient_data_type != 0)
            return in.i32(); // wrapped IEEE-754-2008 32 bit float

        int log2 = header.coefficient_log2_denom;

        int integer = in.se(); // 7 bit signed integer
        assert (-64 <= integer || integer < 64);

        return integer << log2 | in.readUInt(log2);
    }

    protected int readCoefU(RpuHeader header, RbspReader in) {
        if (header.coefficient_data_type != 0)
            return in.i32(); // wrapped IEEE-754-2008 32 bit float

        int integer = in.ue(); // 7 bit unsigned integer
        assert (integer < 128);

        return integer << header.coefficient_log2_denom
                | in.readUInt(header.coefficient_log2_denom);
    }

    protected void writeCoefS(RpuHeader header, RbspWriter out, int value) {
        if (header.coefficient_data_type != 0) {
            out.i32(value);// wrapped IEEE-754-2008 32 bit float
        } else {
            int log2 = header.coefficient_log2_denom;
            out.se(value >> log2);
            out.writeInt(log2, value);
        }
    }

    protected void writeCoefU(RpuHeader header, RbspWriter out, int value) {
        if (header.coefficient_data_type != 0) {
            out.i32(value);// wrapped IEEE-754-2008 32 bit float
        } else {
            int log2 = header.coefficient_log2_denom;
            out.ue(value >> log2);
            out.writeInt(log2, value);
        }
    }

    protected void printCoefS(RpuHeader header, RbspPrinter out,
            String name, int value) {
        if (header.coefficient_data_type != 0) {
            out.raw(name + ": " + intBitsToFloat(value) + ", u(32): " + value);
        } else {
            int log2 = header.coefficient_log2_denom;
            int one = 1 << log2;
            int coef_int = value >> log2;
            int coef = value & (one - 1);
            double real = value / (double) one;
            out.raw(name + ": " + real + ", se(v):" + coef_int
                    + ", u(" + log2 + "): " + coef);
        }
    }

    protected void printCoefU(RpuHeader header, RbspPrinter out,
            String name, int value) {
        if (header.coefficient_data_type != 0) {
            out.raw(name + ": " + intBitsToFloat(value) + ", u(32): " + value);
        } else {
            int log2 = header.coefficient_log2_denom;
            int one = 1 << log2;
            int coef_int = value >> log2;
            int coef = value & (one - 1);
            double real = value / (double) one;
            out.raw(name + ": " + real + ", ue(v):" + coef_int
                    + ", u(" + log2 + "): " + coef);
        }
    }
}
