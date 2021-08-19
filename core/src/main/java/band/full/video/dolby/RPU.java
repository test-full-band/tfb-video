package band.full.video.dolby;

import static band.full.core.CRC32.MPEG2;

import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

import java.nio.ByteBuffer;

/**
 * Reference Processing Unit Metadata Message.
 * <p>
 * <code>rpu_data_rbsp()</code>
 *
 * @author Igor Malinin
 */
public class RPU implements Structure<NalContext> {
    public static final int NUM_CMPS = 3;

    public RpuHeader header;

    public RpuDataMapping mapping;
    public RpuDataNLQ nlq;

    public VdrDmDataPayload dm;

    public RPU() {}

    public RPU(RpuHeader header, RpuDataMapping mapping, VdrDmDataPayload dm) {
        this(header, mapping, null, dm);
    }

    public RPU(RpuHeader header, RpuDataMapping mapping, RpuDataNLQ nlq,
            VdrDmDataPayload dm) {
        this.header = header;
        this.mapping = mapping;
        this.nlq = nlq;
        this.dm = dm;
    }

    public RPU(byte[] bytes) {
        RbspReader in = new RbspReader(bytes, 0, bytes.length);
        int prefix = in.u8();
        if (prefix != 0x19) // 25
            throw new IllegalArgumentException("Only prefix 0x19 is supported");

        int crc32 = MPEG2.checksum(bytes, 1, bytes.length - 6);
        ByteBuffer buf = ByteBuffer.wrap(bytes, bytes.length - 5, 5); // TODO
        if (crc32 != buf.getInt())
            throw new IllegalArgumentException("CRC32 doesn't match");

        if (-128 != buf.get()) // rbsp_trailing_bits() 0x80
            throw new IllegalArgumentException("RPU should end with 0x80 byte");

        read(null, in);

        if (in.available() != 40)
            throw new IllegalArgumentException("Unexpected offset");
    }

    public byte[] toBytes(int size) {
        RbspWriter out = new RbspWriter(new byte[size]);
        out.u8(0x19);
        write(null, out);
        out.u32(0); // CRC32 placeholder
        out.u8(0x80); // rbsp_trailing_bits()
        byte[] bytes = out.bytes();
        ByteBuffer crc = ByteBuffer.wrap(bytes, bytes.length - 5, 4); // TODO
        crc.putInt(MPEG2.checksum(bytes, 1, bytes.length - 6));
        return bytes;
    }

    @Override
    public void read(NalContext context, RbspReader in) {
        header = new RpuHeader();
        header.read(null, in);

        switch (header.rpu_type) {
            case 2 -> {
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
            }

            default -> throw new IllegalStateException(
                    "Unknown RPU type: " + header.rpu_type);
        }

        while (!in.isByteAligned())
            if (in.u1()) throw new IllegalStateException();
    }

    @Override
    public void write(NalContext context, RbspWriter out) {
        header.write(null, out);

        switch (header.rpu_type) {
            case 2 -> {
                if (!header.use_prev_vdr_rpu) {
                    mapping.write(header, out);
                    if ((header.rpu_format & 0x700) == 0
                            && !header.disable_residual) {
                        nlq.write(header, out);
                    }
                    if (header.vdr_dm_metadata_present) {
                        dm.write(header, out);
                    }
                }
            }

            default -> throw new IllegalStateException(
                    "Unknown RPU type: " + header.rpu_type);
        }

        while (!out.isByteAligned()) {
            out.u1(false);
        }
    }

    @Override
    public void print(NalContext context, RbspPrinter out) {
        header.print(null, out);

        switch (header.rpu_type) {
            case 2 -> {
                if (!header.use_prev_vdr_rpu) {
                    mapping.print(header, out);
                    if ((header.rpu_format & 0x700) == 0
                            && !header.disable_residual) {
                        nlq.print(header, out);
                    }
                    if (header.vdr_dm_metadata_present) {
                        dm.print(header, out);
                    }
                }
            }

            default -> throw new IllegalStateException(
                    "Unknown RPU type: " + header.rpu_type);
        }
    }
}
