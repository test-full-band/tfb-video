package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.AUD_NUT;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

/**
 * 7.3.2.5 Access unit delimiter RBSP syntax
 * <p>
 * <code>access_unit_delimiter_rbsp()</code>
 *
 * @author Igor Malinin
 */
public class AUD extends NALUnit {
    public byte pic_type; // u(3)
    public byte[] trailing_bits;

    public AUD() {
        super(AUD_NUT);
    }

    @Override
    public void read(H265Context context, RbspReader in) {
        pic_type = in.u3();
        trailing_bits = in.readTrailingBits();
    }

    @Override
    public void write(H265Context context, RbspWriter out) {
        out.u3(pic_type);
        out.writeTrailingBits(trailing_bits);
    }

    @Override
    public void print(H265Context context, RbspPrinter out) {
        out.u3("pic_type", pic_type);
    }
}
