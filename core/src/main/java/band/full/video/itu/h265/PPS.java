package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.PPS_NUT;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

/**
 * 7.3.2.3 Picture parameter set RBSP syntax<br>
 * 7.3.2.3.1 General picture parameter set RBSP syntax<br>
 * pic_parameter_set_rbsp()
 *
 * @author Igor Malinin
 */
public class PPS extends NALUnit {
    public int pps_pic_parameter_set_id; // ue(v)
    public int pps_seq_parameter_set_id; // ue(v)
    public boolean dependent_slice_segments_enabled_flag; // u(1)
    public boolean output_flag_present_flag; // u(1)
    public byte num_extra_slice_header_bits; // u(3)
    public boolean sign_data_hiding_enabled_flag; // u(1)
    public boolean cabac_init_present_flag; // u(1)

    public int num_ref_idx_l0_default_active_minus1; // ue(v)
    public int num_ref_idx_l1_default_active_minus1; // ue(v)
    public int init_qp_minus26; // se(v)
    public boolean constrained_intra_pred_flag; // u(1)
    public boolean transform_skip_enabled_flag; // u(1)

    public boolean cu_qp_delta_enabled_flag; // u(1)
    // if (cu_qp_delta_enabled_flag)
    public int diff_cu_qp_delta_depth; // ue(v)

    public int pps_cb_qp_offset; // se(v)
    public int pps_cr_qp_offset; // se(v)
    public boolean pps_slice_chroma_qp_offsets_present_flag; // u(1)
    public boolean weighted_pred_flag; // u(1)
    public boolean weighted_bipred_flag; // u(1)
    public boolean transquant_bypass_enabled_flag; // u(1)

    public boolean tiles_enabled_flag; // u(1)
    public boolean entropy_coding_sync_enabled_flag; // u(1)
    // if( tiles_enabled_flag ) {
    public int num_tile_columns_minus1; // ue(v)
    public int num_tile_rows_minus1; // ue(v)
    public boolean uniform_spacing_flag; // u(1)
    // if( !uniform_spacing_flag ) {
    // for(i=0;i<num_tile_columns_minus1;i++)
    public int[] column_width_minus1; // ue(v)
    // for(i=0;i<num_tile_rows_minus1;i++)
    public int[] row_height_minus1; // ue(v)
    // }
    public boolean loop_filter_across_tiles_enabled_flag; // u(1)
    // }

    public boolean pps_loop_filter_across_slices_enabled_flag; // u(1)

    public boolean deblocking_filter_control_present_flag; // u(1)
    // if( deblocking_filter_control_present_flag ) {
    public boolean deblocking_filter_override_enabled_flag; // u(1)
    public boolean pps_deblocking_filter_disabled_flag; // u(1)
    // if( !pps_deblocking_filter_disabled_flag ) {
    public int pps_beta_offset_div2; // se(v)
    public int pps_tc_offset_div2; // se(v)
    // }
    // }

    public boolean pps_scaling_list_data_present_flag; // u(1)
    // if( pps_scaling_list_data_present_flag )
    public ScalingListData scaling_list_data;

    public boolean lists_modification_present_flag; // u(1)
    public int log2_parallel_merge_level_minus2; // ue(v)
    public boolean slice_segment_header_extension_present_flag; // u(1)

    public boolean pps_extension_present_flag; // u(1)
    // if( pps_extension_present_flag ) {
    public boolean pps_range_extension_flag; // u(1)
    public boolean pps_multilayer_extension_flag; // u(1)
    public boolean pps_3d_extension_flag; // u(1)
    public boolean pps_scc_extension_flag; // u(1)
    public byte pps_extension_4bits; // u(4)
    // }

    // if( pps_range_extension_flag )
    // pps_range_extension( )
    // if( pps_multilayer_extension_flag )
    // pps_multilayer_extension( ) /* specified in Annex F */
    // if( pps_3d_extension_flag )
    // pps_3d_extension( ) /* specified in Annex I */
    // if( pps_scc_extension_flag )
    // pps_scc_extension( )
    // if( pps_extension_4bits )
    // while( more_rbsp_data( ) )
    // pps_extension_data_flag; // u(1)
    // rbsp_trailing_bits( )

    public byte[] trailing_bits;

    public PPS() {
        super(PPS_NUT);
    }

    @Override
    public void read(H265Context context, RbspReader in) {
        pps_pic_parameter_set_id = in.ue();
        pps_seq_parameter_set_id = in.ue();
        dependent_slice_segments_enabled_flag = in.u1();
        output_flag_present_flag = in.u1();
        num_extra_slice_header_bits = in.u3();
        sign_data_hiding_enabled_flag = in.u1();
        cabac_init_present_flag = in.u1();

        num_ref_idx_l0_default_active_minus1 = in.ue();
        num_ref_idx_l1_default_active_minus1 = in.ue();
        init_qp_minus26 = in.se();
        constrained_intra_pred_flag = in.u1();
        transform_skip_enabled_flag = in.u1();

        cu_qp_delta_enabled_flag = in.u1();
        if (cu_qp_delta_enabled_flag) {
            diff_cu_qp_delta_depth = in.ue();
        }

        pps_cb_qp_offset = in.se();
        pps_cr_qp_offset = in.se();
        pps_slice_chroma_qp_offsets_present_flag = in.u1();
        weighted_pred_flag = in.u1();
        weighted_bipred_flag = in.u1();
        transquant_bypass_enabled_flag = in.u1();

        tiles_enabled_flag = in.u1();
        entropy_coding_sync_enabled_flag = in.u1();
        if (tiles_enabled_flag) {
            num_tile_columns_minus1 = in.ue();
            num_tile_rows_minus1 = in.ue();

            uniform_spacing_flag = in.u1();
            if (!uniform_spacing_flag) {
                column_width_minus1 = new int[num_tile_columns_minus1];
                for (int i = 0; i < num_tile_columns_minus1; i++) {
                    column_width_minus1[i] = in.ue();
                }

                row_height_minus1 = new int[num_tile_rows_minus1];
                for (int i = 0; i < num_tile_rows_minus1; i++) {
                    row_height_minus1[i] = in.ue();
                }
            }

            loop_filter_across_tiles_enabled_flag = in.u1();
        }

        pps_loop_filter_across_slices_enabled_flag = in.u1();

        deblocking_filter_control_present_flag = in.u1();
        if (deblocking_filter_control_present_flag) {
            deblocking_filter_override_enabled_flag = in.u1();
            pps_deblocking_filter_disabled_flag = in.u1();
            if (!pps_deblocking_filter_disabled_flag) {
                pps_beta_offset_div2 = in.se();
                pps_tc_offset_div2 = in.se();
            }
        }

        pps_scaling_list_data_present_flag = in.u1();
        if (pps_scaling_list_data_present_flag) {
            scaling_list_data = new ScalingListData();
            scaling_list_data.read(context, in);
        }

        lists_modification_present_flag = in.u1();
        log2_parallel_merge_level_minus2 = in.ue();
        slice_segment_header_extension_present_flag = in.u1();

        pps_extension_present_flag = in.u1();
        if (pps_extension_present_flag) {
            pps_range_extension_flag = in.u1();
            pps_multilayer_extension_flag = in.u1();
            pps_3d_extension_flag = in.u1();
            pps_scc_extension_flag = in.u1();
            pps_extension_4bits = in.u4();
        }

        trailing_bits = in.readTrailingBits();
    }

    @Override
    public void write(H265Context context, RbspWriter out) {
        out.ue(pps_pic_parameter_set_id);
        out.ue(pps_seq_parameter_set_id);
        out.u1(dependent_slice_segments_enabled_flag);
        out.u1(output_flag_present_flag);
        out.u3(num_extra_slice_header_bits);
        out.u1(sign_data_hiding_enabled_flag);
        out.u1(cabac_init_present_flag);

        out.ue(num_ref_idx_l0_default_active_minus1);
        out.ue(num_ref_idx_l1_default_active_minus1);
        out.se(init_qp_minus26);
        out.u1(constrained_intra_pred_flag);
        out.u1(transform_skip_enabled_flag);

        out.u1(cu_qp_delta_enabled_flag);
        if (cu_qp_delta_enabled_flag) {
            out.ue(diff_cu_qp_delta_depth);
        }

        out.se(pps_cb_qp_offset);
        out.se(pps_cr_qp_offset);
        out.u1(pps_slice_chroma_qp_offsets_present_flag);
        out.u1(weighted_pred_flag);
        out.u1(weighted_bipred_flag);
        out.u1(transquant_bypass_enabled_flag);

        out.u1(tiles_enabled_flag);
        out.u1(entropy_coding_sync_enabled_flag);
        if (tiles_enabled_flag) {
            out.ue(num_tile_columns_minus1);
            out.ue(num_tile_rows_minus1);

            out.u1(uniform_spacing_flag);
            if (!uniform_spacing_flag) {
                for (int i = 0; i < num_tile_columns_minus1; i++) {
                    out.ue(column_width_minus1[i]);
                }

                for (int i = 0; i < num_tile_rows_minus1; i++) {
                    out.ue(row_height_minus1[i]);
                }
            }

            out.u1(loop_filter_across_tiles_enabled_flag);
        }

        out.u1(pps_loop_filter_across_slices_enabled_flag);

        out.u1(deblocking_filter_control_present_flag);
        if (deblocking_filter_control_present_flag) {
            out.u1(deblocking_filter_override_enabled_flag);
            out.u1(pps_deblocking_filter_disabled_flag);
            if (!pps_deblocking_filter_disabled_flag) {
                out.se(pps_beta_offset_div2);
                out.se(pps_tc_offset_div2);
            }
        }

        out.u1(pps_scaling_list_data_present_flag);
        if (pps_scaling_list_data_present_flag) {
            scaling_list_data.write(context, out);
        }

        out.u1(lists_modification_present_flag);
        out.ue(log2_parallel_merge_level_minus2);
        out.u1(slice_segment_header_extension_present_flag);

        out.u1(pps_extension_present_flag);
        if (pps_extension_present_flag) {
            out.u1(pps_range_extension_flag);
            out.u1(pps_multilayer_extension_flag);
            out.u1(pps_3d_extension_flag);
            out.u1(pps_scc_extension_flag);
            out.u4(pps_extension_4bits);
        }

        out.writeTrailingBits(trailing_bits);
    }

    @Override
    public void print(H265Context context, RbspPrinter out) {
        out.ue("pps_pic_parameter_set_id", pps_pic_parameter_set_id);
        out.ue("pps_seq_parameter_set_id", pps_seq_parameter_set_id);

        out.u1("dependent_slice_segments_enabled_flag",
                dependent_slice_segments_enabled_flag);

        out.u1("output_flag_present_flag", output_flag_present_flag);
        out.u3("num_extra_slice_header_bits", num_extra_slice_header_bits);
        out.u1("sign_data_hiding_enabled_flag", sign_data_hiding_enabled_flag);
        out.u1("cabac_init_present_flag", cabac_init_present_flag);

        out.ue("num_ref_idx_l0_default_active_minus1",
                num_ref_idx_l0_default_active_minus1);

        out.ue("num_ref_idx_l1_default_active_minus1",
                num_ref_idx_l1_default_active_minus1);

        out.se("init_qp_minus26", init_qp_minus26);
        out.u1("constrained_intra_pred_flag", constrained_intra_pred_flag);
        out.u1("transform_skip_enabled_flag", transform_skip_enabled_flag);

        out.u1("cu_qp_delta_enabled_flag", cu_qp_delta_enabled_flag);
        if (cu_qp_delta_enabled_flag) {
            out.enter();
            out.ue("diff_cu_qp_delta_depth", diff_cu_qp_delta_depth);
            out.leave();
        }

        out.se("pps_cb_qp_offset", pps_cb_qp_offset);
        out.se("pps_cr_qp_offset", pps_cr_qp_offset);

        out.u1("pps_slice_chroma_qp_offsets_present_flag",
                pps_slice_chroma_qp_offsets_present_flag);

        out.u1("weighted_pred_flag", weighted_pred_flag);
        out.u1("weighted_bipred_flag", weighted_bipred_flag);

        out.u1("transquant_bypass_enabled_flag",
                transquant_bypass_enabled_flag);

        out.u1("tiles_enabled_flag", tiles_enabled_flag);

        out.u1("entropy_coding_sync_enabled_flag",
                entropy_coding_sync_enabled_flag);

        if (tiles_enabled_flag) {
            out.ue("num_tile_columns_minus1", num_tile_columns_minus1);
            out.ue("num_tile_rows_minus1", num_tile_rows_minus1);

            out.enter();

            out.u1("uniform_spacing_flag", uniform_spacing_flag);
            if (!uniform_spacing_flag) {
                out.enter();

                for (int i = 0; i < num_tile_columns_minus1; i++) {
                    out.ue("column_width_minus1[i]", column_width_minus1[i]);
                }

                for (int i = 0; i < num_tile_rows_minus1; i++) {
                    out.ue("row_height_minus1[i]", row_height_minus1[i]);
                }

                out.leave();
            }

            out.u1("loop_filter_across_tiles_enabled_flag",
                    loop_filter_across_tiles_enabled_flag);

            out.leave();
        }

        out.u1("pps_loop_filter_across_slices_enabled_flag",
                pps_loop_filter_across_slices_enabled_flag);

        out.u1("deblocking_filter_control_present_flag",
                deblocking_filter_control_present_flag);

        if (deblocking_filter_control_present_flag) {
            out.enter();

            out.u1("deblocking_filter_override_enabled_flag",
                    deblocking_filter_override_enabled_flag);
            out.u1("pps_deblocking_filter_disabled_flag",
                    pps_deblocking_filter_disabled_flag);
            if (!pps_deblocking_filter_disabled_flag) {
                out.enter();

                out.se("pps_beta_offset_div2", pps_beta_offset_div2);
                out.se("pps_tc_offset_div2", pps_tc_offset_div2);

                out.leave();
            }

            out.leave();
        }

        out.u1("pps_scaling_list_data_present_flag",
                pps_scaling_list_data_present_flag);

        if (pps_scaling_list_data_present_flag) {
            out.enter();
            scaling_list_data.print(context, out);
            out.leave();
        }

        out.u1("lists_modification_present_flag",
                lists_modification_present_flag);

        out.ue("log2_parallel_merge_level_minus2",
                log2_parallel_merge_level_minus2);

        out.u1("slice_segment_header_extension_present_flag",
                slice_segment_header_extension_present_flag);

        out.u1("pps_extension_present_flag", pps_extension_present_flag);
        if (pps_extension_present_flag) {
            out.enter();

            out.u1("pps_range_extension_flag", pps_range_extension_flag);
            out.u1("pps_multilayer_extension_flag",
                    pps_multilayer_extension_flag);
            out.u1("pps_3d_extension_flag", pps_3d_extension_flag);
            out.u1("pps_scc_extension_flag", pps_scc_extension_flag);
            out.u4("pps_extension_4bits", pps_extension_4bits);

            out.leave();
        }

        if (trailing_bits.length > 0) {
            out.printH("trailing_bits", trailing_bits);
        }
    }
}
