package band.full.video.itu.h265;

import static band.full.core.ArrayMath.toHexString;
import static band.full.video.itu.h265.NALUnitType.VPS_NUT;

import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

import java.io.PrintStream;

/**
 * 7.3.2.1 Video parameter set RBSP syntax
 * <p>
 * <code>video_parameter_set_rbsp()</code>
 *
 * @author Igor Malinin
 */
public class VPS extends NALUnit {
    public byte vps_video_parameter_set_id; // u(4)
    public boolean vps_base_layer_internal; // u(1)
    public boolean vps_base_layer_available; // u(1)
    public byte vps_max_layers_minus1; // u(6)
    public byte vps_max_sub_layers_minus1; // u(3)
    public boolean vps_temporal_id_nesting; // u(1)
    public short vps_reserved_0xffff_16bits = -1; // u(16)

    public ProfileTierLevel profile_tier_level;

    public boolean vps_sub_layer_ordering_info_present;

    public int[] vps_max_dec_pic_buffering_minus1;
    public int[] vps_max_num_reorder_pics;
    public int[] vps_max_latency_increase_plus1;

    public byte vps_max_layer_id; // u(6)

    public int vps_num_layer_sets_minus1; // ue(v)
    public boolean[][] layer_id_included; // u(1)

    public boolean vps_timing_info_present; // u(1)
    public long vps_num_units_in_tick; // u(32)
    public long vps_time_scale; // u(32)
    public boolean vps_poc_proportional_to_timing; // u(1)
    public int vps_num_ticks_poc_diff_one_minus1; // ue(v)
    public int vps_num_hrd_parameters; // ue(v)

    public int[] hrd_layer_set_idx; // ue(v)
    public boolean[] cprms_present; // u(1)

    public HrdParameters[] hrd_parameters;

    public boolean vps_extension; // u(1)
    public byte[] vps_extension_data; // u(1)
    // rbsp_trailing_bits()

    public VPS() {
        super(VPS_NUT);
    }

    @Override
    public void read(H265Context context, RbspReader reader) {
        vps_video_parameter_set_id = reader.readUByte(4);
        vps_base_layer_internal = reader.readU1();
        vps_base_layer_available = reader.readU1();
        vps_max_layers_minus1 = reader.readUByte(6);
        vps_max_sub_layers_minus1 = reader.readUByte(3);
        vps_temporal_id_nesting = reader.readU1();
        vps_reserved_0xffff_16bits = reader.readS16();

        profile_tier_level = new ProfileTierLevel(
                true, vps_max_sub_layers_minus1);
        profile_tier_level.read(context, reader);

        vps_sub_layer_ordering_info_present = reader.readU1();

        vps_max_dec_pic_buffering_minus1 =
                new int[vps_max_sub_layers_minus1 + 1];
        vps_max_num_reorder_pics = new int[vps_max_sub_layers_minus1 + 1];
        vps_max_latency_increase_plus1 = new int[vps_max_sub_layers_minus1 + 1];

        for (int i = vps_sub_layer_ordering_info_present ? 0
                : vps_max_sub_layers_minus1; i <= vps_max_sub_layers_minus1;
                i++) {
            vps_max_dec_pic_buffering_minus1[i] = reader.readUE();
            vps_max_num_reorder_pics[i] = reader.readUE();
            vps_max_latency_increase_plus1[i] = reader.readUE();
        }

        vps_max_layer_id = reader.readUByte(6);

        vps_num_layer_sets_minus1 = reader.readUE();
        layer_id_included = new boolean[vps_num_layer_sets_minus1 + 1][];
        for (int i = 1; i <= vps_num_layer_sets_minus1; i++) {
            layer_id_included[i] = new boolean[vps_max_layer_id + 1];
            for (int j = 0; j <= vps_max_layer_id; j++) {
                layer_id_included[i][j] = reader.readU1();
            }
        }

        vps_timing_info_present = reader.readU1();
        if (vps_timing_info_present) {
            vps_num_units_in_tick = reader.readULong(32);
            vps_time_scale = reader.readULong(32);

            vps_poc_proportional_to_timing = reader.readU1();
            if (vps_poc_proportional_to_timing) {
                vps_num_ticks_poc_diff_one_minus1 = reader.readUE();
            }

            vps_num_hrd_parameters = reader.readUE();
            hrd_layer_set_idx = new int[vps_num_hrd_parameters];
            cprms_present = new boolean[vps_num_hrd_parameters];
            for (int i = 0; i < vps_num_hrd_parameters; i++) {
                hrd_layer_set_idx[i] = reader.readUE();
                if (i > 0) {
                    cprms_present[i] = reader.readU1();
                }

                hrd_parameters[i] = new HrdParameters(
                        cprms_present[i], vps_max_sub_layers_minus1);
                hrd_parameters[i].read(context, reader);
            }
        }

        vps_extension = reader.readU1();
        vps_extension_data = reader.readTrailingBits();
    }

    @Override
    public void write(H265Context context, RbspWriter writer) {
        writer.writeU(4, vps_video_parameter_set_id);
        writer.writeU1(vps_base_layer_internal);
        writer.writeU1(vps_base_layer_available);
        writer.writeU(6, vps_max_layers_minus1);
        writer.writeU(3, vps_max_sub_layers_minus1);
        writer.writeU1(vps_temporal_id_nesting);
        writer.writeS16(vps_reserved_0xffff_16bits);

        profile_tier_level.write(context, writer);

        writer.writeU1(vps_sub_layer_ordering_info_present);

        for (int i = (vps_sub_layer_ordering_info_present ? 0
                : vps_max_sub_layers_minus1); i <= vps_max_sub_layers_minus1;
                i++) {
            writer.writeUE(vps_max_dec_pic_buffering_minus1[i]);
            writer.writeUE(vps_max_num_reorder_pics[i]);
            writer.writeUE(vps_max_latency_increase_plus1[i]);
        }

        writer.writeU(6, vps_max_layer_id);

        writer.writeUE(vps_num_layer_sets_minus1);
        for (int i = 1; i <= vps_num_layer_sets_minus1; i++) {
            for (int j = 0; j <= vps_max_layer_id; j++) {
                writer.writeU1(layer_id_included[i][j]);
            }
        }

        writer.writeU1(vps_timing_info_present);
        if (vps_timing_info_present) {
            writer.writeULong(32, vps_num_units_in_tick);
            writer.writeULong(32, vps_time_scale);

            writer.writeU1(vps_poc_proportional_to_timing);
            if (vps_poc_proportional_to_timing) {
                writer.writeUE(vps_num_ticks_poc_diff_one_minus1);
            }

            writer.writeUE(vps_num_hrd_parameters);
            for (int i = 0; i < vps_num_hrd_parameters; i++) {
                writer.writeUE(hrd_layer_set_idx[i]);
                if (i > 0) {
                    writer.writeU1(cprms_present[i]);
                }

                hrd_parameters[i].write(context, writer);
            }
        }

        writer.writeU1(vps_extension); // u(1)
        writer.writeTrailingBits(vps_extension_data);
    }

    @Override
    public void print(H265Context context, PrintStream ps) {
        ps.print("    vps_video_parameter_set_id: ");
        ps.println(vps_video_parameter_set_id);
        ps.print("    vps_base_layer_internal: ");
        ps.println(vps_base_layer_internal);
        ps.print("    vps_base_layer_available: ");
        ps.println(vps_base_layer_available);
        ps.print("    vps_max_layers_minus1: ");
        ps.println(vps_max_layers_minus1);
        ps.print("    vps_max_sub_layers_minus1: ");
        ps.println(vps_max_sub_layers_minus1);
        ps.print("    vps_temporal_id_nesting: ");
        ps.println(vps_temporal_id_nesting);
        ps.print("    vps_reserved_0xffff_16bits: ");
        ps.println("0x" + toHexString(vps_reserved_0xffff_16bits & 0xFFFF));

        profile_tier_level.print(context, ps);

        ps.print("    vps_sub_layer_ordering_info_present: ");
        ps.println(vps_sub_layer_ordering_info_present);

        for (int i = (vps_sub_layer_ordering_info_present ? 0
                : vps_max_sub_layers_minus1); i <= vps_max_sub_layers_minus1;
                i++) {
            ps.print("      vps_max_dec_pic_buffering_minus1: ");
            ps.println(vps_max_dec_pic_buffering_minus1[i]);
            ps.print("      vps_max_num_reorder_pics: ");
            ps.println(vps_max_num_reorder_pics[i]);
            ps.print("      vps_max_latency_increase_plus1: ");
            ps.println(vps_max_latency_increase_plus1[i]);
        }

        ps.print("    vps_max_layer_id: ");
        ps.println(vps_max_layer_id);

        ps.print("    vps_num_layer_sets_minus1: ");
        ps.println(vps_num_layer_sets_minus1);
        for (int i = 1; i <= vps_num_layer_sets_minus1; i++) {
            ps.print("    vps_num_layer_set: " + i);
            for (int j = 0; j <= vps_max_layer_id; j++) {
                ps.print("      vps_num_layer_set: " + j);
                ps.print("      layer_id_included: ");
                ps.println(layer_id_included[i][j]);
            }
        }

        ps.print("    vps_timing_info_present: ");
        ps.println(vps_timing_info_present);
        if (vps_timing_info_present) {
            ps.print("      vps_num_units_in_tick: ");
            ps.println(vps_num_units_in_tick);
            ps.print("      vps_time_scale: ");
            ps.println(vps_time_scale);

            ps.print("      vps_poc_proportional_to_timing: ");
            ps.println(vps_poc_proportional_to_timing);
            if (vps_poc_proportional_to_timing) {
                ps.print("        vps_num_ticks_poc_diff_one_minus1: ");
                ps.println(vps_num_ticks_poc_diff_one_minus1);
            }

            ps.print("      vps_num_hrd_parameters: ");
            ps.println(vps_num_hrd_parameters);
            for (int i = 0; i < vps_num_hrd_parameters; i++) {
                ps.print("        vps_num_hrd_parameter: " + i);
                ps.print("        hrd_layer_set_idx: ");
                ps.println(hrd_layer_set_idx[i]);
                if (i > 0) {
                    ps.print("          cprms_present: ");
                    ps.println(cprms_present[i]);
                }

                hrd_parameters[i].print(context, ps);
            }
        }

        ps.print("    vps_extension: ");
        ps.println(vps_extension);
        ps.print("    vps_extension_data: 0x");
        ps.println(toHexString(vps_extension_data));
    }
}
