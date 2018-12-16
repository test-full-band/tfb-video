package band.full.video.dolby;

import static band.full.video.dolby.RpuData.NUM_CMPS;

import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

import java.util.Arrays;

public class RpuHeader implements Structure<NalContext>, NalContext {
    public byte rpu_type; // u(6)
    public short rpu_format; // u(11)
    public byte vdr_rpu_profile; // u(4)
    public byte vdr_rpu_level; // u(4)
    public boolean vdr_seq_info_present; // u(1)
    public boolean chroma_resampling_explicit_filter; // u(1)
    public byte coefficient_data_type; // u(2)
    public int coefficient_log2_denom; // ue(v)
    public byte vdr_rpu_normalized_idc; // u(2)
    public boolean BL_video_full_range; // u(1)
    public int BL_bit_depth_minus8; // ue(v)
    public int EL_bit_depth_minus8; // ue(v)
    public int vdr_bit_depth_minus8; // ue(v)
    public boolean spatial_resampling_filter; // u(1)
    public byte reserved_zero_3bits; // u(3)
    public boolean el_spatial_resampling_filter; // u(1)
    public boolean disable_residual; // u(1)
    public boolean vdr_dm_metadata_present; // u(1)
    public boolean use_prev_vdr_rpu; // u(1)
    public int prev_vdr_rpu_id; // ue(v)
    public int vdr_rpu_id; // ue(v)
    public int mapping_color_space; // ue(v)
    public int mapping_chroma_format_idc; // ue(v)
    public int[][] pred_pivot_value = new int[NUM_CMPS][]; // u(v)
    public byte nlq_method_idc; // u(3)
    public int num_x_partitions_minus1; // ue(v)
    public int num_y_partitions_minus1; // ue(v)

    @Override
    public void read(NalContext context, RbspReader in) {
        rpu_type = in.u6();
        rpu_format = in.u11();
        if (rpu_type == 2) {
            vdr_rpu_profile = in.u4();
            vdr_rpu_level = in.u4();

            vdr_seq_info_present = in.u1();
            if (vdr_seq_info_present) {
                chroma_resampling_explicit_filter = in.u1();

                coefficient_data_type = in.u2();
                if (coefficient_data_type == 0) {
                    coefficient_log2_denom = in.ue();
                    if (coefficient_log2_denom > 23)
                        throw new IllegalStateException(
                                "coefficient_log2_denom: "
                                        + coefficient_log2_denom);
                } else if (coefficient_data_type > 1)
                    throw new IllegalStateException(
                            "coefficient_data_type: "
                                    + coefficient_data_type);

                vdr_rpu_normalized_idc = in.u2();
                BL_video_full_range = in.u1();

                if ((rpu_format & 0x700) == 0) {
                    BL_bit_depth_minus8 = in.ue();
                    EL_bit_depth_minus8 = in.ue();
                    vdr_bit_depth_minus8 = in.ue();
                    spatial_resampling_filter = in.u1();
                    reserved_zero_3bits = in.u3();
                    el_spatial_resampling_filter = in.u1();
                    disable_residual = in.u1();
                } // end sequence header
            } // end of EDR RPU sequence header

            vdr_dm_metadata_present = in.u1();

            use_prev_vdr_rpu = in.u1();
            if (use_prev_vdr_rpu) {
                prev_vdr_rpu_id = in.ue();
            } else {
                vdr_rpu_id = in.ue();
                mapping_color_space = in.ue();
                mapping_chroma_format_idc = in.ue();

                for (int c = 0; c < NUM_CMPS; c++) {
                    int num_pivots = in.ue() + 2;
                    pred_pivot_value[c] = new int[num_pivots];
                    for (int p = 0; p < num_pivots; p++) {
                        pred_pivot_value[c][p] =
                                in.readUInt(BL_bit_depth_minus8 + 8);
                    }
                } // end of pivot points for BL three components

                if ((rpu_format & 0x700) == 0 && !disable_residual) {
                    nlq_method_idc = in.u3();
                } // end of v1.x architecture EL specific

                num_x_partitions_minus1 = in.ue();
                num_y_partitions_minus1 = in.ue();
            } // end of EDR RPU frame header
        } // end of EDR RPU header
    }

    @Override
    public void write(NalContext context, RbspWriter out) {
        out.u6(rpu_type);
        out.u11(rpu_format);
        if (rpu_type == 2) {
            out.u4(vdr_rpu_profile);
            out.u4(vdr_rpu_level);

            out.u1(vdr_seq_info_present);
            if (vdr_seq_info_present) {
                out.u1(chroma_resampling_explicit_filter);

                out.u2(coefficient_data_type);
                if (coefficient_data_type == 0) {
                    out.ue(coefficient_log2_denom);
                }

                out.u2(vdr_rpu_normalized_idc);
                out.u1(BL_video_full_range);

                if ((rpu_format & 0x700) == 0) {
                    out.ue(BL_bit_depth_minus8);
                    out.ue(EL_bit_depth_minus8);
                    out.ue(vdr_bit_depth_minus8);
                    out.u1(spatial_resampling_filter);
                    out.u3(reserved_zero_3bits);
                    out.u1(el_spatial_resampling_filter);
                    out.u1(disable_residual);
                } // end sequence header
            } // end of EDR RPU sequence header

            out.u1(vdr_dm_metadata_present);

            out.u1(use_prev_vdr_rpu);
            if (use_prev_vdr_rpu) {
                out.ue(prev_vdr_rpu_id);
            } else {
                out.ue(vdr_rpu_id);
                out.ue(mapping_color_space);
                out.ue(mapping_chroma_format_idc);

                if (pred_pivot_value.length != NUM_CMPS)
                    throw new IllegalStateException(
                            "pred_pivot_value.length: "
                                    + pred_pivot_value.length);

                for (int c = 0; c < NUM_CMPS; c++) {
                    int num_pivots = pred_pivot_value[c].length;
                    out.ue(num_pivots - 2);
                    for (int p = 0; p < num_pivots; p++) {
                        out.u(BL_bit_depth_minus8 + 8, pred_pivot_value[c][p]);
                    }
                } // end of pivot points for BL three components

                if ((rpu_format & 0x700) == 0 && !disable_residual) {
                    out.u3(nlq_method_idc);
                } // end of v1.x architecture EL specific

                out.ue(num_x_partitions_minus1);
                out.ue(num_y_partitions_minus1);
            } // end of EDR RPU frame header
        } // end of EDR RPU header
    }

    @Override
    public void print(NalContext context, RbspPrinter out) {
        out.u6("rpu_type", rpu_type);
        out.u11("rpu_format", rpu_format);
        if (rpu_type == 2) {
            out.u4("vdr_rpu_profile", vdr_rpu_profile);
            out.u4("vdr_rpu_level", vdr_rpu_level);

            out.u1("vdr_seq_info_present", vdr_seq_info_present);
            if (vdr_seq_info_present) {
                out.u1("chroma_resampling_explicit_filter",
                        chroma_resampling_explicit_filter);

                out.u2("coefficient_data_type", coefficient_data_type);
                if (coefficient_data_type == 0) {
                    out.ue("coefficient_log2_denom", coefficient_log2_denom);
                }

                out.u2("vdr_rpu_normalized_idc", vdr_rpu_normalized_idc);
                out.u1("BL_video_full_range", BL_video_full_range);

                if ((rpu_format & 0x700) == 0) {
                    out.ue("BL_bit_depth_minus8", BL_bit_depth_minus8);
                    out.ue("EL_bit_depth_minus8", EL_bit_depth_minus8);
                    out.ue("vdr_bit_depth_minus8", vdr_bit_depth_minus8);

                    out.u1("spatial_resampling_filter",
                            spatial_resampling_filter);

                    out.u3("reserved_zero_3bits", reserved_zero_3bits);

                    out.u1("el_spatial_resampling_filter",
                            el_spatial_resampling_filter);

                    out.u1("disable_residual", disable_residual);
                } // end sequence header
            } // end of EDR RPU sequence header

            out.u1("vdr_dm_metadata_present", vdr_dm_metadata_present);

            out.u1("use_prev_vdr_rpu", use_prev_vdr_rpu);
            if (use_prev_vdr_rpu) {
                out.ue("prev_vdr_rpu_id", prev_vdr_rpu_id);
            } else {
                out.ue("vdr_rpu_id", vdr_rpu_id);
                out.ue("mapping_color_space", mapping_color_space);
                out.ue("mapping_chroma_format_idc", mapping_chroma_format_idc);

                for (int c = 0; c < NUM_CMPS; c++) {
                    int num_pivots = pred_pivot_value[c].length;
                    out.ue("num_pivots_minus2", num_pivots - 2);
                    out.raw("pred_pivot_value[" + c + "]: "
                            + Arrays.toString(pred_pivot_value[c]));
                } // end of pivot points for BL three components

                if ((rpu_format & 0x700) == 0 && !disable_residual) {
                    out.u3("nlq_method_idc", nlq_method_idc);
                } // end of v1.x architecture EL specific

                out.ue("num_x_partitions_minus1", num_x_partitions_minus1);
                out.ue("num_y_partitions_minus1", num_y_partitions_minus1);
            } // end of EDR RPU frame header
        } // end of EDR RPU header
    }
}
