package band.full.video.itu.h264;

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
    public void read(H264Context context, RbspReader in) {
        bytes = in.readTrailingBits();
    }

    @Override
    public void write(H264Context context, RbspWriter out) {
        out.writeTrailingBits(bytes);
    }

    @Override
    public void print(H264Context context, RbspPrinter out) {
        out.i32("size", bytes.length);
        if (bytes.length <= 256) {
            out.printH("bytes", bytes);
        }
    }
}
