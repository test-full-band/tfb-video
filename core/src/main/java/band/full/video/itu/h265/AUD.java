package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.AUD_NUT;

import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

import java.io.PrintStream;

/**
 * 7.3.2.5 Access unit delimiter RBSP syntax
 * <p>
 * <code>access_unit_delimiter_rbsp()</code>
 *
 * @author Igor Malinin
 */
public class AUD extends NALUnit {
    public byte pic_type;
    public byte[] trailing_bits;

    public AUD() {
        super(AUD_NUT);
    }

    @Override
    public void read(H265Context context, RbspReader reader) {
        pic_type = reader.readUByte(3);
        trailing_bits = reader.readTrailingBits();
    }

    @Override
    public void write(H265Context context, RbspWriter writer) {
        writer.writeU(3, pic_type);
        writer.writeTrailingBits(trailing_bits);
    }

    @Override
    public void print(H265Context context, PrintStream ps) {
        ps.print("    pic_type: ");
        ps.println(pic_type);
    }
}
