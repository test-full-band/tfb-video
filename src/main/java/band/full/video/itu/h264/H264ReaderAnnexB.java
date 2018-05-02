package band.full.video.itu.h264;

import band.full.video.itu.nal.NalReaderAnnexB;
import band.full.video.itu.nal.RbspReader;

import java.io.IOException;
import java.io.InputStream;

public class H264ReaderAnnexB extends NalReaderAnnexB<NALUnit> {
    public H264ReaderAnnexB(InputStream in) throws IOException {
        super(in);
    }

    /** for unit testing */
    H264ReaderAnnexB(byte[] nal) {
        super(nal);
    }

    @Override
    protected NALUnit create(boolean zero_byte, RbspReader nalu) {
        return NALUnit.create(zero_byte, nalu);
    }
}
