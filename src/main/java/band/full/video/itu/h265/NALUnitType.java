package band.full.video.itu.h265;

import java.util.function.Supplier;

public enum NALUnitType {
    TRAIL_N("Coded slice segment of a non-TSA, non-STSA trailing picture N"),
    TRAIL_R("Coded slice segment of a non-TSA, non-STSA trailing picture R"),
    TSA_N("Coded slice segment of a TSA picture N"),
    TSA_R("Coded slice segment of a TSA picture R"),
    STSA_N("Coded slice segment of an STSA picture N"),
    STSA_R("Coded slice segment of an STSA picture R"),
    RADL_N("Coded slice segment of a RADL picture N"),
    RADL_R("Coded slice segment of a RADL picture R"),
    RASL_N("Coded slice segment of a RASL picture N"),
    RASL_R("Coded slice segment of a RASL picture R"),
    RSV_VCL_N10("Reserved non-IRAP SLNR VCL 10"),
    RSV_VCL_R11("Reserved non-IRAP sub-layer reference VCL 11"),
    RSV_VCL_N12("Reserved non-IRAP SLNR VCL 12"),
    RSV_VCL_R13("Reserved non-IRAP sub-layer reference VCL 13"),
    RSV_VCL_N14("Reserved non-IRAP SLNR VCL 14"),
    RSV_VCL_R15("Reserved non-IRAP sub-layer reference VCL 15"),
    BLA_W_LP("Coded slice segment of a BLA picture W_LP"),
    BLA_W_RADL("Coded slice segment of a BLA picture W_RADL"),
    BLA_N_LP("Coded slice segment of a BLA picture N_LP"),
    IDR_W_RADL("Coded slice segment of an IDR picture W_RADL"),
    IDR_N_LP("Coded slice segment of an IDR picture N_LP"),
    CRA_NUT("Coded slice segment of a CRA picture"),
    RSV_IRAP_VCL22("Reserved IRAP VCL 22"),
    RSV_IRAP_VCL23("Reserved IRAP VCL 23"),
    RSV_VCL24("Reserved non-IRAP VCL 24"),
    RSV_VCL25("Reserved non-IRAP VCL 25"),
    RSV_VCL26("Reserved non-IRAP VCL 26"),
    RSV_VCL27("Reserved non-IRAP VCL 27"),
    RSV_VCL28("Reserved non-IRAP VCL 28"),
    RSV_VCL29("Reserved non-IRAP VCL 29"),
    RSV_VCL30("Reserved non-IRAP VCL 30"),
    RSV_VCL31("Reserved non-IRAP VCL 31"),
    VPS_NUT("Video parameter set", VPS::new),
    SPS_NUT("Sequence parameter set"),
    PPS_NUT("Picture parameter set", PPS::new),
    AUD_NUT("Access unit delimiter", AUD::new),
    EOS_NUT("End of sequence"),
    EOB_NUT("End of bitstream"),
    FD_NUT("Filler data"),
    PREFIX_SEI_NUT("Supplemental enhancement information (prefix)",
            SEI::PREFIX_SEI),
    SUFFIX_SEI_NUT("Supplemental enhancement information (suffix)",
            SEI::SUFFIX_SEI),
    RSV_NVCL41("Reserved 41"),
    RSV_NVCL42("Reserved 42"),
    RSV_NVCL43("Reserved 43"),
    RSV_NVCL44("Reserved 44"),
    RSV_NVCL45("Reserved 45"),
    RSV_NVCL46("Reserved 46"),
    RSV_NVCL47("Reserved 47"),
    UNSPEC48("Unspecified 48"),
    UNSPEC49("Unspecified 49"),
    UNSPEC50("Unspecified 50"),
    UNSPEC51("Unspecified 51"),
    UNSPEC52("Unspecified 52"),
    UNSPEC53("Unspecified 53"),
    UNSPEC54("Unspecified 54"),
    UNSPEC55("Unspecified 55"),
    UNSPEC56("Unspecified 56"),
    UNSPEC57("Unspecified 57"),
    UNSPEC58("Unspecified 58"),
    UNSPEC59("Unspecified 59"),
    UNSPEC60("Unspecified 60"),
    UNSPEC61("Unspecified 61"),
    UNSPEC62("Unspecified 62"),
    UNSPEC63("Unspecified 63");

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
