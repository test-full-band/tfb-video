package band.full.video.dolby;

import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

/**
 * Reference Processing Unit Metadata Message.
 * <p>
 * <code>rpu_data_rbsp()</code>
 *
 * @author Igor Malinin
 */
public class RpuData implements Structure<NalContext> {
    public static final int NUM_CMPS = 3;

    public RpuHeader header;

    public RpuDataMapping mapping;
    public RpuDataNLQ nlq;

    public VdrDmDataPayload dm;

    @Override
    public void read(NalContext context, RbspReader in) {
        header = new RpuHeader();
        header.read(null, in);

        switch (header.rpu_type) {
            case 2:
                if (!header.use_prev_vdr_rpu) {
                    mapping = new RpuDataMapping();
                    mapping.read(header, in);
                    if ((header.rpu_format & 0x700) == 0
                            && !header.disable_residual) {
                        nlq = new RpuDataNLQ();
                        nlq.read(header, in);
                    }

                    if (header.vdr_dm_metadata_present) {
                        dm = new VdrDmDataPayload();
                        dm.read(header, in);
                    }
                }
                break;

            default:
                throw new IllegalStateException(
                        "Unknown RPU type: " + header.rpu_type);
        }

        while (!in.isByteAligned())
            if (in.u1()) throw new IllegalStateException();

        if (in.u8() != 0x80) // trailing_bits()
            throw new IllegalStateException();

        // int crc32 = MPEG2.checksum(n.bytes, 1, n.bytes.length - 6);
    }

    @Override
    public void write(NalContext context, RbspWriter out) {
        // TODO Auto-generated method stub

    }

    @Override
    public void print(NalContext context, RbspPrinter out) {
        // TODO Auto-generated method stub

    }
}
