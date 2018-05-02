package band.full.video.itu.h265;

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
    ProfileTierLevel profile_tier_level; // ( 1, sps_max_sub_layers_minus1 );
    public int sps_seq_parameter_set_id; // ue(v)

    public int chroma_format_idc; // ue(v)
    // if( chroma_format_idc = = 3 )
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
    public boolean[] sps_max_dec_pic_buffering_minus1;
    public boolean[] sps_max_num_reorder_pics;
    public boolean[] sps_max_latency_increase_plus1;
    // }

    public boolean log2_min_luma_coding_block_size_minus3; // ue(v)
    public boolean log2_diff_max_min_luma_coding_block_size; // ue(v)
    public boolean log2_min_luma_transform_block_size_minus2; // ue(v)
    public boolean log2_diff_max_min_luma_transform_block_size; // ue(v)
    public boolean max_transform_hierarchy_depth_inter; // ue(v)
    public boolean max_transform_hierarchy_depth_intra; // ue(v)

    public boolean scaling_list_enabled_flag; // u(1)
    // if( scaling_list_enabled_flag ) {
    public boolean sps_scaling_list_data_present_flag; // u(1)
    // if( sps_scaling_list_data_present_flag )
    ScalingListData scaling_list_data;
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

    public int num_short_term_ref_pic_sets; // ue(v) 0..64
    // for( i = 0; i < num_short_term_ref_pic_sets; i++)
    public StRefPicSet[] st_ref_pic_set;

    public boolean long_term_ref_pics_present_flag; // u(1)
    // if( long_term_ref_pics_present_flag ) {
    public boolean num_long_term_ref_pics_sps; // ue(v)
    // for( i = 0; i < num_long_term_ref_pics_sps; i++ ) {
    public int lt_ref_pic_poc_lsb_sps; // u(log2_max_pic_order_cnt_lsb_minus4 +
                                       // 4)
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
    public boolean sps_extension_4bits; // u(4)
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
    public void read(RbspReader reader) {
        trailing_bits = reader.readTrailingBits();
    }

    @Override
    public void write(RbspWriter writer) {
        writer.writeTrailingBits(trailing_bits);
    }

    @Override
    public void print(PrintStream ps) {
        // TODO Auto-generated method stub
    }
}
