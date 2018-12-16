package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.SPS_NUT;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

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
    public void read(H265Context context, RbspReader in) {
        context.sps = this;

        sps_video_parameter_set_id = in.u4();
        sps_max_sub_layers_minus1 = in.u3();
        sps_temporal_id_nesting_flag = in.u1();

        profile_tier_level =
                new ProfileTierLevel(true, sps_max_sub_layers_minus1);

        profile_tier_level.read(context, in);

        sps_seq_parameter_set_id = in.ue();
        chroma_format_idc = in.ue();

        if (chroma_format_idc == 3) {
            separate_colour_plane_flag = in.u1();
        }

        pic_width_in_luma_samples = in.ue();
        pic_height_in_luma_samples = in.ue();

        conformance_window_flag = in.u1();
        if (conformance_window_flag) {
            conf_win_left_offset = in.ue();
            conf_win_right_offset = in.ue();
            conf_win_top_offset = in.ue();
            conf_win_bottom_offset = in.ue();
        }

        bit_depth_luma_minus8 = in.ue();
        bit_depth_chroma_minus8 = in.ue();
        log2_max_pic_order_cnt_lsb_minus4 = in.ue(); // 0..12
        sps_sub_layer_ordering_info_present_flag = in.u1();

        int sps_max_sub_layers = sps_max_sub_layers_minus1 + 1;
        sps_max_dec_pic_buffering_minus1 = new int[sps_max_sub_layers];
        sps_max_num_reorder_pics = new int[sps_max_sub_layers];
        sps_max_latency_increase_plus1 = new int[sps_max_sub_layers];

        for (int i = (sps_sub_layer_ordering_info_present_flag ? 0
                : sps_max_sub_layers_minus1); i < sps_max_sub_layers;
                i++) {
            sps_max_dec_pic_buffering_minus1[i] = in.ue();
            sps_max_num_reorder_pics[i] = in.ue();
            sps_max_latency_increase_plus1[i] = in.ue();
        }

        log2_min_luma_coding_block_size_minus3 = in.ue();
        log2_diff_max_min_luma_coding_block_size = in.ue();
        log2_min_luma_transform_block_size_minus2 = in.ue();
        log2_diff_max_min_luma_transform_block_size = in.ue();
        max_transform_hierarchy_depth_inter = in.ue();
        max_transform_hierarchy_depth_intra = in.ue();

        scaling_list_enabled_flag = in.u1();
        if (scaling_list_enabled_flag) {
            sps_scaling_list_data_present_flag = in.u1();
            if (sps_scaling_list_data_present_flag) {
                scaling_list_data = new ScalingListData();
                scaling_list_data.read(context, in);
            }
        }

        amp_enabled_flag = in.u1();
        sample_adaptive_offset_enabled_flag = in.u1();

        pcm_enabled_flag = in.u1();
        if (pcm_enabled_flag) {
            pcm_sample_bit_depth_luma_minus1 = in.u4();
            pcm_sample_bit_depth_chroma_minus1 = in.u4();
            log2_min_pcm_luma_coding_block_size_minus3 = in.ue();
            log2_diff_max_min_pcm_luma_coding_block_size = in.ue();
            pcm_loop_filter_disabled_flag = in.u1();
        }

        num_short_term_ref_pic_sets = (byte) in.ue(); // 0..64
        context.st_ref_pic_set =
                new StRefPicSet[num_short_term_ref_pic_sets + 1];
        for (byte i = 0; i < num_short_term_ref_pic_sets; i++) {
            context.st_ref_pic_set[i] =
                    new StRefPicSet(i, num_short_term_ref_pic_sets);
            context.st_ref_pic_set[i].read(context, in);
        }

        long_term_ref_pics_present_flag = in.u1();
        if (long_term_ref_pics_present_flag) {
            num_long_term_ref_pics_sps = in.ue();
            lt_ref_pic_poc_lsb_sps = new int[num_long_term_ref_pics_sps];
            used_by_curr_pic_lt_sps_flag =
                    new boolean[num_long_term_ref_pics_sps];
            int log2_max_pic_order_cnt_lsb =
                    log2_max_pic_order_cnt_lsb_minus4 + 4;
            for (int i = 0; i < num_long_term_ref_pics_sps; i++) {
                lt_ref_pic_poc_lsb_sps[i] =
                        in.readUInt(log2_max_pic_order_cnt_lsb);
                used_by_curr_pic_lt_sps_flag[i] = in.u1();
            }
        }

        sps_temporal_mvp_enabled_flag = in.u1();
        strong_intra_smoothing_enabled_flag = in.u1();

        vui_parameters_present_flag = in.u1();
        if (vui_parameters_present_flag) {
            vui_parameters = new VuiParameters(sps_max_sub_layers_minus1);
            vui_parameters.read(context, in);
        }

        sps_extension_present_flag = in.u1();
        if (sps_extension_present_flag) {
            sps_range_extension_flag = in.u1();
            sps_multilayer_extension_flag = in.u1();
            sps_3d_extension_flag = in.u1();
            sps_scc_extension_flag = in.u1();
            sps_extension_4bits = in.u4();
        }

        trailing_bits = in.readTrailingBits();
    }

    @Override
    public void write(H265Context context, RbspWriter out) {
        out.u4(sps_video_parameter_set_id);
        out.u3(sps_max_sub_layers_minus1);
        out.u1(sps_temporal_id_nesting_flag);

        profile_tier_level.write(context, out);

        out.ue(sps_seq_parameter_set_id);
        out.ue(chroma_format_idc);

        if (chroma_format_idc == 3) {
            out.u1(separate_colour_plane_flag);
        }

        out.ue(pic_width_in_luma_samples);
        out.ue(pic_height_in_luma_samples);

        out.u1(conformance_window_flag);
        if (conformance_window_flag) {
            out.ue(conf_win_left_offset);
            out.ue(conf_win_right_offset);
            out.ue(conf_win_top_offset);
            out.ue(conf_win_bottom_offset);
        }

        out.ue(bit_depth_luma_minus8);
        out.ue(bit_depth_chroma_minus8);
        out.ue(log2_max_pic_order_cnt_lsb_minus4); // 0..12
        out.u1(sps_sub_layer_ordering_info_present_flag);

        int sps_max_sub_layers = sps_max_sub_layers_minus1 + 1;
        for (int i = (sps_sub_layer_ordering_info_present_flag ? 0
                : sps_max_sub_layers_minus1); i < sps_max_sub_layers;
                i++) {
            out.ue(sps_max_dec_pic_buffering_minus1[i]);
            out.ue(sps_max_num_reorder_pics[i]);
            out.ue(sps_max_latency_increase_plus1[i]);
        }

        out.ue(log2_min_luma_coding_block_size_minus3);
        out.ue(log2_diff_max_min_luma_coding_block_size);
        out.ue(log2_min_luma_transform_block_size_minus2);
        out.ue(log2_diff_max_min_luma_transform_block_size);
        out.ue(max_transform_hierarchy_depth_inter);
        out.ue(max_transform_hierarchy_depth_intra);

        out.u1(scaling_list_enabled_flag);
        if (scaling_list_enabled_flag) {
            out.u1(sps_scaling_list_data_present_flag);
            if (sps_scaling_list_data_present_flag) {
                scaling_list_data.write(context, out);
            }
        }

        out.u1(amp_enabled_flag);
        out.u1(sample_adaptive_offset_enabled_flag);

        out.u1(pcm_enabled_flag);
        if (pcm_enabled_flag) {
            out.u4(pcm_sample_bit_depth_luma_minus1);
            out.u4(pcm_sample_bit_depth_chroma_minus1);
            out.ue(log2_min_pcm_luma_coding_block_size_minus3);
            out.ue(log2_diff_max_min_pcm_luma_coding_block_size);
            out.u1(pcm_loop_filter_disabled_flag);
        }

        out.ue(num_short_term_ref_pic_sets); // 0..64
        for (int i = 0; i < num_short_term_ref_pic_sets; i++) {
            context.st_ref_pic_set[i].write(context, out);
        }

        out.u1(long_term_ref_pics_present_flag);
        if (long_term_ref_pics_present_flag) {
            out.ue(num_long_term_ref_pics_sps);
            int log2_max_pic_order_cnt_lsb =
                    log2_max_pic_order_cnt_lsb_minus4 + 4;
            for (int i = 0; i < num_long_term_ref_pics_sps; i++) {
                out.u(log2_max_pic_order_cnt_lsb, lt_ref_pic_poc_lsb_sps[i]);
                out.u1(used_by_curr_pic_lt_sps_flag[i]);
            }
        }

        out.u1(sps_temporal_mvp_enabled_flag);
        out.u1(strong_intra_smoothing_enabled_flag);

        out.u1(vui_parameters_present_flag);
        if (vui_parameters_present_flag) {
            vui_parameters.write(context, out);
        }

        out.u1(sps_extension_present_flag);
        if (sps_extension_present_flag) {
            out.u1(sps_range_extension_flag);
            out.u1(sps_multilayer_extension_flag);
            out.u1(sps_3d_extension_flag);
            out.u1(sps_scc_extension_flag);
            out.u4(sps_extension_4bits);
        }

        out.writeTrailingBits(trailing_bits);
    }

    @Override
    public void print(H265Context context, RbspPrinter out) {
        out.u4("sps_video_parameter_set_id", sps_video_parameter_set_id);
        out.u3("sps_max_sub_layers_minus1", sps_max_sub_layers_minus1);
        out.u1("sps_temporal_id_nesting_flag", sps_temporal_id_nesting_flag);

        profile_tier_level.print(context, out);

        out.ue("sps_seq_parameter_set_id", sps_seq_parameter_set_id);
        out.ue("chroma_format_idc", chroma_format_idc);

        if (chroma_format_idc == 3) {
            out.enter();
            out.u1("separate_colour_plane_flag", separate_colour_plane_flag);
            out.leave();
        }

        out.ue("pic_width_in_luma_samples", pic_width_in_luma_samples);
        out.ue("pic_height_in_luma_samples", pic_height_in_luma_samples);

        out.u1("conformance_window_flag", conformance_window_flag);
        if (conformance_window_flag) {
            out.enter();

            out.ue("conf_win_left_offset", conf_win_left_offset);
            out.ue("conf_win_right_offset", conf_win_right_offset);
            out.ue("conf_win_top_offset", conf_win_top_offset);
            out.ue("conf_win_bottom_offset", conf_win_bottom_offset);

            out.leave();
        }

        out.ue("bit_depth_luma_minus8", bit_depth_luma_minus8);
        out.ue("bit_depth_chroma_minus8", bit_depth_chroma_minus8);
        out.ue("log2_max_pic_order_cnt_lsb_minus4",
                log2_max_pic_order_cnt_lsb_minus4); // 0..12
        out.u1("sps_sub_layer_ordering_info_present_flag",
                sps_sub_layer_ordering_info_present_flag);

        int sps_max_sub_layers = sps_max_sub_layers_minus1 + 1;
        for (int i = (sps_sub_layer_ordering_info_present_flag ? 0
                : sps_max_sub_layers_minus1); i < sps_max_sub_layers;
                i++) {
            out.raw("sub_layer_" + i);
            out.enter();

            out.ue("sps_max_dec_pic_buffering_minus1",
                    sps_max_dec_pic_buffering_minus1[i]);
            out.ue("sps_max_num_reorder_pics",
                    sps_max_num_reorder_pics[i]);
            out.ue("sps_max_latency_increase_plus1",
                    sps_max_latency_increase_plus1[i]);

            out.leave();
        }

        out.ue("log2_min_luma_coding_block_size_minus3",
                log2_min_luma_coding_block_size_minus3);
        out.ue("log2_diff_max_min_luma_coding_block_size",
                log2_diff_max_min_luma_coding_block_size);
        out.ue("log2_min_luma_transform_block_size_minus2",
                log2_min_luma_transform_block_size_minus2);
        out.ue("log2_diff_max_min_luma_transform_block_size",
                log2_diff_max_min_luma_transform_block_size);
        out.ue("max_transform_hierarchy_depth_inter",
                max_transform_hierarchy_depth_inter);
        out.ue("max_transform_hierarchy_depth_intra",
                max_transform_hierarchy_depth_intra);

        out.u1("scaling_list_enabled_flag", scaling_list_enabled_flag);
        if (scaling_list_enabled_flag) {
            out.enter();
            out.u1("sps_scaling_list_data_present_flag",
                    sps_scaling_list_data_present_flag);
            if (sps_scaling_list_data_present_flag) {
                scaling_list_data.print(context, out);
            }
            out.leave();
        }

        out.u1("amp_enabled_flag", amp_enabled_flag);
        out.u1("sample_adaptive_offset_enabled_flag",
                sample_adaptive_offset_enabled_flag);

        out.u1("pcm_enabled_flag", pcm_enabled_flag);
        if (pcm_enabled_flag) {
            out.enter();

            out.u4("pcm_sample_bit_depth_luma_minus1",
                    pcm_sample_bit_depth_luma_minus1);
            out.u4("pcm_sample_bit_depth_chroma_minus1",
                    pcm_sample_bit_depth_chroma_minus1);
            out.ue("log2_min_pcm_luma_coding_block_size_minus3",
                    log2_min_pcm_luma_coding_block_size_minus3);
            out.ue("log2_diff_max_min_pcm_luma_coding_block_size",
                    log2_diff_max_min_pcm_luma_coding_block_size);
            out.u1("pcm_loop_filter_disabled_flag",
                    pcm_loop_filter_disabled_flag);

            out.leave();
        }

        out.ue("num_short_term_ref_pic_sets", num_short_term_ref_pic_sets); // 0..64
        for (int i = 0; i < num_short_term_ref_pic_sets; i++) {
            context.st_ref_pic_set[i].print(context, out);
        }

        out.u1("long_term_ref_pics_present_flag",
                long_term_ref_pics_present_flag);
        if (long_term_ref_pics_present_flag) {
            out.enter();

            int log2_max_pic_order_cnt_lsb =
                    log2_max_pic_order_cnt_lsb_minus4 + 4;

            out.ue("num_long_term_ref_pics_sps",
                    num_long_term_ref_pics_sps);
            for (int i = 0; i < num_long_term_ref_pics_sps; i++) {
                out.enter();

                out.printU("lt_ref_pic_poc_lsb_sps: ",
                        log2_max_pic_order_cnt_lsb, lt_ref_pic_poc_lsb_sps[i]);
                out.u1("used_by_curr_pic_lt_sps_flag",
                        used_by_curr_pic_lt_sps_flag[i]);

                out.leave();
            }

            out.leave();
        }

        out.u1("sps_temporal_mvp_enabled_flag",
                sps_temporal_mvp_enabled_flag);
        out.u1("strong_intra_smoothing_enabled_flag",
                strong_intra_smoothing_enabled_flag);

        out.u1("vui_parameters_present_flag", vui_parameters_present_flag);
        if (vui_parameters_present_flag) {
            out.enter();
            vui_parameters.print(context, out);
            out.leave();
        }

        out.u1("sps_extension_present_flag", sps_extension_present_flag);
        if (sps_extension_present_flag) {
            out.enter();

            out.u1("sps_range_extension_flag", sps_range_extension_flag);
            out.u1("sps_multilayer_extension_flag",
                    sps_multilayer_extension_flag);
            out.u1("sps_3d_extension_flag", sps_3d_extension_flag);
            out.u1("sps_scc_extension_flag", sps_scc_extension_flag);
            out.u4("sps_extension_4bits", sps_extension_4bits);

            out.leave();
        }

        out.printH("trailing_bits", trailing_bits);
    }
}
