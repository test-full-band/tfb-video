package band.full.video.itu.h264;

import band.full.video.itu.nal.NalReaderAnnexB;
import band.full.video.itu.nal.RbspReader;

import java.io.IOException;
import java.io.InputStream;

public class H264ReaderAnnexB extends NalReaderAnnexB<H264Context, NALUnit> {
    public final H264Context context = new H264Context();

    public H264ReaderAnnexB(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected H264Context context() {
        return context;
    }

    @Override
    protected NALUnit create(H264Context context, RbspReader in) {
        return NALUnit.create(context, in);
    }
}
