package band.full.video.itu.h265;

import band.full.video.itu.nal.NalUnit;
import band.full.video.itu.nal.RbspReader;

public abstract class NALUnit extends NalUnit<H265Context> {
    // nal_unit_header
    public final NALUnitType type;
    public byte nuh_layer_id;
    public byte nuh_temporal_id_plus1 = 1;

    public NALUnit(NALUnitType type) {
        this.type = type;
        zero_byte = isZeroByteRequired();
    }

    @Override
    public boolean isZeroByteRequired() {
        switch (type) {
            case VPS_NUT:
            case SPS_NUT:
            case PPS_NUT:
            case AUD_NUT:
                return true;

            default:
                return false;
        }
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

    public static NALUnit create(H265Context context, RbspReader in) {
        // nal_unit_header
        if (in.u1())
            throw new IllegalStateException("forbidden_zero_bit is 1");

        NALUnitType type = NALUnitType.get(in.u6());
        NALUnit nalu = type.createNALU();

        nalu.nuh_layer_id = in.u6();
        nalu.nuh_temporal_id_plus1 = in.u3();

        // *_rbsp()
        nalu.read(context, in);
        return nalu;
    }
}
