package band.full.video.itu.h264;

import band.full.video.itu.nal.NalUnit;
import band.full.video.itu.nal.RbspReader;

public abstract class NALUnit extends NalUnit {
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

    public static NALUnit create(RbspReader reader) {
        return create(false, reader);
    }

    public static NALUnit create(boolean zero_byte, RbspReader reader) {
        // nal_unit_header
        if (reader.readU1())
            throw new IllegalStateException("forbidden_zero_bit is 1");

        byte nal_ref_idc = reader.readUByte(2);
        NALUnitType type = NALUnitType.get(reader.readUInt(5));
        NALUnit nalu = type.createNALU();

        nalu.zero_byte = zero_byte;
        nalu.nal_ref_idc = nal_ref_idc;

        // *_rbsp()
        nalu.read(reader);
        return nalu;
    }
}
