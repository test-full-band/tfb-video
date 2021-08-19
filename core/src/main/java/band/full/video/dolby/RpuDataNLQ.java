package band.full.video.dolby;

import static band.full.video.dolby.RpuData.NUM_CMPS;
import static java.util.Arrays.setAll;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

/**
 * rpu_data_mapping(), rpu_data_mapping_param()
 */
public class RpuDataNLQ extends VdrRpuDataPayload {
    public static final int NLQ_LINEAR_DZ = 0;
    public static final int NLQ_MU_LAW = 1;

    public static final int NLQ_NUM_PIVOTS = 2;

    public static class Param {
        public int nlq_offset; // u(v)
        public int f_vdr_in_max; // ue(v) u(v) | f(32)
        public int f_linear_deadzone_slope; // ue(v) u(v) | f(32)
        public int f_linear_deadzone_threshold; // ue(v) u(v) | f(32)
    }

    public boolean[][] nlq_param_pred; // u(1)

    // ue(v)
    public int[][] diff_pred_part_idx_nlq_minus1;

    public byte[][] num_nlq_param_predictors;
    public Param[][] rpu_data_nlq_param;

    @Override
    public void read(RpuHeader header, RbspReader in) {
        int end = NLQ_NUM_PIVOTS - 1;
        nlq_param_pred = new boolean[end][3];
        diff_pred_part_idx_nlq_minus1 = new int[end][3];
        num_nlq_param_predictors = new byte[end][3];
        rpu_data_nlq_param = new Param[end][3];
        for (Param[] p : rpu_data_nlq_param) {
            setAll(p, i -> new Param());
        }

        for (int i = 0; i < end; i++) {
            for (int c = 0; c < NUM_CMPS; c++) {
                if (num_nlq_param_predictors[i][c] > 0) {
                    nlq_param_pred[i][c] = in.u1();
                } else {
                    nlq_param_pred[i][c] = false;
                }

                if (!nlq_param_pred[i][c]) {
                    ++num_nlq_param_predictors[i][c];
                }

                if (!nlq_param_pred[i][c]) {
                    readParam(header, in, i, c);
                } else if (num_nlq_param_predictors[i][c] > 1) {
                    diff_pred_part_idx_nlq_minus1[i][c] = in.ue();
                }
            } // pivot idx
        } // cmp
    }

    private void readParam(RpuHeader header, RbspReader in, int i, int c) {
        Param param = rpu_data_nlq_param[i][c];

        param.nlq_offset = in.readUShort(header.EL_bit_depth_minus8 + 8);

        param.f_vdr_in_max = readCoefU(header, in);

        switch (header.nlq_method_idc) {
            case NLQ_LINEAR_DZ -> {
                param.f_linear_deadzone_slope = readCoefU(header, in);
                param.f_linear_deadzone_threshold = readCoefU(header, in);
            }

            default -> throw new IllegalStateException(
                    "nlq_method_idc: " + header.nlq_method_idc);
        }
    }

    @Override
    public void write(RpuHeader header, RbspWriter out) {
        int end = NLQ_NUM_PIVOTS - 1;
        num_nlq_param_predictors = new byte[end][3];
        for (int i = 0; i < end; i++) {
            for (int c = 0; c < NUM_CMPS; c++) {
                if (num_nlq_param_predictors[i][c] > 0) {
                    out.u1(nlq_param_pred[i][c]);
                } else {
                    nlq_param_pred[i][c] = false;
                }

                if (!nlq_param_pred[i][c]) {
                    ++num_nlq_param_predictors[i][c];
                }

                if (!nlq_param_pred[i][c]) {
                    writeParam(header, out, i, c);
                } else if (num_nlq_param_predictors[i][c] > 1) {
                    out.ue(diff_pred_part_idx_nlq_minus1[i][c]);
                }
            } // pivot idx
        } // cmp
    }

    private void writeParam(RpuHeader header, RbspWriter out, int i, int c) {
        Param param = rpu_data_nlq_param[i][c];

        out.u(header.EL_bit_depth_minus8 + 8, param.nlq_offset);

        writeCoefU(header, out, param.f_vdr_in_max);

        switch (header.nlq_method_idc) {
            case NLQ_LINEAR_DZ -> {
                writeCoefU(header, out, param.f_linear_deadzone_slope);
                writeCoefU(header, out, param.f_linear_deadzone_threshold);
            }

            default -> throw new IllegalStateException(
                    "nlq_method_idc: " + header.nlq_method_idc);
        }
    }

    @Override
    public void print(RpuHeader header, RbspPrinter out) {
        int end = NLQ_NUM_PIVOTS - 1;
        num_nlq_param_predictors = new byte[end][3];
        for (int i = 0; i < end; i++) {
            out.i32("idx", i);
            out.enter();

            for (int c = 0; c < NUM_CMPS; c++) {
                out.i32("cmp", c);
                out.enter();

                if (num_nlq_param_predictors[i][c] > 0) {
                    out.u1("nlq_param_pred", nlq_param_pred[i][c]);
                } else {
                    nlq_param_pred[i][c] = false;
                }

                if (!nlq_param_pred[i][c]) {
                    ++num_nlq_param_predictors[i][c];
                }

                if (!nlq_param_pred[i][c]) {
                    out.enter();
                    printParam(header, out, i, c);
                    out.leave();
                } else if (num_nlq_param_predictors[i][c] > 1) {
                    out.ue("diff_pred_part_idx_nlq_minus1",
                            diff_pred_part_idx_nlq_minus1[i][c]);
                }

                out.leave();
            } // pivot idx

            out.leave();
        } // cmp
    }

    private void printParam(RpuHeader header, RbspPrinter out, int i, int c) {
        Param param = rpu_data_nlq_param[i][c];

        out.printU("nlq_offset", header.EL_bit_depth_minus8 + 8,
                param.nlq_offset);

        printCoefU(header, out, "vdr_in_max_int", param.f_vdr_in_max);

        switch (header.nlq_method_idc) {
            case NLQ_LINEAR_DZ -> {
                printCoefU(header, out, "linear_deadzone_slope",
                        param.f_linear_deadzone_slope);

                printCoefU(header, out, "linear_deadzone_threshold",
                        param.f_linear_deadzone_threshold);
            }

            default -> throw new IllegalStateException(
                    "nlq_method_idc: " + header.nlq_method_idc);
        }
    }
}
