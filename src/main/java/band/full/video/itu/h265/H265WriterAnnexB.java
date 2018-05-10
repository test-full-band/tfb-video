package band.full.video.itu.h265;

import band.full.video.itu.nal.NalWriterAnnexB;
import band.full.video.itu.nal.RbspWriter;

import java.io.IOException;
import java.io.OutputStream;

public class H265WriterAnnexB extends NalWriterAnnexB<NALUnit> {
    public H265WriterAnnexB(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeHeader(RbspWriter writer, NALUnit nalu) {
        writer.writeU1(false);
        writer.writeU(6, nalu.type.ordinal());
        writer.writeU(6, nalu.nuh_layer_id);
        writer.writeU(3, nalu.nuh_temporal_id_plus1);
    }
}
