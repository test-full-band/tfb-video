package band.full.video.itu.h265;

import band.full.video.itu.nal.NalReaderAnnexB;
import band.full.video.itu.nal.RbspReader;

import java.io.IOException;
import java.io.InputStream;

public class H265ReaderAnnexB extends NalReaderAnnexB<NALUnit> {
    public H265ReaderAnnexB(InputStream in) throws IOException {
        super(in);
    }

    /** for unit testing */
    H265ReaderAnnexB(byte[] nal) {
        super(nal);
    }

    @Override
    protected NALUnit create(boolean zero_byte, RbspReader nalu) {
        return NALUnit.create(zero_byte, nalu);
    }
}
