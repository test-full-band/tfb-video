package band.full.video.itu.h265.sei;

import static band.full.video.itu.nal.RbspWriter.countUEbits;

import band.full.video.itu.h265.H265Context;
import band.full.video.itu.h265.HrdParameters.CommonInf;
import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

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

    public PicTiming(H265Context context, RbspReader in, int size) {
        int start = in.available();
        read(context, in);
        if (start - in.available() != size << 3)
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
    public void read(H265Context context, RbspReader in) {
        if (context.sps.vui_parameters.frame_field_info_present) {
            pic_struct = in.u4();
            source_scan_type = in.u2();
            duplicate_flag = in.u1();
        }

        CommonInf hrd = context.sps.vui_parameters.hrd_parameters.common_inf;

        if (hrd.nal_hrd_parameters_present || hrd.vcl_hrd_parameters_present) {
            au_cpb_removal_delay_minus1 = in.readUInt(
                    hrd.au_cpb_removal_delay_length_minus1 + 1);

            pic_dpb_output_delay = in.readUInt(
                    hrd.dpb_output_delay_length_minus1 + 1);

            if (hrd.sub_pic_hrd_params_present) {
                pic_dpb_output_du_delay = in.readUInt(
                        hrd.dpb_output_delay_du_length_minus1 + 1);

                if (hrd.sub_pic_cpb_params_in_pic_timing_sei) {
                    int bits = hrd.du_cpb_removal_delay_increment_length_minus1
                            + 1;

                    num_decoding_units_minus1 = in.ue();
                    du_common_cpb_removal_delay_flag = in.u1();
                    if (du_common_cpb_removal_delay_flag) {
                        du_common_cpb_removal_delay_increment_minus1 =
                                in.readUInt(bits);
                    }

                    for (int i = 0; i <= num_decoding_units_minus1; i++) {
                        num_nalus_in_du_minus1[i] = in.ue();

                        if (!du_common_cpb_removal_delay_flag
                                && i < num_decoding_units_minus1) {
                            du_cpb_removal_delay_increment_minus1[i] =
                                    in.readUInt(bits);
                        }
                    }
                }
            }
        }

        if (in.isByteAligned()) return;

        if (!in.u1()) throw new IllegalStateException();
        while (!in.isByteAligned())
            if (in.u1()) throw new IllegalStateException();
    }

    @Override
    public void write(H265Context context, RbspWriter out) {
        if (context.sps.vui_parameters.frame_field_info_present) {
            out.u4(pic_struct);
            out.u2(source_scan_type);
            out.u1(duplicate_flag);
        }

        CommonInf hrd = context.sps.vui_parameters.hrd_parameters.common_inf;

        if (hrd.nal_hrd_parameters_present || hrd.vcl_hrd_parameters_present) {
            out.u(hrd.au_cpb_removal_delay_length_minus1 + 1,
                    au_cpb_removal_delay_minus1);

            out.u(hrd.dpb_output_delay_length_minus1 + 1,
                    pic_dpb_output_delay);

            if (hrd.sub_pic_hrd_params_present) {
                out.u(hrd.dpb_output_delay_du_length_minus1 + 1,
                        pic_dpb_output_du_delay);

                if (hrd.sub_pic_cpb_params_in_pic_timing_sei) {
                    int bits = hrd.du_cpb_removal_delay_increment_length_minus1
                            + 1;

                    out.ue(num_decoding_units_minus1);
                    out.u1(du_common_cpb_removal_delay_flag);
                    if (du_common_cpb_removal_delay_flag) {
                        out.u(bits,
                                du_common_cpb_removal_delay_increment_minus1);
                    }

                    for (int i = 0; i <= num_decoding_units_minus1; i++) {
                        out.ue(num_nalus_in_du_minus1[i]);

                        if (!du_common_cpb_removal_delay_flag
                                && i < num_decoding_units_minus1) {
                            out.u(bits,
                                    du_cpb_removal_delay_increment_minus1[i]);
                        }
                    }
                }
            }
        }

        if (out.isByteAligned()) return;

        out.u1(true);
        while (!out.isByteAligned()) {
            out.u1(false);
        }
    }

    @Override
    public void print(H265Context context, RbspPrinter out) {
        if (context.sps.vui_parameters.frame_field_info_present) {
            out.u4("pic_struct", pic_struct);
            out.u2("source_scan_type", source_scan_type);
            out.u1("duplicate_flag", duplicate_flag);
        }

        CommonInf hrd = context.sps.vui_parameters.hrd_parameters.common_inf;

        if (hrd.nal_hrd_parameters_present || hrd.vcl_hrd_parameters_present) {
            out.printU("au_cpb_removal_delay_minus1",
                    hrd.au_cpb_removal_delay_length_minus1 + 1,
                    au_cpb_removal_delay_minus1);

            out.printU("pic_dpb_output_delay",
                    hrd.dpb_output_delay_length_minus1 + 1,
                    pic_dpb_output_delay);

            if (hrd.sub_pic_hrd_params_present) {
                out.printU("pic_dpb_output_du_delay",
                        hrd.dpb_output_delay_du_length_minus1 + 1,
                        pic_dpb_output_du_delay);

                if (hrd.sub_pic_cpb_params_in_pic_timing_sei) {
                    int bits = hrd.du_cpb_removal_delay_increment_length_minus1
                            + 1;

                    out.ue("num_decoding_units_minus1",
                            num_decoding_units_minus1);
                    out.u1("du_common_cpb_removal_delay_flag",
                            du_common_cpb_removal_delay_flag);
                    if (du_common_cpb_removal_delay_flag) {
                        out.printU(
                                "du_common_cpb_removal_delay_increment_minus1",
                                bits,
                                du_common_cpb_removal_delay_increment_minus1);
                    }

                    out.enter();
                    for (int i = 0; i <= num_decoding_units_minus1; i++) {
                        out.ue("num_nalus_in_du_minus1",
                                num_nalus_in_du_minus1[i]);

                        if (!du_common_cpb_removal_delay_flag
                                && i < num_decoding_units_minus1) {
                            out.printU("du_cpb_removal_delay_increment_minus1",
                                    bits,
                                    du_cpb_removal_delay_increment_minus1[i]);
                        }
                    }
                    out.leave();
                }
            }
        }
    }
}
