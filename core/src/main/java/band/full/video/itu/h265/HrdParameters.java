package band.full.video.itu.h265;

import static java.util.Arrays.setAll;

import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

import java.io.PrintStream;

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
        public void read(H265Context context, RbspReader reader) {
            nal_hrd_parameters_present = reader.readU1();
            vcl_hrd_parameters_present = reader.readU1();

            if (nal_hrd_parameters_present || vcl_hrd_parameters_present) {
                sub_pic_hrd_params_present = reader.readU1();

                if (sub_pic_hrd_params_present) {
                    tick_divisor_minus2 = reader.readUShort(8);
                    du_cpb_removal_delay_increment_length_minus1 =
                            reader.readUByte(5);
                    sub_pic_cpb_params_in_pic_timing_sei = reader.readU1();
                    dpb_output_delay_du_length_minus1 = reader.readUByte(5);
                }

                bit_rate_scale = reader.readUByte(4);
                cpb_size_scale = reader.readUByte(4);

                if (sub_pic_hrd_params_present) {
                    cpb_size_du_scale = reader.readUByte(4);
                }

                initial_cpb_removal_delay_length_minus1 = reader.readUByte(5);
                au_cpb_removal_delay_length_minus1 = reader.readUByte(5);
                dpb_output_delay_length_minus1 = reader.readUByte(5);
            }
        }

        @Override
        public void write(H265Context context, RbspWriter writer) {
            writer.writeU1(nal_hrd_parameters_present);
            writer.writeU1(vcl_hrd_parameters_present);

            if (nal_hrd_parameters_present || vcl_hrd_parameters_present) {
                writer.writeU1(sub_pic_hrd_params_present);

                if (sub_pic_hrd_params_present) {
                    writer.writeU(8, tick_divisor_minus2);
                    writer.writeU(5,
                            du_cpb_removal_delay_increment_length_minus1);
                    writer.writeU1(sub_pic_cpb_params_in_pic_timing_sei);
                    writer.writeU(5, dpb_output_delay_du_length_minus1);
                }

                writer.writeU(4, bit_rate_scale);
                writer.writeU(4, cpb_size_scale);

                if (sub_pic_hrd_params_present) {
                    writer.writeU(4, cpb_size_du_scale);
                }

                writer.writeU(5, initial_cpb_removal_delay_length_minus1);
                writer.writeU(5, au_cpb_removal_delay_length_minus1);
                writer.writeU(5, dpb_output_delay_length_minus1);
            }
        }

        @Override
        public void print(H265Context context, PrintStream ps) {
            ps.print("          nal_hrd_parameters_present: ");
            ps.println(nal_hrd_parameters_present);
            ps.print("          vcl_hrd_parameters_present: ");
            ps.println(vcl_hrd_parameters_present);

            if (nal_hrd_parameters_present || vcl_hrd_parameters_present) {
                ps.print("          sub_pic_hrd_params_present: ");
                ps.println(sub_pic_hrd_params_present);

                if (sub_pic_hrd_params_present) {
                    ps.print("          tick_divisor_minus2: ");
                    ps.println(tick_divisor_minus2);
                    ps.print(
                            "          du_cpb_removal_delay_increment_length_minus1: ");
                    ps.println(du_cpb_removal_delay_increment_length_minus1);
                    ps.print(
                            "          sub_pic_cpb_params_in_pic_timing_sei: ");
                    ps.println(sub_pic_cpb_params_in_pic_timing_sei);
                    ps.print("          dpb_output_delay_du_length_minus1: ");
                    ps.println(dpb_output_delay_du_length_minus1);
                }

                ps.print("          bit_rate_scale: ");
                ps.println(bit_rate_scale);
                ps.print("          cpb_size_scale: ");
                ps.println(cpb_size_scale);

                if (sub_pic_hrd_params_present) {
                    ps.print("          cpb_size_du_scale: ");
                    ps.println(cpb_size_du_scale);
                }

                ps.print("          initial_cpb_removal_delay_length_minus1: ");
                ps.println(initial_cpb_removal_delay_length_minus1);
                ps.print("          au_cpb_removal_delay_length_minus1: ");
                ps.println(au_cpb_removal_delay_length_minus1);
                ps.print("          dpb_output_delay_length_minus1: ");
                ps.println(dpb_output_delay_length_minus1);
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
        public void read(H265Context context, RbspReader reader) {
            fixed_pic_rate_general = reader.readU1();
            fixed_pic_rate_within_cvs = fixed_pic_rate_general
                    || reader.readU1();
            if (fixed_pic_rate_within_cvs) {
                elemental_duration_in_tc_minus1 = reader.readUE();
            } else {
                low_delay_hrd = reader.readU1();
            }
            if (!low_delay_hrd) {
                cpb_cnt_minus1 = reader.readUE();
            }

            int CbpCnt = cpb_cnt_minus1 + 1;

            if (common_inf.nal_hrd_parameters_present) {
                nal_hrd_parameters = new SubLayerParameters[CbpCnt];
                setAll(nal_hrd_parameters, i -> new SubLayerParameters());
                for (SubLayerParameters nhp : nal_hrd_parameters) {
                    nhp.read(context, reader);
                }
            }

            if (common_inf.vcl_hrd_parameters_present) {
                vcl_hrd_parameters = new SubLayerParameters[CbpCnt];
                setAll(vcl_hrd_parameters, i -> new SubLayerParameters());
                for (SubLayerParameters vhp : vcl_hrd_parameters) {
                    vhp.read(context, reader);
                }
            }
        }

        @Override
        public void write(H265Context context, RbspWriter writer) {
            writer.writeU1(fixed_pic_rate_general);
            if (fixed_pic_rate_general) {
                if (!fixed_pic_rate_within_cvs)
                    throw new IllegalStateException();
            } else {
                writer.writeU1(fixed_pic_rate_within_cvs);
            }
            if (fixed_pic_rate_within_cvs) {
                writer.writeUE(elemental_duration_in_tc_minus1);
            } else {
                writer.writeU1(low_delay_hrd);
            }
            if (!low_delay_hrd) {
                writer.writeUE(cpb_cnt_minus1);
            }

            int CbpCnt = cpb_cnt_minus1 + 1;

            if (common_inf.nal_hrd_parameters_present) {
                if (nal_hrd_parameters.length != CbpCnt)
                    throw new IllegalStateException();

                for (SubLayerParameters nhp : nal_hrd_parameters) {
                    nhp.write(context, writer);
                }
            }

            if (common_inf.vcl_hrd_parameters_present) {
                if (vcl_hrd_parameters.length != CbpCnt)
                    throw new IllegalStateException();

                for (SubLayerParameters vhp : vcl_hrd_parameters) {
                    vhp.write(context, writer);
                }
            }
        }

        @Override
        public void print(H265Context context, PrintStream ps) {
            ps.print("          fixed_pic_rate_general: ");
            ps.println(fixed_pic_rate_general);
            if (!fixed_pic_rate_general) {
                ps.print("          fixed_pic_rate_within_cvs: ");
                ps.println(fixed_pic_rate_within_cvs);
            }
            if (fixed_pic_rate_within_cvs) {
                ps.print("          elemental_duration_in_tc_minus1: ");
                ps.println(elemental_duration_in_tc_minus1);
            } else {
                ps.print("          low_delay_hrd: ");
                ps.println(low_delay_hrd);
            }
            if (!low_delay_hrd) {
                ps.print("          cpb_cnt_minus1: ");
                ps.println(cpb_cnt_minus1);
            }

            if (common_inf.nal_hrd_parameters_present) {
                ps.println("          nal_hrd_parameters");
                for (SubLayerParameters nhp : nal_hrd_parameters) {
                    nhp.print(context, ps);
                }
            }

            if (common_inf.vcl_hrd_parameters_present) {
                ps.print("          vcl_hrd_parameters");
                for (SubLayerParameters vhp : vcl_hrd_parameters) {
                    vhp.print(context, ps);
                }
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
        public void read(H265Context context, RbspReader reader) {
            bit_rate_value_minus1 = reader.readUE();
            cpb_size_value_minus1 = reader.readUE();

            if (common_inf.sub_pic_hrd_params_present) {
                cpb_size_du_value_minus1 = reader.readUE();
                bit_rate_du_value_minus1 = reader.readUE();
            }

            cbr = reader.readU1();
        }

        @Override
        public void write(H265Context context, RbspWriter writer) {
            writer.writeUE(bit_rate_value_minus1);
            writer.writeUE(cpb_size_value_minus1);

            if (common_inf.sub_pic_hrd_params_present) {
                writer.writeUE(cpb_size_du_value_minus1);
                writer.writeUE(bit_rate_du_value_minus1);
            }

            writer.writeU1(cbr);
        }

        @Override
        public void print(H265Context context, PrintStream ps) {
            ps.print("            bit_rate_value_minus1: ");
            ps.println(bit_rate_value_minus1);
            ps.print("            cpb_size_value_minus1: ");
            ps.println(cpb_size_value_minus1);

            if (common_inf.sub_pic_hrd_params_present) {
                ps.print("            cpb_size_du_value_minus1: ");
                ps.println(cpb_size_du_value_minus1);
                ps.print("            bit_rate_du_value_minus1: ");
                ps.println(bit_rate_du_value_minus1);
            }

            ps.print("            cbr: ");
            ps.println(cbr);
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
    public void read(H265Context context, RbspReader reader) {
        if (commonInfPresentFlag) {
            common_inf.read(context, reader);
        }

        for (SubLayer sl : sub_layers) {
            sl.read(context, reader);
        }
    }

    @Override
    public void write(H265Context context, RbspWriter writer) {
        if (commonInfPresentFlag) {
            common_inf.write(context, writer);
        }

        for (SubLayer sl : sub_layers) {
            sl.write(context, writer);
        }
    }

    @Override
    public void print(H265Context context, PrintStream ps) {
        ps.println("        hrd_parameters");
        if (commonInfPresentFlag) {
            common_inf.print(context, ps);
        }

        for (int i = 0; i < sub_layers.length; i++) {
            ps.print("        sub_layer_");
            ps.println(i);
            sub_layers[i].print(context, ps);
        }
    }
}
