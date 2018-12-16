package band.full.video.itu.h265;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

/**
 * 7.3.3 Profile, tier and level syntax
 * <p>
 * <code>profile_tier_level(profilePresentFlag, maxNumSubLayersMinus1)</code>
 *
 * @author Igor Malinin
 */
public class ProfileTierLevel implements Structure<H265Context> {
    public final boolean profilePresentFlag;
    public final byte maxNumSubLayersMinus1;

    public final LayerProfile general_profile = new LayerProfile();

    public short general_level_idc;

    public final boolean[] sub_layer_profile_present;
    public final boolean[] sub_layer_level_present;

    public static class LayerProfile implements Structure<H265Context> {
        public byte sub_layer_profile_space;
        public boolean sub_layer_tier;
        public byte sub_layer_profile_idc;
        public int sub_layer_profile_compatibility_flags; // 32 bits
        public boolean sub_layer_progressive_source;
        public boolean sub_layer_interlaced_source;
        public boolean sub_layer_non_packed_constraint;
        public boolean sub_layer_frame_only_constraint;

        // if (general_profile_idc = [4..10] ||
        // general_profile_compatibility_flag[4..10] is set) {
        public boolean sub_layer_max_12bit_constraint;
        public boolean sub_layer_max_10bit_constraint;
        public boolean sub_layer_max_8bit_constraint;
        public boolean sub_layer_max_422chroma_constraint;
        public boolean sub_layer_max_420chroma_constraint;
        public boolean sub_layer_max_monochrome_constraint;
        public boolean sub_layer_intra_constraint;
        public boolean sub_layer_one_picture_only_constraint;
        public boolean sub_layer_lower_bit_rate_constraint;
        // if (general_profile_idc = [5, 9, 10] ... ) {
        public boolean sub_layer_max_14bit_constraint;
        public long sub_layer_reserved_zero_33bits;
        // }} end ifs

        // if (general_profile_idc = [1..5, 9] ... )
        public boolean sub_layer_inbld;
        // } end if

        @Override
        public void read(H265Context context, RbspReader in) {
            sub_layer_profile_space = in.u2();
            sub_layer_tier = in.u1();
            sub_layer_profile_idc = in.u5();
            sub_layer_profile_compatibility_flags = in.i32();
            sub_layer_progressive_source = in.u1();
            sub_layer_interlaced_source = in.u1();
            sub_layer_non_packed_constraint = in.u1();
            sub_layer_frame_only_constraint = in.u1();

            sub_layer_max_12bit_constraint = in.u1();
            sub_layer_max_10bit_constraint = in.u1();
            sub_layer_max_8bit_constraint = in.u1();
            sub_layer_max_422chroma_constraint = in.u1();
            sub_layer_max_420chroma_constraint = in.u1();
            sub_layer_max_monochrome_constraint = in.u1();
            sub_layer_intra_constraint = in.u1();
            sub_layer_one_picture_only_constraint = in.u1();
            sub_layer_lower_bit_rate_constraint = in.u1();
            sub_layer_max_14bit_constraint = in.u1();
            sub_layer_reserved_zero_33bits = in.readULong(33);
            sub_layer_inbld = in.u1();
        }

        @Override
        public void write(H265Context context, RbspWriter out) {
            out.u2(sub_layer_profile_space);
            out.u1(sub_layer_tier);
            out.u5(sub_layer_profile_idc);
            out.i32(sub_layer_profile_compatibility_flags);
            out.u1(sub_layer_progressive_source);
            out.u1(sub_layer_interlaced_source);
            out.u1(sub_layer_non_packed_constraint);
            out.u1(sub_layer_frame_only_constraint);

            out.u1(sub_layer_max_12bit_constraint);
            out.u1(sub_layer_max_10bit_constraint);
            out.u1(sub_layer_max_8bit_constraint);
            out.u1(sub_layer_max_422chroma_constraint);
            out.u1(sub_layer_max_420chroma_constraint);
            out.u1(sub_layer_max_monochrome_constraint);
            out.u1(sub_layer_intra_constraint);
            out.u1(sub_layer_one_picture_only_constraint);
            out.u1(sub_layer_lower_bit_rate_constraint);
            out.u1(sub_layer_max_14bit_constraint);
            out.writeULong(33, sub_layer_reserved_zero_33bits);
            out.u1(sub_layer_inbld);
        }

        @Override
        public void print(H265Context context, RbspPrinter out) {
            out.u2("sub_layer_profile_space", sub_layer_profile_space);
            out.u1("sub_layer_tier", sub_layer_tier);
            out.u5("sub_layer_profile_idc", sub_layer_profile_idc);

            out.printB("sub_layer_profile_compatibility_flags", 32,
                    sub_layer_profile_compatibility_flags);

            out.u1("sub_layer_progressive_source",
                    sub_layer_progressive_source);
            out.u1("sub_layer_interlaced_source",
                    sub_layer_interlaced_source);
            out.u1("sub_layer_non_packed_constraint",
                    sub_layer_non_packed_constraint);
            out.u1("sub_layer_frame_only_constraint",
                    sub_layer_frame_only_constraint);

            out.u1("sub_layer_max_12bit_constraint",
                    sub_layer_max_12bit_constraint);
            out.u1("sub_layer_max_10bit_constraint",
                    sub_layer_max_10bit_constraint);
            out.u1("sub_layer_max_8bit_constraint",
                    sub_layer_max_8bit_constraint);
            out.u1("sub_layer_max_422chroma_constraint",
                    sub_layer_max_422chroma_constraint);
            out.u1("sub_layer_max_420chroma_constraint",
                    sub_layer_max_420chroma_constraint);
            out.u1("sub_layer_max_monochrome_constraint",
                    sub_layer_max_monochrome_constraint);
            out.u1("sub_layer_intra_constraint",
                    sub_layer_intra_constraint);
            out.u1("sub_layer_one_picture_only_constraint",
                    sub_layer_one_picture_only_constraint);
            out.u1("sub_layer_lower_bit_rate_constraint",
                    sub_layer_lower_bit_rate_constraint);
            out.u1("sub_layer_max_14bit_constraint",
                    sub_layer_max_14bit_constraint);
            out.printULong("sub_layer_reserved_zero_33bits", 33,
                    sub_layer_reserved_zero_33bits);
            out.u1("sub_layer_inbld", sub_layer_inbld);
        }
    }

    public final LayerProfile[] sub_layer_profile;
    public final short[] sub_layer_level_idc;

    public int reserved_zero_2bits;

    public ProfileTierLevel(boolean profilePresentFlag,
            byte maxNumSubLayersMinus1) {
        this.profilePresentFlag = profilePresentFlag;
        this.maxNumSubLayersMinus1 = maxNumSubLayersMinus1;

        sub_layer_profile_present = new boolean[maxNumSubLayersMinus1];
        sub_layer_level_present = new boolean[maxNumSubLayersMinus1];
        sub_layer_profile = new LayerProfile[maxNumSubLayersMinus1];
        sub_layer_level_idc = new short[maxNumSubLayersMinus1];
    }

    @Override
    public void read(H265Context context, RbspReader in) {
        if (profilePresentFlag) {
            general_profile.read(context, in);
        }

        general_level_idc = in.u8();

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            sub_layer_profile_present[i] = in.u1();
            sub_layer_level_present[i] = in.u1();
        }

        if (maxNumSubLayersMinus1 > 0) {
            reserved_zero_2bits = in.readUInt(
                    16 - maxNumSubLayersMinus1 * 2);
        }

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            if (sub_layer_profile_present[i]) {
                sub_layer_profile[i] = new LayerProfile();
                sub_layer_profile[i].read(context, in);
            }

            if (sub_layer_level_present[i]) {
                sub_layer_level_idc[i] = in.u8();
            }
        }
    }

    @Override
    public void write(H265Context context, RbspWriter out) {
        if (profilePresentFlag) {
            general_profile.write(context, out);
        }

        out.u8(general_level_idc);

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            out.u1(sub_layer_profile_present[i]);
            out.u1(sub_layer_level_present[i]);
        }

        if (maxNumSubLayersMinus1 > 0) {
            out.u(16 - maxNumSubLayersMinus1 * 2, reserved_zero_2bits);
        }

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            if (sub_layer_profile_present[i]) {
                sub_layer_profile[i].write(context, out);
            }

            if (sub_layer_level_present[i]) {
                out.u8(sub_layer_level_idc[i]);
            }
        }
    }

    @Override
    public void print(H265Context context, RbspPrinter out) {
        if (profilePresentFlag) {
            out.raw("general_profile");

            out.enter();
            general_profile.print(context, out);
            out.leave();
        }

        out.u8("general_level_idc", general_level_idc);

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            out.i32("sub_layer", i);
            out.enter();

            out.u1("sub_layer_profile_present", sub_layer_profile_present[i]);
            out.u1("sub_layer_level_present", sub_layer_level_present[i]);

            out.leave();
        }

        if (maxNumSubLayersMinus1 > 0) {
            out.printU("reserved_zero_2bits", 16 - maxNumSubLayersMinus1 * 2,
                    reserved_zero_2bits);
        }

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            out.i32("sub_layer", i);
            out.enter();

            if (sub_layer_profile_present[i]) {
                sub_layer_profile[i].print(context, out);
            }

            if (sub_layer_level_present[i]) {
                out.u8("sub_layer_level_idc", sub_layer_level_idc[i]);
            }

            out.leave();
        }
    }
}
