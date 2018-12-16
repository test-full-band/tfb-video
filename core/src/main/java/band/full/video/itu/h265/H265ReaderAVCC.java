package band.full.video.itu.h265;

import band.full.video.itu.nal.NalReaderAVCC;
import band.full.video.itu.nal.RbspReader;

import java.io.IOException;
import java.io.InputStream;

public class H265ReaderAVCC extends NalReaderAVCC<H265Context, NALUnit> {
    public final H265Context context = new H265Context();

    public H265ReaderAVCC(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected H265Context context() {
        return context;
    }

    @Override
    protected NALUnit create(H265Context context, RbspReader in) {
        return NALUnit.create(context, in);
    }
}
