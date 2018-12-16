package band.full.video.itu.h265;

import band.full.video.itu.nal.NalWriterAnnexB;
import band.full.video.itu.nal.RbspWriter;

import java.io.IOException;
import java.io.OutputStream;

public class H265WriterAnnexB extends NalWriterAnnexB<H265Context, NALUnit> {
    public H265WriterAnnexB(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeHeader(RbspWriter out, NALUnit nalu) {
        out.u1(false);
        out.u6(nalu.type.ordinal());
        out.u6(nalu.nuh_layer_id);
        out.u3(nalu.nuh_temporal_id_plus1);
    }
}
