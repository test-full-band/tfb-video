package band.full.video.itu.h265;

import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

import java.io.PrintStream;

/**
 * E.2.1 VUI parameters syntax
 * <p>
 * <code>vui_parameters()</code>
 *
 * @author Igor Malinin
 */
public class VuiParameters implements Structure {
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
    public void read(RbspReader reader) {
        aspect_ratio_info_present = reader.readU1();
        if (aspect_ratio_info_present) {
            aspect_ratio_idc = reader.readUShort(8);
            if (aspect_ratio_idc == 255) { // TODO EXTENDED_SAR constant
                sar_width = reader.readUInt(16);
                sar_height = reader.readUInt(16);
            }
        }

        overscan_info_present = reader.readU1();
        if (overscan_info_present) {
            overscan_appropriate = reader.readU1();
        }

        video_signal_type_present = reader.readU1();
        if (video_signal_type_present) {
            video_format = reader.readUByte(3);
            video_full_range = reader.readU1();
            colour_description_present = reader.readU1();
            if (colour_description_present) {
                colour_primaries = reader.readUShort(8);
                transfer_characteristics = reader.readUShort(8);
                matrix_coeffs = reader.readUShort(8);
            }
        }

        chroma_loc_info_present = reader.readU1();
        if (chroma_loc_info_present) {
            chroma_sample_loc_type_top_field = reader.readUE();
            chroma_sample_loc_type_bottom_field = reader.readUE();
        }

        neutral_chroma_indication = reader.readU1();
        field_seq = reader.readU1();
        frame_field_info_present = reader.readU1();
        default_display_window = reader.readU1();
        if (default_display_window) {
            def_disp_win_left_offset = reader.readUE();
            def_disp_win_right_offset = reader.readUE();
            def_disp_win_top_offset = reader.readUE();
            def_disp_win_bottom_offset = reader.readUE();
        }

        vui_timing_info_present = reader.readU1();
        if (vui_timing_info_present) {
            vui_num_units_in_tick = reader.readULong(32);
            vui_time_scale = reader.readULong(32);

            vui_poc_proportional_to_timing = reader.readU1();
            if (vui_poc_proportional_to_timing) {
                vui_num_ticks_poc_diff_one_minus1 = reader.readUE();
            }

            vui_hrd_parameters_present = reader.readU1();
            if (vui_hrd_parameters_present) {
                hrd_parameters = new HrdParameters(true, maxNumSubLayersMinus1);
            }
        }

        bitstream_restriction = reader.readU1();
        if (bitstream_restriction) {
            tiles_fixed_structure = reader.readU1();
            motion_vectors_over_pic_boundaries = reader.readU1();
            restricted_ref_pic_lists = reader.readU1();
            min_spatial_segmentation_idc = reader.readUE();
            max_bytes_per_pic_denom = reader.readUE();
            max_bits_per_min_cu_denom = reader.readUE();
            log2_max_mv_length_horizontal = reader.readUE();
            log2_max_mv_length_vertical = reader.readUE();
        }
    }

    @Override
    public void write(RbspWriter writer) {
        writer.writeU1(aspect_ratio_info_present);
        if (aspect_ratio_info_present) {
            writer.writeU(8, aspect_ratio_idc);
            if (aspect_ratio_idc == 255) { // TODO EXTENDED_SAR constant
                writer.writeU(16, sar_width);
                writer.writeU(16, sar_height);
            }
        }

        writer.writeU1(overscan_info_present);
        if (overscan_info_present) {
            writer.writeU1(overscan_appropriate);
        }

        writer.writeU1(video_signal_type_present);
        if (video_signal_type_present) {
            writer.writeU(3, video_format);
            writer.writeU1(video_full_range);
            writer.writeU1(colour_description_present);
            if (colour_description_present) {
                writer.writeU(8, colour_primaries);
                writer.writeU(8, transfer_characteristics);
                writer.writeU(8, matrix_coeffs);
            }
        }

        writer.writeU1(chroma_loc_info_present);
        if (chroma_loc_info_present) {
            writer.writeUE(chroma_sample_loc_type_top_field);
            writer.writeUE(chroma_sample_loc_type_bottom_field);
        }

        writer.writeU1(neutral_chroma_indication);
        writer.writeU1(field_seq);
        writer.writeU1(frame_field_info_present);
        writer.writeU1(default_display_window);
        if (default_display_window) {
            writer.writeUE(def_disp_win_left_offset);
            writer.writeUE(def_disp_win_right_offset);
            writer.writeUE(def_disp_win_top_offset);
            writer.writeUE(def_disp_win_bottom_offset);
        }

        writer.writeU1(vui_timing_info_present);
        if (vui_timing_info_present) {
            writer.writeULong(32, vui_num_units_in_tick);
            writer.writeULong(32, vui_time_scale);

            writer.writeU1(vui_poc_proportional_to_timing);
            if (vui_poc_proportional_to_timing) {
                writer.writeUE(vui_num_ticks_poc_diff_one_minus1);
            }

            writer.writeU1(vui_hrd_parameters_present);
            if (vui_hrd_parameters_present) {
                hrd_parameters.write(writer);
            }
        }

        writer.writeU1(bitstream_restriction);
        if (bitstream_restriction) {
            writer.writeU1(tiles_fixed_structure);
            writer.writeU1(motion_vectors_over_pic_boundaries);
            writer.writeU1(restricted_ref_pic_lists);
            writer.writeUE(min_spatial_segmentation_idc);
            writer.writeUE(max_bytes_per_pic_denom);
            writer.writeUE(max_bits_per_min_cu_denom);
            writer.writeUE(log2_max_mv_length_horizontal);
            writer.writeUE(log2_max_mv_length_vertical);
        }
    }

    @Override
    public void print(PrintStream ps) {
        ps.print("    aspect_ratio_info_present: ");
        ps.println(aspect_ratio_info_present);
        if (aspect_ratio_info_present) {
            ps.print("      aspect_ratio_idc: ");
            ps.println(aspect_ratio_idc);
            if (aspect_ratio_idc == 255) { // TODO EXTENDED_SAR constant
                ps.print("        sar_width: ");
                ps.println(sar_width);
                ps.print("        sar_height: ");
                ps.println(sar_height);
            }
        }

        ps.print("    overscan_info_present: ");
        ps.println(overscan_info_present);
        if (overscan_info_present) {
            ps.print("      overscan_appropriate: ");
            ps.println(overscan_appropriate);
        }

        ps.print("    video_signal_type_present: ");
        ps.println(video_signal_type_present);
        if (video_signal_type_present) {
            ps.print("      video_format: ");
            ps.println(video_format);
            ps.print("      video_full_range: ");
            ps.println(video_full_range);
            ps.print("      colour_description_present: ");
            ps.println(colour_description_present);
            if (colour_description_present) {
                ps.print("        colour_primaries: ");
                ps.println(colour_primaries);
                ps.print("        transfer_characteristics: ");
                ps.println(transfer_characteristics);
                ps.print("        matrix_coeffs: ");
                ps.println(matrix_coeffs);
            }
        }

        ps.println("    chroma_loc_info_present: ");
        ps.println(chroma_loc_info_present);
        if (chroma_loc_info_present) {
            ps.print("      chroma_sample_loc_type_top_field: ");
            ps.println(chroma_sample_loc_type_top_field);
            ps.print("      chroma_sample_loc_type_bottom_field: ");
            ps.println(chroma_sample_loc_type_bottom_field);
        }

        ps.print("    neutral_chroma_indication: ");
        ps.println(neutral_chroma_indication);
        ps.print("    field_seq: ");
        ps.println(field_seq);
        ps.print("    frame_field_info_present: ");
        ps.println(frame_field_info_present);
        ps.print("    default_display_window: ");
        ps.println(default_display_window);
        if (default_display_window) {
            ps.print("      def_disp_win_left_offset: ");
            ps.println(def_disp_win_left_offset);
            ps.print("      def_disp_win_right_offset: ");
            ps.println(def_disp_win_right_offset);
            ps.print("      def_disp_win_top_offset: ");
            ps.println(def_disp_win_top_offset);
            ps.print("      def_disp_win_bottom_offset: ");
            ps.println(def_disp_win_bottom_offset);
        }

        ps.print("    vui_timing_info_present: ");
        ps.println(vui_timing_info_present);
        if (vui_timing_info_present) {
            ps.print("      vui_num_units_in_tick: ");
            ps.println(vui_num_units_in_tick);
            ps.print("      vui_time_scale: ");
            ps.println(vui_time_scale);

            ps.print("      vui_poc_proportional_to_timing: ");
            ps.println(vui_poc_proportional_to_timing);
            if (vui_poc_proportional_to_timing) {
                ps.print("        vui_num_ticks_poc_diff_one_minus1: ");
                ps.println(vui_num_ticks_poc_diff_one_minus1);
            }

            ps.print("      vui_hrd_parameters_present: ");
            ps.println(vui_hrd_parameters_present);
            if (vui_hrd_parameters_present) {
                hrd_parameters.print(ps);
            }
        }

        ps.print("    bitstream_restriction: ");
        ps.println(bitstream_restriction);
        if (bitstream_restriction) {
            ps.print("      tiles_fixed_structure: ");
            ps.println(tiles_fixed_structure);
            ps.print("      motion_vectors_over_pic_boundaries: ");
            ps.println(motion_vectors_over_pic_boundaries);
            ps.print("      restricted_ref_pic_lists: ");
            ps.println(restricted_ref_pic_lists);
            ps.print("      min_spatial_segmentation_idc: ");
            ps.println(min_spatial_segmentation_idc);
            ps.print("      max_bytes_per_pic_denom: ");
            ps.println(max_bytes_per_pic_denom);
            ps.print("      max_bits_per_min_cu_denom: ");
            ps.println(max_bits_per_min_cu_denom);
            ps.print("      log2_max_mv_length_horizontal: ");
            ps.println(log2_max_mv_length_horizontal);
            ps.print("      log2_max_mv_length_vertical: ");
            ps.println(log2_max_mv_length_vertical);
        }
    }
}
