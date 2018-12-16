package band.full.video.itu.h265;

import static java.util.Arrays.setAll;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

/**
 * E.2.2 HRD parameters syntax
 * <p>
 * <code>hrd_parameters(commonInfPresentFlag, maxNumSubLayersMinus1)</code>
 *
 * @author Igor Malinin
 */
public class HrdParameters implements Structure<H265Context> {
    public final boolean commonInfPresentFlag;
    public final byte maxNumSubLayersMinus1;

    public final CommonInf common_inf = new CommonInf();
    public final SubLayer[] sub_layers;

    public static class CommonInf implements Structure<H265Context> {
        public boolean nal_hrd_parameters_present;
        public boolean vcl_hrd_parameters_present;
        public boolean sub_pic_hrd_params_present;
        public short tick_divisor_minus2; // u8
        public byte du_cpb_removal_delay_increment_length_minus1; // u5
        public boolean sub_pic_cpb_params_in_pic_timing_sei; // u1
        public byte dpb_output_delay_du_length_minus1; // u5
        public byte bit_rate_scale; // u4
        public byte cpb_size_scale; // u4
        public byte cpb_size_du_scale; // u4
        public byte initial_cpb_removal_delay_length_minus1; // u5
        public byte au_cpb_removal_delay_length_minus1; // u5
        public byte dpb_output_delay_length_minus1; // u5

        @Override
        public void read(H265Context context, RbspReader in) {
            nal_hrd_parameters_present = in.u1();
            vcl_hrd_parameters_present = in.u1();

            if (nal_hrd_parameters_present || vcl_hrd_parameters_present) {
                sub_pic_hrd_params_present = in.u1();

                if (sub_pic_hrd_params_present) {
                    tick_divisor_minus2 = in.u8();
                    du_cpb_removal_delay_increment_length_minus1 = in.u5();
                    sub_pic_cpb_params_in_pic_timing_sei = in.u1();
                    dpb_output_delay_du_length_minus1 = in.u5();
                }

                bit_rate_scale = in.u4();
                cpb_size_scale = in.u4();

                if (sub_pic_hrd_params_present) {
                    cpb_size_du_scale = in.u4();
                }

                initial_cpb_removal_delay_length_minus1 = in.u5();
                au_cpb_removal_delay_length_minus1 = in.u5();
                dpb_output_delay_length_minus1 = in.u5();
            }
        }

        @Override
        public void write(H265Context context, RbspWriter out) {
            out.u1(nal_hrd_parameters_present);
            out.u1(vcl_hrd_parameters_present);

            if (nal_hrd_parameters_present || vcl_hrd_parameters_present) {
                out.u1(sub_pic_hrd_params_present);

                if (sub_pic_hrd_params_present) {
                    out.u8(tick_divisor_minus2);
                    out.u5(du_cpb_removal_delay_increment_length_minus1);
                    out.u1(sub_pic_cpb_params_in_pic_timing_sei);
                    out.u5(dpb_output_delay_du_length_minus1);
                }

                out.u4(bit_rate_scale);
                out.u4(cpb_size_scale);

                if (sub_pic_hrd_params_present) {
                    out.u4(cpb_size_du_scale);
                }

                out.u5(initial_cpb_removal_delay_length_minus1);
                out.u5(au_cpb_removal_delay_length_minus1);
                out.u5(dpb_output_delay_length_minus1);
            }
        }

        @Override
        public void print(H265Context context, RbspPrinter out) {
            out.u1("nal_hrd_parameters_present", nal_hrd_parameters_present);
            out.u1("vcl_hrd_parameters_present", vcl_hrd_parameters_present);

            if (nal_hrd_parameters_present || vcl_hrd_parameters_present) {
                out.u1("sub_pic_hrd_params_present",
                        sub_pic_hrd_params_present);

                if (sub_pic_hrd_params_present) {
                    out.u8("tick_divisor_minus2", tick_divisor_minus2);

                    out.u5("du_cpb_removal_delay_increment_length_minus1",
                            du_cpb_removal_delay_increment_length_minus1);

                    out.u1("sub_pic_cpb_params_in_pic_timing_sei",
                            sub_pic_cpb_params_in_pic_timing_sei);

                    out.u5("dpb_output_delay_du_length_minus1",
                            dpb_output_delay_du_length_minus1);
                }

                out.u4("bit_rate_scale", bit_rate_scale);
                out.u4("cpb_size_scale", cpb_size_scale);

                if (sub_pic_hrd_params_present) {
                    out.u4("cpb_size_du_scale", cpb_size_du_scale);
                }

                out.u5("initial_cpb_removal_delay_length_minus1",
                        initial_cpb_removal_delay_length_minus1);

                out.u5("au_cpb_removal_delay_length_minus1",
                        au_cpb_removal_delay_length_minus1);

                out.u5("dpb_output_delay_length_minus1",
                        dpb_output_delay_length_minus1);
            }
        }
    }

    public class SubLayer implements Structure<H265Context> {
        public boolean fixed_pic_rate_general;
        public boolean fixed_pic_rate_within_cvs;
        public int elemental_duration_in_tc_minus1;
        public boolean low_delay_hrd;
        /** 0..31 */
        public int cpb_cnt_minus1;

        public SubLayerParameters[] nal_hrd_parameters;
        public SubLayerParameters[] vcl_hrd_parameters;

        @Override
        public void read(H265Context context, RbspReader in) {
            fixed_pic_rate_general = in.u1();
            fixed_pic_rate_within_cvs = fixed_pic_rate_general || in.u1();
            if (fixed_pic_rate_within_cvs) {
                elemental_duration_in_tc_minus1 = in.ue();
            } else {
                low_delay_hrd = in.u1();
            }
            if (!low_delay_hrd) {
                cpb_cnt_minus1 = in.ue();
            }

            int CbpCnt = cpb_cnt_minus1 + 1;

            if (common_inf.nal_hrd_parameters_present) {
                nal_hrd_parameters = new SubLayerParameters[CbpCnt];
                setAll(nal_hrd_parameters, i -> new SubLayerParameters());
                for (SubLayerParameters nhp : nal_hrd_parameters) {
                    nhp.read(context, in);
                }
            }

            if (common_inf.vcl_hrd_parameters_present) {
                vcl_hrd_parameters = new SubLayerParameters[CbpCnt];
                setAll(vcl_hrd_parameters, i -> new SubLayerParameters());
                for (SubLayerParameters vhp : vcl_hrd_parameters) {
                    vhp.read(context, in);
                }
            }
        }

        @Override
        public void write(H265Context context, RbspWriter out) {
            out.u1(fixed_pic_rate_general);
            if (fixed_pic_rate_general) {
                if (!fixed_pic_rate_within_cvs)
                    throw new IllegalStateException();
            } else {
                out.u1(fixed_pic_rate_within_cvs);
            }
            if (fixed_pic_rate_within_cvs) {
                out.ue(elemental_duration_in_tc_minus1);
            } else {
                out.u1(low_delay_hrd);
            }
            if (!low_delay_hrd) {
                out.ue(cpb_cnt_minus1);
            }

            int CbpCnt = cpb_cnt_minus1 + 1;

            if (common_inf.nal_hrd_parameters_present) {
                if (nal_hrd_parameters.length != CbpCnt)
                    throw new IllegalStateException();

                for (SubLayerParameters nhp : nal_hrd_parameters) {
                    nhp.write(context, out);
                }
            }

            if (common_inf.vcl_hrd_parameters_present) {
                if (vcl_hrd_parameters.length != CbpCnt)
                    throw new IllegalStateException();

                for (SubLayerParameters vhp : vcl_hrd_parameters) {
                    vhp.write(context, out);
                }
            }
        }

        @Override
        public void print(H265Context context, RbspPrinter out) {
            out.u1("fixed_pic_rate_general", fixed_pic_rate_general);
            if (!fixed_pic_rate_general) {
                out.u1("fixed_pic_rate_within_cvs", fixed_pic_rate_within_cvs);
            }
            if (fixed_pic_rate_within_cvs) {
                out.ue("elemental_duration_in_tc_minus1",
                        elemental_duration_in_tc_minus1);
            } else {
                out.u1("low_delay_hrd", low_delay_hrd);
            }
            if (!low_delay_hrd) {
                out.ue("cpb_cnt_minus1", cpb_cnt_minus1);
            }

            if (common_inf.nal_hrd_parameters_present) {
                out.raw("nal_hrd_parameters");
                out.enter();

                for (SubLayerParameters nhp : nal_hrd_parameters) {
                    nhp.print(context, out);
                }

                out.leave();
            }

            if (common_inf.vcl_hrd_parameters_present) {
                out.raw("vcl_hrd_parameters");
                out.enter();

                for (SubLayerParameters vhp : vcl_hrd_parameters) {
                    vhp.print(context, out);
                }

                out.leave();
            }
        }
    }

    /**
     * E.2.3 Sub-layer HRD parameters syntax
     * <p>
     * <code>sub_layer_hrd_parameters(subLayerId)</code>
     */
    public class SubLayerParameters implements Structure<H265Context> {
        public int bit_rate_value_minus1;
        public int cpb_size_value_minus1;
        public int cpb_size_du_value_minus1;
        public int bit_rate_du_value_minus1;
        public boolean cbr;

        @Override
        public void read(H265Context context, RbspReader in) {
            bit_rate_value_minus1 = in.ue();
            cpb_size_value_minus1 = in.ue();

            if (common_inf.sub_pic_hrd_params_present) {
                cpb_size_du_value_minus1 = in.ue();
                bit_rate_du_value_minus1 = in.ue();
            }

            cbr = in.u1();
        }

        @Override
        public void write(H265Context context, RbspWriter out) {
            out.ue(bit_rate_value_minus1);
            out.ue(cpb_size_value_minus1);

            if (common_inf.sub_pic_hrd_params_present) {
                out.ue(cpb_size_du_value_minus1);
                out.ue(bit_rate_du_value_minus1);
            }

            out.u1(cbr);
        }

        @Override
        public void print(H265Context context, RbspPrinter out) {
            out.ue("bit_rate_value_minus1", bit_rate_value_minus1);
            out.ue("cpb_size_value_minus1", cpb_size_value_minus1);

            if (common_inf.sub_pic_hrd_params_present) {
                out.ue("cpb_size_du_value_minus1", cpb_size_du_value_minus1);
                out.ue("bit_rate_du_value_minus1", bit_rate_du_value_minus1);
            }

            out.u1("cbr", cbr);
        }
    }

    public HrdParameters(boolean commonInfPresentFlag,
            byte maxNumSubLayersMinus1) {
        this.commonInfPresentFlag = commonInfPresentFlag;
        this.maxNumSubLayersMinus1 = maxNumSubLayersMinus1;

        sub_layers = new SubLayer[maxNumSubLayersMinus1 + 1];
        setAll(sub_layers, i -> new SubLayer());
    }

    @Override
    public void read(H265Context context, RbspReader in) {
        if (commonInfPresentFlag) {
            common_inf.read(context, in);
        }

        for (SubLayer sl : sub_layers) {
            sl.read(context, in);
        }
    }

    @Override
    public void write(H265Context context, RbspWriter out) {
        if (commonInfPresentFlag) {
            common_inf.write(context, out);
        }

        for (SubLayer sl : sub_layers) {
            sl.write(context, out);
        }
    }

    @Override
    public void print(H265Context context, RbspPrinter out) {
        out.raw("hrd_parameters"); // TODO
        if (commonInfPresentFlag) {
            out.enter();
            common_inf.print(context, out);
            out.leave();
        }

        for (int i = 0; i < sub_layers.length; i++) {
            out.i32("sub_layer", i);

            out.enter();
            sub_layers[i].print(context, out);
            out.leave();
        }
    }
}
