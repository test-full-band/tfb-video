package band.full.video.itu.h265;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

/**
 * Generic NAL Unit for all unknow types, stores RBSP bytes.
 *
 * @author Igor Malinin
 */
public class NALU extends NALUnit {
    public byte[] bytes;

    public NALU(NALUnitType type) {
        super(type);
    }

    public NALU(NALUnitType type, byte[] bytes) {
        super(type);
        this.bytes = bytes;
    }

    @Override
    public void read(H265Context context, RbspReader in) {
        bytes = in.readTrailingBits();
    }

    @Override
    public void write(H265Context context, RbspWriter out) {
        out.writeTrailingBits(bytes);
    }

    @Override
    public void print(H265Context context, RbspPrinter out) {
        out.i32("size", bytes.length);
        if (bytes.length <= 512) {
            out.printH("bytes", bytes);
        }
    }
}
