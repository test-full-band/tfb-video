package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.VPS_NUT;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

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
    public void read(H265Context context, RbspReader in) {
        vps_video_parameter_set_id = in.u4();
        vps_base_layer_internal = in.u1();
        vps_base_layer_available = in.u1();
        vps_max_layers_minus1 = in.u6();
        vps_max_sub_layers_minus1 = in.u3();
        vps_temporal_id_nesting = in.u1();
        vps_reserved_0xffff_16bits = in.i16();

        profile_tier_level =
                new ProfileTierLevel(true, vps_max_sub_layers_minus1);
        profile_tier_level.read(context, in);

        vps_sub_layer_ordering_info_present = in.u1();

        vps_max_dec_pic_buffering_minus1 =
                new int[vps_max_sub_layers_minus1 + 1];
        vps_max_num_reorder_pics = new int[vps_max_sub_layers_minus1 + 1];
        vps_max_latency_increase_plus1 = new int[vps_max_sub_layers_minus1 + 1];

        for (int i = vps_sub_layer_ordering_info_present ? 0
                : vps_max_sub_layers_minus1; i <= vps_max_sub_layers_minus1;
                i++) {
            vps_max_dec_pic_buffering_minus1[i] = in.ue();
            vps_max_num_reorder_pics[i] = in.ue();
            vps_max_latency_increase_plus1[i] = in.ue();
        }

        vps_max_layer_id = in.u6();

        vps_num_layer_sets_minus1 = in.ue();
        layer_id_included = new boolean[vps_num_layer_sets_minus1 + 1][];
        for (int i = 1; i <= vps_num_layer_sets_minus1; i++) {
            layer_id_included[i] = new boolean[vps_max_layer_id + 1];
            for (int j = 0; j <= vps_max_layer_id; j++) {
                layer_id_included[i][j] = in.u1();
            }
        }

        vps_timing_info_present = in.u1();
        if (vps_timing_info_present) {
            vps_num_units_in_tick = in.u32();
            vps_time_scale = in.u32();

            vps_poc_proportional_to_timing = in.u1();
            if (vps_poc_proportional_to_timing) {
                vps_num_ticks_poc_diff_one_minus1 = in.ue();
            }

            vps_num_hrd_parameters = in.ue();
            hrd_layer_set_idx = new int[vps_num_hrd_parameters];
            cprms_present = new boolean[vps_num_hrd_parameters];
            for (int i = 0; i < vps_num_hrd_parameters; i++) {
                hrd_layer_set_idx[i] = in.ue();
                if (i > 0) {
                    cprms_present[i] = in.u1();
                }

                hrd_parameters[i] = new HrdParameters(
                        cprms_present[i], vps_max_sub_layers_minus1);
                hrd_parameters[i].read(context, in);
            }
        }

        vps_extension = in.u1();
        vps_extension_data = in.readTrailingBits();
    }

    @Override
    public void write(H265Context context, RbspWriter out) {
        out.u4(vps_video_parameter_set_id);
        out.u1(vps_base_layer_internal);
        out.u1(vps_base_layer_available);
        out.u6(vps_max_layers_minus1);
        out.u3(vps_max_sub_layers_minus1);
        out.u1(vps_temporal_id_nesting);
        out.i16(vps_reserved_0xffff_16bits);

        profile_tier_level.write(context, out);

        out.u1(vps_sub_layer_ordering_info_present);

        for (int i = (vps_sub_layer_ordering_info_present ? 0
                : vps_max_sub_layers_minus1); i <= vps_max_sub_layers_minus1;
                i++) {
            out.ue(vps_max_dec_pic_buffering_minus1[i]);
            out.ue(vps_max_num_reorder_pics[i]);
            out.ue(vps_max_latency_increase_plus1[i]);
        }

        out.u6(vps_max_layer_id);

        out.ue(vps_num_layer_sets_minus1);
        for (int i = 1; i <= vps_num_layer_sets_minus1; i++) {
            for (int j = 0; j <= vps_max_layer_id; j++) {
                out.u1(layer_id_included[i][j]);
            }
        }

        out.u1(vps_timing_info_present);
        if (vps_timing_info_present) {
            out.u32(vps_num_units_in_tick);
            out.u32(vps_time_scale);

            out.u1(vps_poc_proportional_to_timing);
            if (vps_poc_proportional_to_timing) {
                out.ue(vps_num_ticks_poc_diff_one_minus1);
            }

            out.ue(vps_num_hrd_parameters);
            for (int i = 0; i < vps_num_hrd_parameters; i++) {
                out.ue(hrd_layer_set_idx[i]);
                if (i > 0) {
                    out.u1(cprms_present[i]);
                }

                hrd_parameters[i].write(context, out);
            }
        }

        out.u1(vps_extension); // u(1)
        out.writeTrailingBits(vps_extension_data);
    }

    @Override
    public void print(H265Context context, RbspPrinter out) {
        out.u4("vps_video_parameter_set_id", vps_video_parameter_set_id);
        out.u1("vps_base_layer_internal", vps_base_layer_internal);
        out.u1("vps_base_layer_available", vps_base_layer_available);
        out.u6("vps_max_layers_minus1", vps_max_layers_minus1);
        out.u3("vps_max_sub_layers_minus1", vps_max_sub_layers_minus1);
        out.u1("vps_temporal_id_nesting", vps_temporal_id_nesting);
        out.i16("vps_reserved_0xffff_16bits", vps_reserved_0xffff_16bits);

        profile_tier_level.print(context, out);

        out.u1("vps_sub_layer_ordering_info_present",
                vps_sub_layer_ordering_info_present);

        out.enter();
        for (int i = (vps_sub_layer_ordering_info_present ? 0
                : vps_max_sub_layers_minus1); i <= vps_max_sub_layers_minus1;
                i++) {
            out.ue("vps_max_dec_pic_buffering_minus1",
                    vps_max_dec_pic_buffering_minus1[i]);
            out.ue("vps_max_num_reorder_pics",
                    vps_max_num_reorder_pics[i]);
            out.ue("vps_max_latency_increase_plus1",
                    vps_max_latency_increase_plus1[i]);
        }
        out.leave();

        out.u6("vps_max_layer_id", vps_max_layer_id);

        out.ue("vps_num_layer_sets_minus1", vps_num_layer_sets_minus1);
        for (int i = 1; i <= vps_num_layer_sets_minus1; i++) {
            out.i32("vps_num_layer_set", i);
            out.enter();

            for (int j = 0; j <= vps_max_layer_id; j++) {
                out.i32("vps_num_layer_set", j);
                out.u1("layer_id_included", layer_id_included[i][j]);
            }

            out.leave();
        }

        out.u1("vps_timing_info_present", vps_timing_info_present);
        if (vps_timing_info_present) {
            out.enter();

            out.u32("vps_num_units_in_tick", vps_num_units_in_tick);
            out.u32("vps_time_scale", vps_time_scale);

            out.u1("vps_poc_proportional_to_timing",
                    vps_poc_proportional_to_timing);
            if (vps_poc_proportional_to_timing) {
                out.enter();
                out.ue("vps_num_ticks_poc_diff_one_minus1",
                        vps_num_ticks_poc_diff_one_minus1);
                out.leave();
            }

            out.ue("vps_num_hrd_parameters", vps_num_hrd_parameters);
            for (int i = 0; i < vps_num_hrd_parameters; i++) {
                out.enter();

                out.i32("vps_num_hrd_parameter", i);
                out.ue("hrd_layer_set_idx", hrd_layer_set_idx[i]);
                if (i > 0) {
                    out.u1("cprms_present", cprms_present[i]);
                }

                out.enter();
                hrd_parameters[i].print(context, out);
                out.leave();

                out.leave();
            }

            out.leave();
        }

        out.u1("vps_extension", vps_extension);
        out.printH("vps_extension_data", vps_extension_data);
    }
}
