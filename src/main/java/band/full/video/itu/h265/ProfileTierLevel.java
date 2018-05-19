package band.full.video.itu.h265;

import static java.lang.Integer.toBinaryString;

import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

import java.io.PrintStream;

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
        public void read(H265Context context, RbspReader reader) {
            sub_layer_profile_space = reader.readUByte(2);
            sub_layer_tier = reader.readU1();
            sub_layer_profile_idc = reader.readUByte(5);
            sub_layer_profile_compatibility_flags = reader.readS32();
            sub_layer_progressive_source = reader.readU1();
            sub_layer_interlaced_source = reader.readU1();
            sub_layer_non_packed_constraint = reader.readU1();
            sub_layer_frame_only_constraint = reader.readU1();

            sub_layer_max_12bit_constraint = reader.readU1();
            sub_layer_max_10bit_constraint = reader.readU1();
            sub_layer_max_8bit_constraint = reader.readU1();
            sub_layer_max_422chroma_constraint = reader.readU1();
            sub_layer_max_420chroma_constraint = reader.readU1();
            sub_layer_max_monochrome_constraint = reader.readU1();
            sub_layer_intra_constraint = reader.readU1();
            sub_layer_one_picture_only_constraint = reader.readU1();
            sub_layer_lower_bit_rate_constraint = reader.readU1();
            sub_layer_max_14bit_constraint = reader.readU1();
            sub_layer_reserved_zero_33bits = reader.readULong(33);
            sub_layer_inbld = reader.readU1();
        }

        @Override
        public void write(H265Context context, RbspWriter writer) {
            writer.writeU(2, sub_layer_profile_space);
            writer.writeU1(sub_layer_tier);
            writer.writeU(5, sub_layer_profile_idc);
            writer.writeS32(sub_layer_profile_compatibility_flags);
            writer.writeU1(sub_layer_progressive_source);
            writer.writeU1(sub_layer_interlaced_source);
            writer.writeU1(sub_layer_non_packed_constraint);
            writer.writeU1(sub_layer_frame_only_constraint);

            writer.writeU1(sub_layer_max_12bit_constraint);
            writer.writeU1(sub_layer_max_10bit_constraint);
            writer.writeU1(sub_layer_max_8bit_constraint);
            writer.writeU1(sub_layer_max_422chroma_constraint);
            writer.writeU1(sub_layer_max_420chroma_constraint);
            writer.writeU1(sub_layer_max_monochrome_constraint);
            writer.writeU1(sub_layer_intra_constraint);
            writer.writeU1(sub_layer_one_picture_only_constraint);
            writer.writeU1(sub_layer_lower_bit_rate_constraint);
            writer.writeU1(sub_layer_max_14bit_constraint);
            writer.writeULong(33, sub_layer_reserved_zero_33bits);
            writer.writeU1(sub_layer_inbld);
        }

        @Override
        public void print(H265Context context, PrintStream ps) {
            ps.print("      sub_layer_profile_space: ");
            ps.println(sub_layer_profile_space);
            ps.print("      sub_layer_tier: ");
            ps.println(sub_layer_tier);
            ps.print("      sub_layer_profile_idc: ");
            ps.println(sub_layer_profile_idc);
            ps.print("      sub_layer_profile_compatibility_flags: ");
            ps.println("0b"
                    + toBinaryString(sub_layer_profile_compatibility_flags));
            ps.print("      sub_layer_progressive_source: ");
            ps.println(sub_layer_progressive_source);
            ps.print("      sub_layer_interlaced_source: ");
            ps.println(sub_layer_interlaced_source);
            ps.print("      sub_layer_non_packed_constraint: ");
            ps.println(sub_layer_non_packed_constraint);
            ps.print("      sub_layer_frame_only_constraint: ");
            ps.println(sub_layer_frame_only_constraint);

            ps.print("      sub_layer_max_12bit_constraint: ");
            ps.println(sub_layer_max_12bit_constraint);
            ps.print("      sub_layer_max_10bit_constraint: ");
            ps.println(sub_layer_max_10bit_constraint);
            ps.print("      sub_layer_max_8bit_constraint: ");
            ps.println(sub_layer_max_8bit_constraint);
            ps.print("      sub_layer_max_422chroma_constraint: ");
            ps.println(sub_layer_max_422chroma_constraint);
            ps.print("      sub_layer_max_420chroma_constraint: ");
            ps.println(sub_layer_max_420chroma_constraint);
            ps.print("      sub_layer_max_monochrome_constraint: ");
            ps.println(sub_layer_max_monochrome_constraint);
            ps.print("      sub_layer_intra_constraint: ");
            ps.println(sub_layer_intra_constraint);
            ps.print("      sub_layer_one_picture_only_constraint: ");
            ps.println(sub_layer_one_picture_only_constraint);
            ps.print("      sub_layer_lower_bit_rate_constraint: ");
            ps.println(sub_layer_lower_bit_rate_constraint);
            ps.print("      sub_layer_max_14bit_constraint: ");
            ps.println(sub_layer_max_14bit_constraint);
            ps.print("      sub_layer_reserved_zero_33bits: ");
            ps.println(sub_layer_reserved_zero_33bits);
            ps.print("      sub_layer_inbld: ");
            ps.println(sub_layer_inbld);
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
    public void read(H265Context context, RbspReader reader) {
        if (profilePresentFlag) {
            general_profile.read(context, reader);
        }

        general_level_idc = reader.readUShort(8);

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            sub_layer_profile_present[i] = reader.readU1();
            sub_layer_level_present[i] = reader.readU1();
        }

        if (maxNumSubLayersMinus1 > 0) {
            reserved_zero_2bits = reader.readUInt(
                    16 - maxNumSubLayersMinus1 * 2);
        }

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            if (sub_layer_profile_present[i]) {
                sub_layer_profile[i] = new LayerProfile();
                sub_layer_profile[i].read(context, reader);
            }

            if (sub_layer_level_present[i]) {
                sub_layer_level_idc[i] = reader.readUShort(8);
            }
        }
    }

    @Override
    public void write(H265Context context, RbspWriter writer) {
        if (profilePresentFlag) {
            general_profile.write(context, writer);
        }

        writer.writeU(8, general_level_idc);

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            writer.writeU1(sub_layer_profile_present[i]);
            writer.writeU1(sub_layer_level_present[i]);
        }

        if (maxNumSubLayersMinus1 > 0) {
            writer.writeU(16 - maxNumSubLayersMinus1 * 2, reserved_zero_2bits);
        }

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            if (sub_layer_profile_present[i]) {
                sub_layer_profile[i].write(context, writer);
            }

            if (sub_layer_level_present[i]) {
                writer.writeU(8, sub_layer_level_idc[i]);
            }
        }
    }

    @Override
    public void print(H265Context context, PrintStream ps) {
        if (profilePresentFlag) {
            ps.println("    general_profile");
            general_profile.print(context, ps);
        }

        ps.print("    general_level_idc: ");
        ps.println(general_level_idc);

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            ps.println("    sub_layer: " + i);
            ps.print("      sub_layer_profile_present: ");
            ps.println(sub_layer_profile_present[i]);
            ps.print("      sub_layer_level_present: ");
            ps.println(sub_layer_level_present[i]);
        }

        if (maxNumSubLayersMinus1 > 0) {
            ps.print("    reserved_zero_2bits: ");
            ps.println(reserved_zero_2bits);
        }

        for (int i = 0; i < maxNumSubLayersMinus1; i++) {
            ps.println("    sub_layer: " + i);
            if (sub_layer_profile_present[i]) {
                sub_layer_profile[i].print(context, ps);
            }

            if (sub_layer_level_present[i]) {
                ps.print("      sub_layer_level_idc: ");
                ps.println(sub_layer_level_idc[i]);
            }
        }
    }
}
