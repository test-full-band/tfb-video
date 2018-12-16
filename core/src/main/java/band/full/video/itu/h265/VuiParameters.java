package band.full.video.itu.h265;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

/**
 * E.2.1 VUI parameters syntax
 * <p>
 * <code>vui_parameters()</code>
 *
 * @author Igor Malinin
 */
public class VuiParameters implements Structure<H265Context> {
    public final byte maxNumSubLayersMinus1;

    public boolean aspect_ratio_info_present;
    public short aspect_ratio_idc; // u8
    public int sar_width; // u16
    public int sar_height; // u16

    public boolean overscan_info_present;
    public boolean overscan_appropriate;

    public boolean video_signal_type_present;
    public byte video_format; // u3
    public boolean video_full_range;
    public boolean colour_description_present;
    public short colour_primaries; // u8
    public short transfer_characteristics; // u8
    public short matrix_coeffs; // u8

    public boolean chroma_loc_info_present;
    public int chroma_sample_loc_type_top_field; // ue
    public int chroma_sample_loc_type_bottom_field; // ue

    public boolean neutral_chroma_indication;
    public boolean field_seq;
    public boolean frame_field_info_present;
    public boolean default_display_window;
    public int def_disp_win_left_offset; // ue
    public int def_disp_win_right_offset; // ue
    public int def_disp_win_top_offset; // ue
    public int def_disp_win_bottom_offset; // ue
    public boolean vui_timing_info_present;
    public long vui_num_units_in_tick; // u32
    public long vui_time_scale; // u32
    public boolean vui_poc_proportional_to_timing;
    public int vui_num_ticks_poc_diff_one_minus1; // ue
    public boolean vui_hrd_parameters_present;
    public HrdParameters hrd_parameters;
    public boolean bitstream_restriction;
    public boolean tiles_fixed_structure;
    public boolean motion_vectors_over_pic_boundaries;
    public boolean restricted_ref_pic_lists;
    public int min_spatial_segmentation_idc; // ue
    public int max_bytes_per_pic_denom; // ue
    public int max_bits_per_min_cu_denom; // ue
    public int log2_max_mv_length_horizontal; // ue
    public int log2_max_mv_length_vertical; // ue

    public VuiParameters(byte maxNumSubLayersMinus1) {
        this.maxNumSubLayersMinus1 = maxNumSubLayersMinus1;
    }

    @Override
    public void read(H265Context context, RbspReader in) {
        aspect_ratio_info_present = in.u1();
        if (aspect_ratio_info_present) {
            aspect_ratio_idc = in.u8();
            if (aspect_ratio_idc == 255) { // TODO EXTENDED_SAR constant
                sar_width = in.u16();
                sar_height = in.u16();
            }
        }

        overscan_info_present = in.u1();
        if (overscan_info_present) {
            overscan_appropriate = in.u1();
        }

        video_signal_type_present = in.u1();
        if (video_signal_type_present) {
            video_format = in.u3();
            video_full_range = in.u1();
            colour_description_present = in.u1();
            if (colour_description_present) {
                colour_primaries = in.u8();
                transfer_characteristics = in.u8();
                matrix_coeffs = in.u8();
            }
        }

        chroma_loc_info_present = in.u1();
        if (chroma_loc_info_present) {
            chroma_sample_loc_type_top_field = in.ue();
            chroma_sample_loc_type_bottom_field = in.ue();
        }

        neutral_chroma_indication = in.u1();
        field_seq = in.u1();
        frame_field_info_present = in.u1();
        default_display_window = in.u1();
        if (default_display_window) {
            def_disp_win_left_offset = in.ue();
            def_disp_win_right_offset = in.ue();
            def_disp_win_top_offset = in.ue();
            def_disp_win_bottom_offset = in.ue();
        }

        vui_timing_info_present = in.u1();
        if (vui_timing_info_present) {
            vui_num_units_in_tick = in.u32();
            vui_time_scale = in.u32();

            vui_poc_proportional_to_timing = in.u1();
            if (vui_poc_proportional_to_timing) {
                vui_num_ticks_poc_diff_one_minus1 = in.ue();
            }

            vui_hrd_parameters_present = in.u1();
            if (vui_hrd_parameters_present) {
                hrd_parameters = new HrdParameters(true, maxNumSubLayersMinus1);
                hrd_parameters.read(context, in);
            }
        }

        bitstream_restriction = in.u1();
        if (bitstream_restriction) {
            tiles_fixed_structure = in.u1();
            motion_vectors_over_pic_boundaries = in.u1();
            restricted_ref_pic_lists = in.u1();
            min_spatial_segmentation_idc = in.ue();
            max_bytes_per_pic_denom = in.ue();
            max_bits_per_min_cu_denom = in.ue();

            log2_max_mv_length_horizontal = in.ue();
            log2_max_mv_length_vertical = in.ue();
        }
    }

    @Override
    public void write(H265Context context, RbspWriter out) {
        out.u1(aspect_ratio_info_present);
        if (aspect_ratio_info_present) {
            out.u8(aspect_ratio_idc);
            if (aspect_ratio_idc == 255) { // TODO EXTENDED_SAR constant
                out.u16(sar_width);
                out.u16(sar_height);
            }
        }

        out.u1(overscan_info_present);
        if (overscan_info_present) {
            out.u1(overscan_appropriate);
        }

        out.u1(video_signal_type_present);
        if (video_signal_type_present) {
            out.u3(video_format);
            out.u1(video_full_range);
            out.u1(colour_description_present);
            if (colour_description_present) {
                out.u8(colour_primaries);
                out.u8(transfer_characteristics);
                out.u8(matrix_coeffs);
            }
        }

        out.u1(chroma_loc_info_present);
        if (chroma_loc_info_present) {
            out.ue(chroma_sample_loc_type_top_field);
            out.ue(chroma_sample_loc_type_bottom_field);
        }

        out.u1(neutral_chroma_indication);
        out.u1(field_seq);
        out.u1(frame_field_info_present);
        out.u1(default_display_window);
        if (default_display_window) {
            out.ue(def_disp_win_left_offset);
            out.ue(def_disp_win_right_offset);
            out.ue(def_disp_win_top_offset);
            out.ue(def_disp_win_bottom_offset);
        }

        out.u1(vui_timing_info_present);
        if (vui_timing_info_present) {
            out.u32(vui_num_units_in_tick);
            out.u32(vui_time_scale);

            out.u1(vui_poc_proportional_to_timing);
            if (vui_poc_proportional_to_timing) {
                out.ue(vui_num_ticks_poc_diff_one_minus1);
            }

            out.u1(vui_hrd_parameters_present);
            if (vui_hrd_parameters_present) {
                hrd_parameters.write(context, out);
            }
        }

        out.u1(bitstream_restriction);
        if (bitstream_restriction) {
            out.u1(tiles_fixed_structure);
            out.u1(motion_vectors_over_pic_boundaries);
            out.u1(restricted_ref_pic_lists);
            out.ue(min_spatial_segmentation_idc);
            out.ue(max_bytes_per_pic_denom);
            out.ue(max_bits_per_min_cu_denom);

            out.ue(log2_max_mv_length_horizontal);
            out.ue(log2_max_mv_length_vertical);
        }
    }

    @Override
    public void print(H265Context context, RbspPrinter out) {
        out.u1("aspect_ratio_info_present", aspect_ratio_info_present);
        if (aspect_ratio_info_present) {
            out.enter();

            out.u8("aspect_ratio_idc", aspect_ratio_idc);
            if (aspect_ratio_idc == 255) { // TODO EXTENDED_SAR constant
                out.enter();
                out.u16("sar_width", sar_width);
                out.u16("sar_height", sar_height);
                out.leave();
            }

            out.leave();
        }

        out.u1("overscan_info_present", overscan_info_present);
        if (overscan_info_present) {
            out.enter();
            out.u1("overscan_appropriate", overscan_appropriate);
            out.leave();
        }

        out.u1("video_signal_type_present", video_signal_type_present);
        if (video_signal_type_present) {
            out.enter();

            out.u3("video_format", video_format);
            out.u1("video_full_range", video_full_range);

            out.u1("colour_description_present",
                    colour_description_present);
            if (colour_description_present) {
                out.enter();

                out.u8("colour_primaries", colour_primaries);
                out.u8("transfer_characteristics", transfer_characteristics);
                out.u8("matrix_coeffs", matrix_coeffs);

                out.leave();
            }

            out.leave();
        }

        out.u1("chroma_loc_info_present", chroma_loc_info_present);
        if (chroma_loc_info_present) {
            out.enter();

            out.ue("chroma_sample_loc_type_top_field",
                    chroma_sample_loc_type_top_field);
            out.ue("chroma_sample_loc_type_bottom_field",
                    chroma_sample_loc_type_bottom_field);

            out.leave();
        }

        out.u1("neutral_chroma_indication", neutral_chroma_indication);
        out.u1("field_seq", field_seq);
        out.u1("frame_field_info_present", frame_field_info_present);
        out.u1("default_display_window", default_display_window);
        if (default_display_window) {
            out.enter();

            out.ue("def_disp_win_left_offset", def_disp_win_left_offset);
            out.ue("def_disp_win_right_offset", def_disp_win_right_offset);
            out.ue("def_disp_win_top_offset", def_disp_win_top_offset);
            out.ue("def_disp_win_bottom_offset",
                    def_disp_win_bottom_offset);

            out.leave();
        }

        out.u1("vui_timing_info_present", vui_timing_info_present);
        if (vui_timing_info_present) {
            out.enter();

            out.u32("vui_num_units_in_tick", vui_num_units_in_tick);
            out.u32("vui_time_scale", vui_time_scale);

            out.u1("vui_poc_proportional_to_timing",
                    vui_poc_proportional_to_timing);
            if (vui_poc_proportional_to_timing) {
                out.enter();

                out.ue("vui_num_ticks_poc_diff_one_minus1",
                        vui_num_ticks_poc_diff_one_minus1);

                out.leave();
            }

            out.u1("vui_hrd_parameters_present",
                    vui_hrd_parameters_present);
            if (vui_hrd_parameters_present) {
                out.enter();
                hrd_parameters.print(context, out);
                out.leave();
            }

            out.leave();
        }

        out.u1("bitstream_restriction", bitstream_restriction);
        if (bitstream_restriction) {
            out.enter();

            out.u1("tiles_fixed_structure", tiles_fixed_structure);
            out.u1("motion_vectors_over_pic_boundaries",
                    motion_vectors_over_pic_boundaries);
            out.u1("restricted_ref_pic_lists", restricted_ref_pic_lists);
            out.ue("min_spatial_segmentation_idc",
                    min_spatial_segmentation_idc);
            out.ue("max_bytes_per_pic_denom", max_bytes_per_pic_denom);
            out.ue("max_bits_per_min_cu_denom", max_bits_per_min_cu_denom);

            out.ue("log2_max_mv_length_horizontal",
                    log2_max_mv_length_horizontal);
            out.ue("log2_max_mv_length_vertical", log2_max_mv_length_vertical);

            out.leave();
        }
    }
}
