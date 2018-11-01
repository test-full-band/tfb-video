package band.full.video.itu.h265.sei;

import static band.full.video.itu.nal.RbspWriter.countUEbits;

import band.full.video.itu.h265.H265Context;
import band.full.video.itu.h265.HrdParameters.CommonInf;
import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

import java.io.PrintStream;

/**
 * @author Igor Malinin
 */
public class PicTiming implements Payload<H265Context> {
    public byte pic_struct;
    public byte source_scan_type;
    public boolean duplicate_flag;

    public int au_cpb_removal_delay_minus1; // u(v)
    public int pic_dpb_output_delay; // u(v)

    public int pic_dpb_output_du_delay; // u(v)

    public int num_decoding_units_minus1;
    public boolean du_common_cpb_removal_delay_flag;
    public int du_common_cpb_removal_delay_increment_minus1; // u(v)

    public int[] num_nalus_in_du_minus1;
    public int[] du_cpb_removal_delay_increment_minus1; // u(v)

    public PicTiming() {}

    public PicTiming(H265Context context, RbspReader reader, int size) {
        int start = reader.available();
        read(context, reader);
        if (start - reader.available() != size << 3)
            throw new IllegalArgumentException();
    }

    @Override
    public int size(H265Context context) {
        int bits = 0;

        if (context.sps.vui_parameters.frame_field_info_present) {
            bits += 7;
        }

        CommonInf hrd = context.sps.vui_parameters.hrd_parameters.common_inf;

        if (hrd.nal_hrd_parameters_present || hrd.vcl_hrd_parameters_present) {
            bits += hrd.au_cpb_removal_delay_length_minus1 + 1;
            bits += hrd.dpb_output_delay_length_minus1 + 1;

            if (hrd.sub_pic_hrd_params_present) {
                bits += hrd.dpb_output_delay_du_length_minus1 + 1;

                if (hrd.sub_pic_cpb_params_in_pic_timing_sei) {
                    bits += countUEbits(num_decoding_units_minus1) + 1;

                    if (du_common_cpb_removal_delay_flag) {
                        bits += hrd.du_cpb_removal_delay_increment_length_minus1
                                + 1;
                    }

                    for (int i = 0; i <= num_decoding_units_minus1; i++) {
                        bits += countUEbits(num_nalus_in_du_minus1[i]) + 1;

                        if (!du_common_cpb_removal_delay_flag
                                && i < num_decoding_units_minus1) {
                            bits += countUEbits(num_nalus_in_du_minus1[i]) + 1;
                            bits += hrd.du_cpb_removal_delay_increment_length_minus1
                                    + 1;
                        }
                    }
                }
            }
        }

        return bits + 7 >> 3;
    }

    @Override
    public void read(H265Context context, RbspReader reader) {
        if (context.sps.vui_parameters.frame_field_info_present) {
            pic_struct = reader.readUByte(4);
            source_scan_type = reader.readUByte(2);
            duplicate_flag = reader.readU1();
        }

        CommonInf hrd = context.sps.vui_parameters.hrd_parameters.common_inf;

        if (hrd.nal_hrd_parameters_present || hrd.vcl_hrd_parameters_present) {
            au_cpb_removal_delay_minus1 = reader.readUInt(
                    hrd.au_cpb_removal_delay_length_minus1 + 1);

            pic_dpb_output_delay = reader.readUInt(
                    hrd.dpb_output_delay_length_minus1 + 1);

            if (hrd.sub_pic_hrd_params_present) {
                pic_dpb_output_du_delay = reader.readUInt(
                        hrd.dpb_output_delay_du_length_minus1 + 1);

                if (hrd.sub_pic_cpb_params_in_pic_timing_sei) {
                    int bits = hrd.du_cpb_removal_delay_increment_length_minus1
                            + 1;

                    num_decoding_units_minus1 = reader.readUE();
                    du_common_cpb_removal_delay_flag = reader.readU1();
                    if (du_common_cpb_removal_delay_flag) {
                        du_common_cpb_removal_delay_increment_minus1 =
                                reader.readUInt(bits);
                    }

                    for (int i = 0; i <= num_decoding_units_minus1; i++) {
                        num_nalus_in_du_minus1[i] = reader.readUE();

                        if (!du_common_cpb_removal_delay_flag
                                && i < num_decoding_units_minus1) {
                            du_cpb_removal_delay_increment_minus1[i] =
                                    reader.readUInt(bits);
                        }
                    }
                }
            }
        }

        // if (!reader.readU1()) throw new IllegalStateException();
        while (!reader.isByteAligned())
            if (reader.readU1()) throw new IllegalStateException();
    }

    @Override
    public void write(H265Context context, RbspWriter writer) {
        if (context.sps.vui_parameters.frame_field_info_present) {
            writer.writeU(4, pic_struct);
            writer.writeU(2, source_scan_type);
            writer.writeU1(duplicate_flag);
        }

        CommonInf hrd = context.sps.vui_parameters.hrd_parameters.common_inf;

        if (hrd.nal_hrd_parameters_present || hrd.vcl_hrd_parameters_present) {
            writer.writeU(hrd.au_cpb_removal_delay_length_minus1 + 1,
                    au_cpb_removal_delay_minus1);

            writer.writeU(hrd.dpb_output_delay_length_minus1 + 1,
                    pic_dpb_output_delay);

            if (hrd.sub_pic_hrd_params_present) {
                writer.writeU(hrd.dpb_output_delay_du_length_minus1 + 1,
                        pic_dpb_output_du_delay);

                if (hrd.sub_pic_cpb_params_in_pic_timing_sei) {
                    int bits = hrd.du_cpb_removal_delay_increment_length_minus1
                            + 1;

                    writer.writeUE(num_decoding_units_minus1);
                    writer.writeU1(du_common_cpb_removal_delay_flag);
                    if (du_common_cpb_removal_delay_flag) {
                        writer.writeU(bits,
                                du_common_cpb_removal_delay_increment_minus1);
                    }

                    for (int i = 0; i <= num_decoding_units_minus1; i++) {
                        writer.writeUE(num_nalus_in_du_minus1[i]);

                        if (!du_common_cpb_removal_delay_flag
                                && i < num_decoding_units_minus1) {
                            writer.writeU(bits,
                                    du_cpb_removal_delay_increment_minus1[i]);
                        }
                    }
                }
            }
        }

        writer.writeU1(true);
        while (!writer.isByteAligned()) {
            writer.writeU1(false);
        }
    }

    @Override
    public void print(H265Context context, PrintStream ps) {
        if (context.sps.vui_parameters.frame_field_info_present) {
            ps.println("      pic_struct: ");
            ps.println(pic_struct);
            ps.println("      source_scan_type: ");
            ps.println(source_scan_type);
            ps.println("      duplicate_flag: ");
            ps.println(duplicate_flag);
        }

        CommonInf hrd = context.sps.vui_parameters.hrd_parameters.common_inf;

        if (hrd.nal_hrd_parameters_present || hrd.vcl_hrd_parameters_present) {
            ps.print("      au_cpb_removal_delay_minus1: ");
            ps.println(au_cpb_removal_delay_minus1);
            ps.print("      pic_dpb_output_delay: ");
            ps.println(pic_dpb_output_delay);

            if (hrd.sub_pic_hrd_params_present) {
                ps.print("      pic_dpb_output_du_delay: ");
                ps.println(pic_dpb_output_du_delay);

                if (hrd.sub_pic_cpb_params_in_pic_timing_sei) {
                    ps.print("      num_decoding_units_minus1: ");
                    ps.println(num_decoding_units_minus1);
                    ps.print("      du_common_cpb_removal_delay_flag: ");
                    ps.println(du_common_cpb_removal_delay_flag);
                    if (du_common_cpb_removal_delay_flag) {
                        ps.print(
                                "      du_common_cpb_removal_delay_increment_minus1: ");
                        ps.println(
                                du_common_cpb_removal_delay_increment_minus1);
                    }

                    for (int i = 0; i <= num_decoding_units_minus1; i++) {
                        ps.print("        num_nalus_in_du_minus1: ");
                        ps.println(num_nalus_in_du_minus1[i]);

                        if (!du_common_cpb_removal_delay_flag
                                && i < num_decoding_units_minus1) {
                            ps.print(
                                    "        du_cpb_removal_delay_increment_minus1: ");
                            ps.println(
                                    du_cpb_removal_delay_increment_minus1[i]);
                        }
                    }
                }
            }
        }
    }
}
