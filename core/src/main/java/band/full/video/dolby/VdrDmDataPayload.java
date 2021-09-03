package band.full.video.dolby;

import static java.lang.String.format;

import band.full.core.color.Matrix3x3;
import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;
import band.full.video.smpte.st2094.ST2094_10.ActiveArea;
import band.full.video.smpte.st2094.ST2094_10.ContentLightLevel;
import band.full.video.smpte.st2094.ST2094_10.ContentRange;
import band.full.video.smpte.st2094.ST2094_10.ContentRangeOffsets;
import band.full.video.smpte.st2094.ST2094_10.DisplayManagementBlock;
import band.full.video.smpte.st2094.ST2094_10.TemporallyFilteredImageLevel;
import band.full.video.smpte.st2094.ST2094_10.Reserved;
import band.full.video.smpte.st2094.ST2094_10.TrimPass;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public class VdrDmDataPayload implements Structure<NalContext> {
    public static final int YCCtoRGB_coef_scale = 1 << 13;
    public static final int YCCtoRGB_offset_scale = 1 << 28;
    public static final int RGBtoLMS_coef_scale = 1 << 14;

    public static final short[] DEFAULT_YCCtoRGB_coef = {
        8192, 0, 12900,
        8192, -1534, -3836,
        8192, 15201, 0,
    };

    public static final long[] DEFAULT_YCCtoRGB_offset = {
        0, 536870912, 536870912 // 0,
    };

    public static final short[] DEFAULT_RGBtoLMS_coef = {
        5845, 9702, 837, //
        2568, 12256, 1561, //
        0, 679, 15705, //
    };

    // Dolby Vision profile 5 IPTPQc2 coefficients

    /** IPT to LMS */
    public static final short[] IPTPQ_YCCtoRGB_coef = {
        8192, 799, 1681, // 1.0, 0.0976, 0.2052
        8192, -933, 1091, // 1.0, -0.1139, 0.1332
        8192, 267, -5545, // 1.0, 0.0326, -0.6769
    };

    /** IPT to LMS */
    public static final long[] IPTPQ_YCCtoRGB_offset = {
        0, 134217728, 134217728 // 0, 0.5, 0.5
    };

    /** LMS inverse cross-talk matrix for c == 0.02 */
    public static final short[] IPTPQc2_RGBtoLMS_coef = {
        17081, -349, -349,
        -349, 17081, -349,
        -349, -349, 17081,
    };

    public int affected_dm_metadata_id; // ue(v)
    public int current_dm_metadata_id; // ue(v)
    public int scene_refresh_flag; // ue(v)

    // i(16)
    public short[] YCCtoRGB_coef;

    // u(32)
    public long[] YCCtoRGB_offset;

    // i(16)
    public short[] RGBtoLMS_coef;

    public int signal_eotf = 0xFFFF; // u(16)
    public int signal_eotf_param0; // u(16)
    public int signal_eotf_param1; // u(16)
    public long signal_eotf_param2; // u(32)

    public byte signal_bit_depth; // u(5)

    /**
     * Color Space Representation:
     * <ol start="0">
     * <li>YCbCr
     * <li>RGB
     * <li>IPT
     * <li>Reserved
     * </ol>
     *
     * @serial u(2)
     */
    public byte signal_color_space;

    /**
     * Chroma Format Representation:
     * <ol start="0">
     * <li>4:2:0
     * <li>4:2:2
     * <li>4:4:4
     * </ol>
     *
     * @serial u(2)
     */
    public byte signal_chroma_format;

    /**
     * Signal Range.
     * <ol start="0">
     * <li>Narrow range
     * <li>Full range
     * <li>SDI range
     * <li>Reserved
     * </ol>
     *
     * @serial u(2)
     */
    public byte signal_full_range; // u(2)

    public short source_min_PQ = 62; // u(12)
    public short source_max_PQ = 3696; // u(12)

    public short source_diagonal = 42; // u(10)

    public DisplayManagementBlock[] ext_blocks;

    static Matrix3x3 asMatrix3x3(short[] coef, int scale) {
        return new Matrix3x3(
                coef[0], coef[1], coef[2],
                coef[3], coef[4], coef[5],
                coef[6], coef[7], coef[8]
        ).multiply(1.0 / scale);
    }

    public static Matrix3x3 getYCCtoRGB(short[] coef) {
        return asMatrix3x3(coef, YCCtoRGB_coef_scale);
    }

    public double[] getYCCtoRGB_offset(long[] offset) {
        double s = YCCtoRGB_offset_scale;
        return new double[] {offset[0] / s, offset[1] / s, offset[2] / s};
    }

    public static Matrix3x3 getRGBtoLMS(short[] coef) {
        return asMatrix3x3(coef, RGBtoLMS_coef_scale);
    }

    public Matrix3x3 getYCCtoRGB() {
        return getYCCtoRGB(YCCtoRGB_coef);
    }

    public double[] getYCCtoRGB_offset() {
        return getYCCtoRGB_offset(YCCtoRGB_offset);
    }

    public Matrix3x3 getRGBtoLMS() {
        return getRGBtoLMS(RGBtoLMS_coef);
    }

    @Override
    public void read(NalContext context, RbspReader in) {
        affected_dm_metadata_id = in.ue();
        current_dm_metadata_id = in.ue();
        scene_refresh_flag = in.ue();

        YCCtoRGB_coef = new short[9];
        for (int i = 0; i < YCCtoRGB_coef.length; i++) {
            YCCtoRGB_coef[i] = in.i16();
        }

        YCCtoRGB_offset = new long[3];
        for (int i = 0; i < YCCtoRGB_offset.length; i++) {
            YCCtoRGB_offset[i] = in.u32();
        }

        RGBtoLMS_coef = new short[9];
        for (int i = 0; i < RGBtoLMS_coef.length; i++) {
            RGBtoLMS_coef[i] = in.i16();
        }

        signal_eotf = in.u16();
        signal_eotf_param0 = in.u16();
        signal_eotf_param1 = in.u16();
        signal_eotf_param2 = in.u32();

        signal_bit_depth = in.u5();
        signal_color_space = in.u2();
        signal_chroma_format = in.u2();
        signal_full_range = in.u2();

        source_min_PQ = in.u12();
        source_max_PQ = in.u12();

        source_diagonal = in.u10();

        int num_ext_blocks = in.ue();

        if (num_ext_blocks > 0) {
            while (!in.isByteAligned())
                if (in.u1()) throw new IllegalStateException();

            ext_blocks = new DisplayManagementBlock[num_ext_blocks];
            for (int i = 0; i < num_ext_blocks; i++) {
                int length = in.ue();
                short level = in.u8();

                DisplayManagementBlock block = switch (level) {
                    case ContentRange.LEVEL -> new ContentRange();
                    case TrimPass.LEVEL -> new TrimPass();
                    case ContentRangeOffsets.LEVEL -> new ContentRangeOffsets();
                    case TemporallyFilteredImageLevel.LEVEL -> new TemporallyFilteredImageLevel();
                    case ActiveArea.LEVEL -> new ActiveArea();
                    case ContentLightLevel.LEVEL -> new ContentLightLevel();
                    default -> new Reserved(length, level);
                };

                if (length != block.length)
                    throw new IllegalStateException();

                block.read(context, in);

                ext_blocks[i] = block;
            }
        }
    }

    @Override
    public void write(NalContext context, RbspWriter out) {
        out.ue(affected_dm_metadata_id);
        out.ue(current_dm_metadata_id);
        out.ue(scene_refresh_flag);

        assert (YCCtoRGB_coef.length == 9);
        assert (YCCtoRGB_offset.length == 3);
        assert (RGBtoLMS_coef.length == 9);

        for (short element : YCCtoRGB_coef) {
            out.i16(element);
        }

        for (long element : YCCtoRGB_offset) {
            out.u32(element);
        }

        for (short element : RGBtoLMS_coef) {
            out.i16(element);
        }

        out.u16(signal_eotf);
        out.u16(signal_eotf_param0);
        out.u16(signal_eotf_param1);
        out.u32(signal_eotf_param2);

        out.u5(signal_bit_depth);
        out.u2(signal_color_space);
        out.u2(signal_chroma_format);
        out.u2(signal_full_range);

        out.u12(source_min_PQ);
        out.u12(source_max_PQ);

        out.u10(source_diagonal);

        out.ue(ext_blocks == null ? 0 : ext_blocks.length);
        if (ext_blocks != null && ext_blocks.length > 0) {
            while (!out.isByteAligned()) {
                out.u1(false);
            }

            for (var block : ext_blocks) {
                out.ue(block.length);
                out.u8(block.level);

                block.write(context, out);
            }
        }
    }

    @Override
    public void print(NalContext context, RbspPrinter out) {
        out.ue("affected_dm_metadata_id", affected_dm_metadata_id);
        out.ue("current_dm_metadata_id", current_dm_metadata_id);
        out.ue("scene_refresh_flag", scene_refresh_flag);

        out.raw("YCCtoRGB_coef: " + Arrays.toString(YCCtoRGB_coef));
        print(out, getYCCtoRGB());

        out.raw("YCCtoRGB_offset" + Arrays.toString(YCCtoRGB_offset));
        print(out, getYCCtoRGB_offset());
        out.raw("RGBtoLMS_coef: " + Arrays.toString(RGBtoLMS_coef));
        print(out, getRGBtoLMS());

        out.u16("signal_eotf", signal_eotf);
        out.u16("signal_eotf_param0", signal_eotf_param0);
        out.u16("signal_eotf_param1", signal_eotf_param1);
        out.u32("signal_eotf_param2", signal_eotf_param2);

        out.u5("signal_bit_depth", signal_bit_depth);
        out.u2("signal_color_space", signal_color_space);
        out.u2("signal_chroma_format", signal_chroma_format);
        out.u2("signal_full_range", signal_full_range);

        out.u12("source_min_PQ", source_min_PQ);
        out.u12("source_max_PQ", source_max_PQ);

        out.u10("source_diagonal", source_diagonal);

        if (ext_blocks != null) {
            for (var block : ext_blocks) {
                block.print(context, out);
            }
        }
    }

    public void print(RbspPrinter out, Matrix3x3 matrix) {
        for (int row = 0; row < 3; row++) {
            out.raw(format("  %.5f, %.5f, %.5f", matrix.get(row, 0),
                    matrix.get(row, 1), matrix.get(row, 2)));
        }
        out.raw(" inverse");
        matrix = matrix.invert();
        for (int row = 0; row < 3; row++) {
            out.raw(format("  %.5f, %.5f, %.5f", matrix.get(row, 0),
                    matrix.get(row, 1), matrix.get(row, 2)));
        }
    }

    public void print(RbspPrinter out, double[] vector) {
        out.raw(format("  %.5f, %.5f, %.5f", vector[0], vector[1], vector[2]));
    }
}
