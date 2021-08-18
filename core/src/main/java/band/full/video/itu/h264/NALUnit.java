package band.full.video.itu.h264;

import band.full.video.itu.nal.NalUnit;
import band.full.video.itu.nal.RbspReader;

public abstract class NALUnit extends NalUnit<H264Context> {
    // nal_unit_header
    public byte nal_ref_idc;
    public final NALUnitType type;

    public NALUnit(NALUnitType type) {
        this.type = type;
        zero_byte = isZeroByteRequired();
    }

    @Override
    public boolean isZeroByteRequired() {
        return switch (type) {
            case SPS_NUT, PPS_NUT, AUD_NUT -> true;
            default -> false;
        };
    }

    @Override
    public String getTypeString() {
        return type.fullName + " (" + type.shortName + ")";
    }

    @Override
    public String getHeaderParamsString() {
        return "RefIDC " + nal_ref_idc;
    }

    public static NALUnit create(H264Context context, RbspReader in) {
        // nal_unit_header
        if (in.u1())
            throw new IllegalStateException("forbidden_zero_bit is 1");

        byte nal_ref_idc = in.u2();
        NALUnitType type = NALUnitType.get(in.u5());
        NALUnit nalu = type.createNALU();

        nalu.nal_ref_idc = nal_ref_idc;

        // *_rbsp()
        nalu.read(context, in);
        return nalu;
    }
}
