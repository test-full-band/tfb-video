package band.full.video.itu.h264;

import java.util.function.Supplier;

public enum NALUnitType {
    UNSPEC0("Unspecified 0"),
    CS_NIDR("Coded slice of a non-IDR picture"),
    CS_A("Coded slice data partition A"),
    CS_B("Coded slice data partition B"),
    CS_C("Coded slice data partition C"),
    CS_IDR("Coded slice of an IDR picture"),
    SEI_NUT("Supplemental enhancement information"),
    SPS_NUT("Sequence parameter set"),
    PPS_NUT("Picture parameter set"),
    AUD_NUT("Access unit delimiter"),
    EOS_NUT("End of sequence"),
    EOB_NUT("End of bitstream"),
    FD_NUT("Filler data"),
    SPS_EXT("Sequence parameter set extension"),
    PREFIX_NUT("Prefix NAL unit"),
    SUBSPS_NUT("Subset sequence parameter set"),
    DPS_NUT("Depth parameter set"),
    RSV_NVCL17("Reserved 17"),
    RSV_NVCL18("Reserved 18"),
    CS_AUX("Coded slice of an auxiliary coded picture without partitioning"),
    CS_EXT("Coded slice extension"),
    CS_3D("Coded slice extension for a depth view component or a 3D-AVC texture view component"),
    RSV_NVCL22("Reserved 22"),
    RSV_NVCL23("Reserved 23"),
    UNSPEC24("Unspecified 24"),
    UNSPEC25("Unspecified 25"),
    UNSPEC26("Unspecified 26"),
    UNSPEC27("Unspecified 27"),
    UNSPEC28("Unspecified 28"),
    UNSPEC29("Unspecified 29"),
    UNSPEC30("Unspecified 30"),
    UNSPEC31("Unspecified 31");

    public final String shortName;
    public final String fullName;
    public final Supplier<NALUnit> constructor;

    private NALUnitType(String fullName) {
        this(null, fullName, null);
    }

    private NALUnitType(String fullName, Supplier<NALUnit> constructor) {
        this(null, fullName, constructor);
    }

    private NALUnitType(String shortName, String fullName,
            Supplier<NALUnit> constructor) {
        String name = name();

        this.shortName = name.endsWith("_NUT")
                ? name.substring(0, name.length() - 4)
                : name;

        this.fullName = fullName;

        this.constructor = constructor == null
                ? () -> new NALU(this)
                : constructor;
    }

    public NALUnit createNALU() {
        return constructor.get();
    }

    private static NALUnitType[] CACHE = values();

    public static NALUnitType get(int ordinal) {
        try {
            return CACHE[ordinal];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
