package band.full.video.itu.h265;

import static band.full.core.ArrayMath.toHexString;
import static band.full.video.itu.h265.NALUnitType.SPS_NUT;

import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

import java.io.PrintStream;

/**
 * 7.3.2.2 Sequence parameter set RBSP syntax<br>
 * 7.3.2.2.1 General sequence parameter set RBSP syntax
 * <p>
 * <code>seq_parameter_set_rbsp()</code>
 *
 * @author Igor Malinin
 */
public class SPS extends NALUnit {
    public byte sps_video_parameter_set_id; // u4
    public byte sps_max_sub_layers_minus1; // u3
    public boolean sps_temporal_id_nesting_flag; // u1
    public ProfileTierLevel profile_tier_level;
    public int sps_seq_parameter_set_id; // ue(v)

    public int chroma_format_idc; // ue(v)
    // if( chroma_format_idc == 3 )
    public boolean separate_colour_plane_flag; // u(1)

    public int pic_width_in_luma_samples; // ue(v)
    public int pic_height_in_luma_samples; // ue(v)

    public boolean conformance_window_flag; // u(1)
    // if( conformance_window_flag ) {
    public int conf_win_left_offset; // ue(v)
    public int conf_win_right_offset; // ue(v)
    public int conf_win_top_offset; // ue(v)
    public int conf_win_bottom_offset; // ue(v)
    // }

    public int bit_depth_luma_minus8; // ue(v)
    public int bit_depth_chroma_minus8; // ue(v)
    public int log2_max_pic_order_cnt_lsb_minus4; // ue(v); 0..12
    public boolean sps_sub_layer_ordering_info_present_flag; // u(1)

    // for( i = ( sps_sub_layer_ordering_info_present_flag ? 0 :
    // sps_max_sub_layers_minus1 ); i <= sps_max_sub_layers_minus1; i++ ) {
    public int[] sps_max_dec_pic_buffering_minus1; // ue(v)
    public int[] sps_max_num_reorder_pics; // ue(v)
    public int[] sps_max_latency_increase_plus1; // ue(v)
    // }

    public int log2_min_luma_coding_block_size_minus3; // ue(v)
    public int log2_diff_max_min_luma_coding_block_size; // ue(v)
    public int log2_min_luma_transform_block_size_minus2; // ue(v)
    public int log2_diff_max_min_luma_transform_block_size; // ue(v)
    public int max_transform_hierarchy_depth_inter; // ue(v)
    public int max_transform_hierarchy_depth_intra; // ue(v)

    public boolean scaling_list_enabled_flag; // u(1)
    // if( scaling_list_enabled_flag ) {
    public boolean sps_scaling_list_data_present_flag; // u(1)
    // if( sps_scaling_list_data_present_flag )
    public ScalingListData scaling_list_data;
    // }

    public boolean amp_enabled_flag; // u(1)
    public boolean sample_adaptive_offset_enabled_flag; // u(1)

    public boolean pcm_enabled_flag; // u(1)
    // if( pcm_enabled_flag ) {
    public byte pcm_sample_bit_depth_luma_minus1; // u(4)
    public byte pcm_sample_bit_depth_chroma_minus1; // u(4)
    public int log2_min_pcm_luma_coding_block_size_minus3; // ue(v)
    public int log2_diff_max_min_pcm_luma_coding_block_size; // ue(v)
    public boolean pcm_loop_filter_disabled_flag; // u(1)
    // }

    public byte num_short_term_ref_pic_sets; // ue(v) 0..64
    // for( i = 0; i < num_short_term_ref_pic_sets; i++)
    // public StRefPicSet[] st_ref_pic_set; // in H265Context

    public boolean long_term_ref_pics_present_flag; // u(1)
    // if( long_term_ref_pics_present_flag ) {
    public int num_long_term_ref_pics_sps; // ue(v)
    // for( i = 0; i < num_long_term_ref_pics_sps; i++ ) {
    public int[] lt_ref_pic_poc_lsb_sps; // u(v)
    public boolean[] used_by_curr_pic_lt_sps_flag; // u(1)
    // }
    // }

    public boolean sps_temporal_mvp_enabled_flag; // u(1)
    public boolean strong_intra_smoothing_enabled_flag; // u(1)

    public boolean vui_parameters_present_flag; // u(1)
    // if(vui_parameters_present_flag)
    public VuiParameters vui_parameters;

    public boolean sps_extension_present_flag; // u(1)
    // if(sps_extension_present_flag) {
    public boolean sps_range_extension_flag; // u(1)
    public boolean sps_multilayer_extension_flag; // u(1)
    public boolean sps_3d_extension_flag; // u(1)
    public boolean sps_scc_extension_flag; // u(1)
    public byte sps_extension_4bits; // u(4)
    // }

    // if(sps_range_extension_flag)
    // sps_range_extension()

    // if(sps_multilayer_extension_flag)
    // sps_multilayer_extension()
    // /* specified in Annex F */

    // if(sps_3d_extension_flag)
    // sps_3d_extension() /* specified in Annex I */

    // if(sps_scc_extension_flag)
    // sps_scc_extension()

    // if(sps_extension_4bits)
    // while(more_rbsp_data())

    // public boolean sps_extension_data_flag; // u(1)

    public byte[] trailing_bits;

    public SPS() {
        super(SPS_NUT);
    }

    @Override
    public void read(H265Context context, RbspReader reader) {
        context.sps = this;

        sps_video_parameter_set_id = reader.readUByte(4);
        sps_max_sub_layers_minus1 = reader.readUByte(3);
        sps_temporal_id_nesting_flag = reader.readU1();

        profile_tier_level = new ProfileTierLevel(true,
                sps_max_sub_layers_minus1);
        profile_tier_level.read(context, reader);

        sps_seq_parameter_set_id = reader.readUE();
        chroma_format_idc = reader.readUE();

        if (chroma_format_idc == 3) {
            separate_colour_plane_flag = reader.readU1();
        }

        pic_width_in_luma_samples = reader.readUE();
        pic_height_in_luma_samples = reader.readUE();

        conformance_window_flag = reader.readU1();
        if (conformance_window_flag) {
            conf_win_left_offset = reader.readUE();
            conf_win_right_offset = reader.readUE();
            conf_win_top_offset = reader.readUE();
            conf_win_bottom_offset = reader.readUE();
        }

        bit_depth_luma_minus8 = reader.readUE();
        bit_depth_chroma_minus8 = reader.readUE();
        log2_max_pic_order_cnt_lsb_minus4 = reader.readUE(); // 0..12
        sps_sub_layer_ordering_info_present_flag = reader.readU1();

        int sps_max_sub_layers = sps_max_sub_layers_minus1 + 1;
        sps_max_dec_pic_buffering_minus1 = new int[sps_max_sub_layers];
        sps_max_num_reorder_pics = new int[sps_max_sub_layers];
        sps_max_latency_increase_plus1 = new int[sps_max_sub_layers];

        for (int i = (sps_sub_layer_ordering_info_present_flag ? 0
                : sps_max_sub_layers_minus1); i < sps_max_sub_layers;
                i++) {
            sps_max_dec_pic_buffering_minus1[i] = reader.readUE();
            sps_max_num_reorder_pics[i] = reader.readUE();
            sps_max_latency_increase_plus1[i] = reader.readUE();
        }

        log2_min_luma_coding_block_size_minus3 = reader.readUE();
        log2_diff_max_min_luma_coding_block_size = reader.readUE();
        log2_min_luma_transform_block_size_minus2 = reader.readUE();
        log2_diff_max_min_luma_transform_block_size = reader.readUE();
        max_transform_hierarchy_depth_inter = reader.readUE();
        max_transform_hierarchy_depth_intra = reader.readUE();

        scaling_list_enabled_flag = reader.readU1();
        if (scaling_list_enabled_flag) {
            sps_scaling_list_data_present_flag = reader.readU1();
            if (sps_scaling_list_data_present_flag) {
                scaling_list_data = new ScalingListData();
                scaling_list_data.read(context, reader);
            }
        }

        amp_enabled_flag = reader.readU1();
        sample_adaptive_offset_enabled_flag = reader.readU1();

        pcm_enabled_flag = reader.readU1();
        if (pcm_enabled_flag) {
            pcm_sample_bit_depth_luma_minus1 = reader.readUByte(4);
            pcm_sample_bit_depth_chroma_minus1 = reader.readUByte(4);
            log2_min_pcm_luma_coding_block_size_minus3 = reader.readUE();
            log2_diff_max_min_pcm_luma_coding_block_size = reader.readUE();
            pcm_loop_filter_disabled_flag = reader.readU1();
        }

        num_short_term_ref_pic_sets = (byte) reader.readUE(); // 0..64
        context.st_ref_pic_set =
                new StRefPicSet[num_short_term_ref_pic_sets + 1];
        for (byte i = 0; i < num_short_term_ref_pic_sets; i++) {
            context.st_ref_pic_set[i] =
                    new StRefPicSet(i, num_short_term_ref_pic_sets);
            context.st_ref_pic_set[i].read(context, reader);
        }

        long_term_ref_pics_present_flag = reader.readU1();
        if (long_term_ref_pics_present_flag) {
            num_long_term_ref_pics_sps = reader.readUE();
            lt_ref_pic_poc_lsb_sps = new int[num_long_term_ref_pics_sps];
            used_by_curr_pic_lt_sps_flag =
                    new boolean[num_long_term_ref_pics_sps];
            int log2_max_pic_order_cnt_lsb =
                    log2_max_pic_order_cnt_lsb_minus4 + 4;
            for (int i = 0; i < num_long_term_ref_pics_sps; i++) {
                lt_ref_pic_poc_lsb_sps[i] =
                        reader.readUInt(log2_max_pic_order_cnt_lsb);
                used_by_curr_pic_lt_sps_flag[i] = reader.readU1();
            }
        }

        sps_temporal_mvp_enabled_flag = reader.readU1();
        strong_intra_smoothing_enabled_flag = reader.readU1();

        vui_parameters_present_flag = reader.readU1();
        if (vui_parameters_present_flag) {
            vui_parameters = new VuiParameters(sps_max_sub_layers_minus1);
            vui_parameters.read(context, reader);
        }

        sps_extension_present_flag = reader.readU1();
        if (sps_extension_present_flag) {
            sps_range_extension_flag = reader.readU1();
            sps_multilayer_extension_flag = reader.readU1();
            sps_3d_extension_flag = reader.readU1();
            sps_scc_extension_flag = reader.readU1();
            sps_extension_4bits = reader.readUByte(4);
        }

        trailing_bits = reader.readTrailingBits();
    }

    @Override
    public void write(H265Context context, RbspWriter writer) {
        writer.writeU(4, sps_video_parameter_set_id);
        writer.writeU(3, sps_max_sub_layers_minus1);
        writer.writeU1(sps_temporal_id_nesting_flag);

        profile_tier_level.write(context, writer);

        writer.writeUE(sps_seq_parameter_set_id);
        writer.writeUE(chroma_format_idc);

        if (chroma_format_idc == 3) {
            writer.writeU1(separate_colour_plane_flag);
        }

        writer.writeUE(pic_width_in_luma_samples);
        writer.writeUE(pic_height_in_luma_samples);

        writer.writeU1(conformance_window_flag);
        if (conformance_window_flag) {
            writer.writeUE(conf_win_left_offset);
            writer.writeUE(conf_win_right_offset);
            writer.writeUE(conf_win_top_offset);
            writer.writeUE(conf_win_bottom_offset);
        }

        writer.writeUE(bit_depth_luma_minus8);
        writer.writeUE(bit_depth_chroma_minus8);
        writer.writeUE(log2_max_pic_order_cnt_lsb_minus4); // 0..12
        writer.writeU1(sps_sub_layer_ordering_info_present_flag);

        int sps_max_sub_layers = sps_max_sub_layers_minus1 + 1;
        for (int i = (sps_sub_layer_ordering_info_present_flag ? 0
                : sps_max_sub_layers_minus1); i < sps_max_sub_layers;
                i++) {
            writer.writeUE(sps_max_dec_pic_buffering_minus1[i]);
            writer.writeUE(sps_max_num_reorder_pics[i]);
            writer.writeUE(sps_max_latency_increase_plus1[i]);
        }

        writer.writeUE(log2_min_luma_coding_block_size_minus3);
        writer.writeUE(log2_diff_max_min_luma_coding_block_size);
        writer.writeUE(log2_min_luma_transform_block_size_minus2);
        writer.writeUE(log2_diff_max_min_luma_transform_block_size);
        writer.writeUE(max_transform_hierarchy_depth_inter);
        writer.writeUE(max_transform_hierarchy_depth_intra);

        writer.writeU1(scaling_list_enabled_flag);
        if (scaling_list_enabled_flag) {
            writer.writeU1(sps_scaling_list_data_present_flag);
            if (sps_scaling_list_data_present_flag) {
                scaling_list_data.write(context, writer);
            }
        }

        writer.writeU1(amp_enabled_flag);
        writer.writeU1(sample_adaptive_offset_enabled_flag);

        writer.writeU1(pcm_enabled_flag);
        if (pcm_enabled_flag) {
            writer.writeU(4, pcm_sample_bit_depth_luma_minus1);
            writer.writeU(4, pcm_sample_bit_depth_chroma_minus1);
            writer.writeUE(log2_min_pcm_luma_coding_block_size_minus3);
            writer.writeUE(log2_diff_max_min_pcm_luma_coding_block_size);
            writer.writeU1(pcm_loop_filter_disabled_flag);
        }

        writer.writeUE(num_short_term_ref_pic_sets); // 0..64
        for (int i = 0; i < num_short_term_ref_pic_sets; i++) {
            context.st_ref_pic_set[i].write(context, writer);
        }

        writer.writeU1(long_term_ref_pics_present_flag);
        if (long_term_ref_pics_present_flag) {
            writer.writeUE(num_long_term_ref_pics_sps);
            int log2_max_pic_order_cnt_lsb =
                    log2_max_pic_order_cnt_lsb_minus4 + 4;
            for (int i = 0; i < num_long_term_ref_pics_sps; i++) {
                writer.writeU(log2_max_pic_order_cnt_lsb,
                        lt_ref_pic_poc_lsb_sps[i]);
                writer.writeU1(used_by_curr_pic_lt_sps_flag[i]);
            }
        }

        writer.writeU1(sps_temporal_mvp_enabled_flag);
        writer.writeU1(strong_intra_smoothing_enabled_flag);

        writer.writeU1(vui_parameters_present_flag);
        if (vui_parameters_present_flag) {
            vui_parameters.write(context, writer);
        }

        writer.writeU1(sps_extension_present_flag);
        if (sps_extension_present_flag) {
            writer.writeU1(sps_range_extension_flag);
            writer.writeU1(sps_multilayer_extension_flag);
            writer.writeU1(sps_3d_extension_flag);
            writer.writeU1(sps_scc_extension_flag);
            writer.writeU(4, sps_extension_4bits);
        }

        writer.writeTrailingBits(trailing_bits);
    }

    @Override
    public void print(H265Context context, PrintStream ps) {
        ps.print("    sps_video_parameter_set_id: ");
        ps.println(sps_video_parameter_set_id);
        ps.print("    sps_max_sub_layers_minus1: ");
        ps.println(sps_max_sub_layers_minus1);
        ps.print("    sps_temporal_id_nesting_flag: ");
        ps.println(sps_temporal_id_nesting_flag);

        profile_tier_level.print(context, ps);

        ps.print("    sps_seq_parameter_set_id: ");
        ps.println(sps_seq_parameter_set_id);
        ps.print("    chroma_format_idc: ");
        ps.println(chroma_format_idc);

        if (chroma_format_idc == 3) {
            ps.print("      separate_colour_plane_flag: ");
            ps.println(separate_colour_plane_flag);
        }

        ps.print("    pic_width_in_luma_samples: ");
        ps.println(pic_width_in_luma_samples);
        ps.print("    pic_height_in_luma_samples: ");
        ps.println(pic_height_in_luma_samples);

        ps.print("    conformance_window_flag: ");
        ps.println(conformance_window_flag);
        if (conformance_window_flag) {
            ps.print("      conf_win_left_offset: ");
            ps.println(conf_win_left_offset);
            ps.print("      conf_win_right_offset: ");
            ps.println(conf_win_right_offset);
            ps.print("      conf_win_top_offset: ");
            ps.println(conf_win_top_offset);
            ps.print("      conf_win_bottom_offset: ");
            ps.println(conf_win_bottom_offset);
        }

        ps.print("    bit_depth_luma_minus8: ");
        ps.println(bit_depth_luma_minus8);
        ps.print("    bit_depth_chroma_minus8: ");
        ps.println(bit_depth_chroma_minus8);
        ps.print("    log2_max_pic_order_cnt_lsb_minus4: "); // 0..12
        ps.println(log2_max_pic_order_cnt_lsb_minus4); // 0..12
        ps.print("    sps_sub_layer_ordering_info_present_flag: ");
        ps.println(sps_sub_layer_ordering_info_present_flag);

        int sps_max_sub_layers = sps_max_sub_layers_minus1 + 1;
        for (int i = (sps_sub_layer_ordering_info_present_flag ? 0
                : sps_max_sub_layers_minus1); i < sps_max_sub_layers;
                i++) {
            ps.print("    sub_layer_");
            ps.println(i);
            ps.print("      sps_max_dec_pic_buffering_minus1: ");
            ps.println(sps_max_dec_pic_buffering_minus1[i]);
            ps.print("      sps_max_num_reorder_pics: ");
            ps.println(sps_max_num_reorder_pics[i]);
            ps.print("      sps_max_latency_increase_plus1: ");
            ps.println(sps_max_latency_increase_plus1[i]);
        }

        ps.print("    log2_min_luma_coding_block_size_minus3: ");
        ps.println(log2_min_luma_coding_block_size_minus3);
        ps.print("    log2_diff_max_min_luma_coding_block_size: ");
        ps.println(log2_diff_max_min_luma_coding_block_size);
        ps.print("    log2_min_luma_transform_block_size_minus2: ");
        ps.println(log2_min_luma_transform_block_size_minus2);
        ps.print("    log2_diff_max_min_luma_transform_block_size: ");
        ps.println(log2_diff_max_min_luma_transform_block_size);
        ps.print("    max_transform_hierarchy_depth_inter: ");
        ps.println(max_transform_hierarchy_depth_inter);
        ps.print("    max_transform_hierarchy_depth_intra: ");
        ps.println(max_transform_hierarchy_depth_intra);

        ps.print("    scaling_list_enabled_flag: ");
        ps.println(scaling_list_enabled_flag);
        if (scaling_list_enabled_flag) {
            ps.print("      sps_scaling_list_data_present_flag: ");
            ps.println(sps_scaling_list_data_present_flag);
            if (sps_scaling_list_data_present_flag) {
                scaling_list_data.print(context, ps);
            }
        }

        ps.print("    amp_enabled_flag: ");
        ps.println(amp_enabled_flag);
        ps.print("    sample_adaptive_offset_enabled_flag: ");
        ps.println(sample_adaptive_offset_enabled_flag);

        ps.print("    pcm_enabled_flag: ");
        ps.println(pcm_enabled_flag);
        if (pcm_enabled_flag) {
            ps.print("      pcm_sample_bit_depth_luma_minus1: ");
            ps.println(pcm_sample_bit_depth_luma_minus1);
            ps.print("      pcm_sample_bit_depth_chroma_minus1: ");
            ps.println(pcm_sample_bit_depth_chroma_minus1);
            ps.print("      log2_min_pcm_luma_coding_block_size_minus3: ");
            ps.println(log2_min_pcm_luma_coding_block_size_minus3);
            ps.print("      log2_diff_max_min_pcm_luma_coding_block_size: ");
            ps.println(log2_diff_max_min_pcm_luma_coding_block_size);
            ps.print("      pcm_loop_filter_disabled_flag: ");
            ps.println(pcm_loop_filter_disabled_flag);
        }

        ps.print("    num_short_term_ref_pic_sets: "); // 0..64
        ps.println(num_short_term_ref_pic_sets); // 0..64
        for (int i = 0; i < num_short_term_ref_pic_sets; i++) {
            context.st_ref_pic_set[i].print(context, ps);
        }

        ps.print("    long_term_ref_pics_present_flag: ");
        ps.println(long_term_ref_pics_present_flag);
        if (long_term_ref_pics_present_flag) {
            ps.print("      num_long_term_ref_pics_sps: ");
            ps.println(num_long_term_ref_pics_sps);
            for (int i = 0; i < num_long_term_ref_pics_sps; i++) {
                ps.print("        lt_ref_pic_poc_lsb_sps: ");
                ps.println(lt_ref_pic_poc_lsb_sps[i]);
                ps.print("        used_by_curr_pic_lt_sps_flag: ");
                ps.println(used_by_curr_pic_lt_sps_flag[i]);
            }
        }

        ps.print("    sps_temporal_mvp_enabled_flag: ");
        ps.println(sps_temporal_mvp_enabled_flag);
        ps.print("    strong_intra_smoothing_enabled_flag: ");
        ps.println(strong_intra_smoothing_enabled_flag);

        ps.print("    vui_parameters_present_flag: ");
        ps.println(vui_parameters_present_flag);
        if (vui_parameters_present_flag) {
            vui_parameters.print(context, ps);
        }

        ps.print("    sps_extension_present_flag: ");
        ps.println(sps_extension_present_flag);
        if (sps_extension_present_flag) {
            ps.print("      sps_range_extension_flag: ");
            ps.println(sps_range_extension_flag);
            ps.print("      sps_multilayer_extension_flag: ");
            ps.println(sps_multilayer_extension_flag);
            ps.print("      sps_3d_extension_flag: ");
            ps.println(sps_3d_extension_flag);
            ps.print("      sps_scc_extension_flag: ");
            ps.println(sps_scc_extension_flag);
            ps.print("      sps_extension_4bits: ");
            ps.println(sps_extension_4bits);
        }

        ps.print("    trailing_bits: 0x");
        ps.println(toHexString(trailing_bits));
    }
}
