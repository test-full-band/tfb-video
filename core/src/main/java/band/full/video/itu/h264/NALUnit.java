package band.full.video.itu.h264;

import band.full.video.itu.nal.NalUnit;
import band.full.video.itu.nal.RbspReader;

public abstract class NALUnit extends NalUnit<H264Context> {
    // nal_unit_header
    public byte nal_ref_idc;
    public final NALUnitType type;

    public NALUnit(NALUnitType type) {
        this.type = type;
    }

    @Override
    public String getTypeString() {
        return type.fullName + " (" + type.shortName + ")";
    }

    @Override
    public String getHeaderParamsString() {
        return "RefIDC " + nal_ref_idc;
    }

    public static NALUnit create(H264Context context, RbspReader reader) {
        return create(context, reader, false);
    }

    public static NALUnit create(H264Context context, RbspReader reader,
            boolean zero_byte) {
        // nal_unit_header
        if (reader.readU1())
            throw new IllegalStateException("forbidden_zero_bit is 1");

        byte nal_ref_idc = reader.readUByte(2);
        NALUnitType type = NALUnitType.get(reader.readUInt(5));
        NALUnit nalu = type.createNALU();

        nalu.zero_byte = zero_byte;
        nalu.nal_ref_idc = nal_ref_idc;

        // *_rbsp()
        nalu.read(context, reader);
        return nalu;
    }
}
