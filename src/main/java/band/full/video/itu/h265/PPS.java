package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.PPS_NUT;

import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

import java.io.PrintStream;

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
    public void read(RbspReader reader) {
        pps_pic_parameter_set_id = reader.readUE();
        pps_seq_parameter_set_id = reader.readUE();
        dependent_slice_segments_enabled_flag = reader.readU1();
        output_flag_present_flag = reader.readU1();
        num_extra_slice_header_bits = reader.readUByte(3);
        sign_data_hiding_enabled_flag = reader.readU1();
        cabac_init_present_flag = reader.readU1();

        num_ref_idx_l0_default_active_minus1 = reader.readUE();
        num_ref_idx_l1_default_active_minus1 = reader.readUE();
        init_qp_minus26 = reader.readSE();
        constrained_intra_pred_flag = reader.readU1();
        transform_skip_enabled_flag = reader.readU1();

        cu_qp_delta_enabled_flag = reader.readU1();
        if (cu_qp_delta_enabled_flag) {
            diff_cu_qp_delta_depth = reader.readUE();
        }

        pps_cb_qp_offset = reader.readSE();
        pps_cr_qp_offset = reader.readSE();
        pps_slice_chroma_qp_offsets_present_flag = reader.readU1();
        weighted_pred_flag = reader.readU1();
        weighted_bipred_flag = reader.readU1();
        transquant_bypass_enabled_flag = reader.readU1();

        tiles_enabled_flag = reader.readU1();
        entropy_coding_sync_enabled_flag = reader.readU1();
        if (tiles_enabled_flag) {
            num_tile_columns_minus1 = reader.readUE();
            num_tile_rows_minus1 = reader.readUE();

            uniform_spacing_flag = reader.readU1();
            if (!uniform_spacing_flag) {
                column_width_minus1 = new int[num_tile_columns_minus1];
                for (int i = 0; i < num_tile_columns_minus1; i++) {
                    column_width_minus1[i] = reader.readUE();
                }

                row_height_minus1 = new int[num_tile_rows_minus1];
                for (int i = 0; i < num_tile_rows_minus1; i++) {
                    row_height_minus1[i] = reader.readUE();
                }
            }

            loop_filter_across_tiles_enabled_flag = reader.readU1();
        }

        pps_loop_filter_across_slices_enabled_flag = reader.readU1();

        deblocking_filter_control_present_flag = reader.readU1();
        if (deblocking_filter_control_present_flag) {
            deblocking_filter_override_enabled_flag = reader.readU1();
            pps_deblocking_filter_disabled_flag = reader.readU1();
            if (!pps_deblocking_filter_disabled_flag) {
                pps_beta_offset_div2 = reader.readSE();
                pps_tc_offset_div2 = reader.readSE();
            }
        }

        pps_scaling_list_data_present_flag = reader.readU1();
        if (pps_scaling_list_data_present_flag) {
            scaling_list_data = new ScalingListData();
            scaling_list_data.read(reader);
        }

        lists_modification_present_flag = reader.readU1();
        log2_parallel_merge_level_minus2 = reader.readUE();
        slice_segment_header_extension_present_flag = reader.readU1();

        pps_extension_present_flag = reader.readU1();
        if (pps_extension_present_flag) {
            pps_range_extension_flag = reader.readU1();
            pps_multilayer_extension_flag = reader.readU1();
            pps_3d_extension_flag = reader.readU1();
            pps_scc_extension_flag = reader.readU1();
            pps_extension_4bits = reader.readUByte(4);
        }

        trailing_bits = reader.readTrailingBits();
    }

    @Override
    public void write(RbspWriter writer) {
        writer.writeUE(pps_pic_parameter_set_id);
        writer.writeUE(pps_seq_parameter_set_id);
        writer.writeU1(dependent_slice_segments_enabled_flag);
        writer.writeU1(output_flag_present_flag);
        writer.writeU(3, num_extra_slice_header_bits);
        writer.writeU1(sign_data_hiding_enabled_flag);
        writer.writeU1(cabac_init_present_flag);

        writer.writeUE(num_ref_idx_l0_default_active_minus1);
        writer.writeUE(num_ref_idx_l1_default_active_minus1);
        writer.writeSE(init_qp_minus26);
        writer.writeU1(constrained_intra_pred_flag);
        writer.writeU1(transform_skip_enabled_flag);

        writer.writeU1(cu_qp_delta_enabled_flag);
        if (cu_qp_delta_enabled_flag) {
            writer.writeUE(diff_cu_qp_delta_depth);
        }

        writer.writeSE(pps_cb_qp_offset);
        writer.writeSE(pps_cr_qp_offset);
        writer.writeU1(pps_slice_chroma_qp_offsets_present_flag);
        writer.writeU1(weighted_pred_flag);
        writer.writeU1(weighted_bipred_flag);
        writer.writeU1(transquant_bypass_enabled_flag);

        writer.writeU1(tiles_enabled_flag);
        writer.writeU1(entropy_coding_sync_enabled_flag);
        if (tiles_enabled_flag) {
            writer.writeUE(num_tile_columns_minus1);
            writer.writeUE(num_tile_rows_minus1);

            writer.writeU1(uniform_spacing_flag);
            if (!uniform_spacing_flag) {
                for (int i = 0; i < num_tile_columns_minus1; i++) {
                    writer.writeUE(column_width_minus1[i]);
                }

                for (int i = 0; i < num_tile_rows_minus1; i++) {
                    writer.writeUE(row_height_minus1[i]);
                }
            }

            writer.writeU1(loop_filter_across_tiles_enabled_flag);
        }

        writer.writeU1(pps_loop_filter_across_slices_enabled_flag);

        writer.writeU1(deblocking_filter_control_present_flag);
        if (deblocking_filter_control_present_flag) {
            writer.writeU1(deblocking_filter_override_enabled_flag);
            writer.writeU1(pps_deblocking_filter_disabled_flag);
            if (!pps_deblocking_filter_disabled_flag) {
                writer.writeSE(pps_beta_offset_div2);
                writer.writeSE(pps_tc_offset_div2);
            }
        }

        writer.writeU1(pps_scaling_list_data_present_flag);
        if (pps_scaling_list_data_present_flag) {
            scaling_list_data.write(writer);
        }

        writer.writeU1(lists_modification_present_flag);
        writer.writeUE(log2_parallel_merge_level_minus2);
        writer.writeU1(slice_segment_header_extension_present_flag);

        writer.writeU1(pps_extension_present_flag);
        if (pps_extension_present_flag) {
            writer.writeU1(pps_range_extension_flag);
            writer.writeU1(pps_multilayer_extension_flag);
            writer.writeU1(pps_3d_extension_flag);
            writer.writeU1(pps_scc_extension_flag);
            writer.writeU(4, pps_extension_4bits);
        }

        writer.writeTrailingBits(trailing_bits);
    }

    @Override
    public void print(PrintStream ps) {
        ps.print("    pps_pic_parameter_set_id: ");
        ps.println(pps_pic_parameter_set_id);
        ps.print("    pps_seq_parameter_set_id: ");
        ps.println(pps_seq_parameter_set_id);
        ps.print("    dependent_slice_segments_enabled_flag: ");
        ps.println(dependent_slice_segments_enabled_flag);
        ps.print("    output_flag_present_flag: ");
        ps.println(output_flag_present_flag);
        ps.print("    num_extra_slice_header_bits: ");
        ps.println(num_extra_slice_header_bits);
        ps.print("    sign_data_hiding_enabled_flag: ");
        ps.println(sign_data_hiding_enabled_flag);
        ps.print("    cabac_init_present_flag: ");
        ps.println(cabac_init_present_flag);

        ps.print("    num_ref_idx_l0_default_active_minus1: ");
        ps.println(num_ref_idx_l0_default_active_minus1);
        ps.print("    num_ref_idx_l1_default_active_minus1: ");
        ps.println(num_ref_idx_l1_default_active_minus1);
        ps.print("    init_qp_minus26: ");
        ps.println(init_qp_minus26);
        ps.print("    constrained_intra_pred_flag: ");
        ps.println(constrained_intra_pred_flag);
        ps.print("    transform_skip_enabled_flag: ");
        ps.println(transform_skip_enabled_flag);

        ps.print("    cu_qp_delta_enabled_flag: ");
        ps.println(cu_qp_delta_enabled_flag);
        if (cu_qp_delta_enabled_flag) {
            ps.print("      diff_cu_qp_delta_depth: ");
            ps.println(diff_cu_qp_delta_depth);
        }

        ps.print("    pps_cb_qp_offset: ");
        ps.println(pps_cb_qp_offset);
        ps.print("    pps_cr_qp_offset: ");
        ps.println(pps_cr_qp_offset);
        ps.print("    pps_slice_chroma_qp_offsets_present_flag: ");
        ps.println(pps_slice_chroma_qp_offsets_present_flag);
        ps.print("    weighted_pred_flag: ");
        ps.println(weighted_pred_flag);
        ps.print("    weighted_bipred_flag: ");
        ps.println(weighted_bipred_flag);
        ps.print("    transquant_bypass_enabled_flag: ");
        ps.println(transquant_bypass_enabled_flag);

        ps.print("    tiles_enabled_flag: ");
        ps.println(tiles_enabled_flag);
        ps.print("    entropy_coding_sync_enabled_flag: ");
        ps.println(entropy_coding_sync_enabled_flag);
        if (tiles_enabled_flag) {
            ps.print("      num_tile_columns_minus1: ");
            ps.println(num_tile_columns_minus1);
            ps.print("      num_tile_rows_minus1: ");
            ps.println(num_tile_rows_minus1);

            ps.print("      uniform_spacing_flag: ");
            ps.println(uniform_spacing_flag);
            if (!uniform_spacing_flag) {
                for (int i = 0; i < num_tile_columns_minus1; i++) {
                    ps.print("        column_width_minus1[i]: ");
                    ps.println(column_width_minus1[i]);
                }

                for (int i = 0; i < num_tile_rows_minus1; i++) {
                    ps.print("        row_height_minus1[i]: ");
                    ps.println(row_height_minus1[i]);
                }
            }

            ps.print("      loop_filter_across_tiles_enabled_flag: ");
            ps.println(loop_filter_across_tiles_enabled_flag);
        }

        ps.print("    pps_loop_filter_across_slices_enabled_flag: ");
        ps.println(pps_loop_filter_across_slices_enabled_flag);

        ps.print("    deblocking_filter_control_present_flag: ");
        ps.println(deblocking_filter_control_present_flag);
        if (deblocking_filter_control_present_flag) {
            ps.print("      deblocking_filter_override_enabled_flag: ");
            ps.println(deblocking_filter_override_enabled_flag);
            ps.print("      pps_deblocking_filter_disabled_flag: ");
            ps.println(pps_deblocking_filter_disabled_flag);
            if (!pps_deblocking_filter_disabled_flag) {
                ps.print("        pps_beta_offset_div2: ");
                ps.println(pps_beta_offset_div2);
                ps.print("        pps_tc_offset_div2: ");
                ps.println(pps_tc_offset_div2);
            }
        }

        ps.print("    pps_scaling_list_data_present_flag: ");
        ps.println(pps_scaling_list_data_present_flag);
        if (pps_scaling_list_data_present_flag) {
            // scaling_list_data.write(writer);
            ps.print("      scaling_list_data = #####");
        }

        ps.print("    lists_modification_present_flag: ");
        ps.println(lists_modification_present_flag);
        ps.print("    log2_parallel_merge_level_minus2: ");
        ps.println(log2_parallel_merge_level_minus2);
        ps.print("    slice_segment_header_extension_present_flag: ");
        ps.println(slice_segment_header_extension_present_flag);

        ps.print("    pps_extension_present_flag: ");
        ps.println(pps_extension_present_flag);
        if (pps_extension_present_flag) {
            ps.print("      pps_range_extension_flag: ");
            ps.println(pps_range_extension_flag);
            ps.print("      pps_multilayer_extension_flag: ");
            ps.println(pps_multilayer_extension_flag);
            ps.print("      pps_3d_extension_flag: ");
            ps.println(pps_3d_extension_flag);
            ps.print("      pps_scc_extension_flag: ");
            ps.println(pps_scc_extension_flag);
            ps.print("      pps_extension_4bits: ");
            ps.println(pps_extension_4bits);
        }

        // writer.writeTrailingBits(trailing_bits);
    }
}
