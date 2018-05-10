package band.full.video.itu.h265;

import band.full.video.itu.nal.NalUnit;
import band.full.video.itu.nal.RbspReader;

public abstract class NALUnit extends NalUnit {
    // nal_unit_header
    public final NALUnitType type;
    public byte nuh_layer_id;
    public byte nuh_temporal_id_plus1 = 1;

    public NALUnit(NALUnitType type) {
        this.type = type;
    }

    @Override
    public String getTypeString() {
        return type.fullName + " (" + type.shortName + ")";
    }

    @Override
    public String getHeaderParamsString() {
        return "Layer " + nuh_layer_id +
                ", TemporalID " + (nuh_temporal_id_plus1 - 1);
    }

    public static NALUnit create(RbspReader reader) {
        return create(false, reader);
    }

    public static NALUnit create(boolean zero_byte, RbspReader reader) {
        // nal_unit_header
        if (reader.readU1())
            throw new IllegalStateException("forbidden_zero_bit is 1");

        NALUnitType type = NALUnitType.get(reader.readUInt(6));
        NALUnit nalu = type.createNALU();

        nalu.zero_byte = zero_byte;
        nalu.nuh_layer_id = reader.readUByte(6);
        nalu.nuh_temporal_id_plus1 = reader.readUByte(3);

        // *_rbsp()
        nalu.read(reader);
        return nalu;
    }
}
