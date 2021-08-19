package band.full.video.dolby;

import static band.full.video.dolby.RpuData.NUM_CMPS;
import static java.util.Arrays.setAll;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

/**
 * rpu_data_mapping(), rpu_data_mapping_param()
 */
public class RpuDataMapping extends VdrRpuDataPayload {
    public static final int MAPPING_POLYNOMIAL = 0;
    public static final int MAPPING_MMR = 1;

    public static class Param {
        public int poly_order_minus1; // ue(v)
        public boolean linear_interp_flag; // u(1)

        /**
         * <p>
         * If <code>coefficient_data_type</code> is equal to 0, the length of
         * the <code>pred_linear_interp_value[y][x][cmp][idx]</code> syntax
         * element is <code>coefficient_log2_denom</code> bits. If
         * <code>coefficient_data_type</code> is equal to 1, the length of the
         * <code>pred_linear_interp_value[y][x][cmp][idx]</code> syntax element
         * is 32 bits.
         *
         * @serial ue(v) u(v) | f(32)
         */
        public int f_pred_linear_interp_value; // ue(v) u(v)

        public int f_poly_coef[]; // se(v) u(v) | f(32)

        public byte mmr_order_minus1; // u(2)

        public int f_mmr_constant; // se(v) u(v) | f(32)

        public int f_mmr_coef[]; // se(v) u(v) | f(32)
    }

    public int[][] mapping_idc = new int[NUM_CMPS][]; // ue(v)
    public boolean[][] mapping_param_pred = new boolean[NUM_CMPS][]; // u(1)

    // ue(v)
    public int[][] diff_pred_part_idx_mapping_min = new int[NUM_CMPS][];

    public byte[][] num_mapping_param_predictors = new byte[NUM_CMPS][];
    public Param[][] rpu_data_mapp_ng_param = new Param[NUM_CMPS][];

    @Override
    public void read(RpuHeader header, RbspReader in) {
        for (int c = 0; c < NUM_CMPS; c++) {
            int end = header.pred_pivot_value[c].length - 1;
            mapping_idc[c] = new int[end];
            mapping_param_pred[c] = new boolean[end];
            diff_pred_part_idx_mapping_min[c] = new int[end];
            num_mapping_param_predictors[c] = new byte[end];
            rpu_data_mapp_ng_param[c] = new Param[end + 1];
            setAll(rpu_data_mapp_ng_param[c], i -> new Param());
            for (int i = 0; i < end; i++) {
                mapping_idc[c][i] = in.ue();

                if (num_mapping_param_predictors[c][i] > 0) {
                    mapping_param_pred[c][i] = in.u1();
                } else {
                    mapping_param_pred[c][i] = false;
                }

                if (!mapping_param_pred[c][i]) {
                    ++num_mapping_param_predictors[c][i];
                }

                if (!mapping_param_pred[c][i]) {
                    readParam(header, in, c, i);
                } else if (num_mapping_param_predictors[c][i] > 1) {
                    diff_pred_part_idx_mapping_min[c][i] = in.ue();
                }
            } // pivot idx
        } // cmp
    }

    private void readParam(RpuHeader header, RbspReader in, int c, int i) {
        Param param = rpu_data_mapp_ng_param[c][i];

        switch (mapping_idc[c][i]) {
            case MAPPING_POLYNOMIAL -> {
                param.poly_order_minus1 = in.ue();
                if (param.poly_order_minus1 == 0) {
                    param.linear_interp_flag = in.u1();
                }

                if (param.poly_order_minus1 == 0 && param.linear_interp_flag) {
                    param.f_pred_linear_interp_value = readCoefU(header, in);

                    if (i == header.pred_pivot_value[c].length - 2) {
                        Param next = rpu_data_mapp_ng_param[c][i + 1];
                        next.f_pred_linear_interp_value = readCoefU(header, in);
                    }
                } else {
                    int poly_order = param.poly_order_minus1 + 1;
                    param.f_poly_coef = new int[poly_order + 1];
                    for (int p = 0; p <= poly_order; p++) {
                        param.f_poly_coef[p] = readCoefS(header, in);
                    }
                } // Non-linear
            }

            case MAPPING_MMR -> {
                param.mmr_order_minus1 = in.u2();

                param.f_mmr_constant = readCoefS(header, in);

                int mmr_order = param.mmr_order_minus1 + 1;
                param.f_mmr_coef = new int[mmr_order + 1];
                for (int p = 0; p <= mmr_order; p++) {
                    param.f_mmr_coef[p] = readCoefS(header, in);
                }
            }

            default -> throw new IllegalStateException(
                    "mapping_idc: " + mapping_idc);
        }
    }

    @Override
    public void write(RpuHeader header, RbspWriter out) {
        for (int c = 0; c < NUM_CMPS; c++) {
            int end = header.pred_pivot_value[c].length - 1;
            num_mapping_param_predictors[c] = new byte[end];
            for (int i = 0; i < end; i++) {
                out.ue(mapping_idc[c][i]);

                if (num_mapping_param_predictors[c][i] > 0) {
                    out.u1(mapping_param_pred[c][i]);
                } else {
                    mapping_param_pred[c][i] = false;
                }

                if (!mapping_param_pred[c][i]) {
                    ++num_mapping_param_predictors[c][i];
                }

                if (!mapping_param_pred[c][i]) {
                    writeParam(header, out, c, i);
                } else if (num_mapping_param_predictors[c][i] > 1) {
                    out.ue(diff_pred_part_idx_mapping_min[c][i]);
                }
            } // pivot idx
        } // cmp
    }

    private void writeParam(RpuHeader header, RbspWriter out, int c, int i) {
        Param param = rpu_data_mapp_ng_param[c][i];

        switch (mapping_idc[c][i]) {
            case MAPPING_POLYNOMIAL -> {
                out.ue(param.poly_order_minus1);
                if (param.poly_order_minus1 == 0) {
                    out.u1(param.linear_interp_flag);
                }

                if (param.poly_order_minus1 == 0 && param.linear_interp_flag) {
                    writeCoefU(header, out, param.f_pred_linear_interp_value);

                    if (i == header.pred_pivot_value[c].length - 2) {
                        Param next = rpu_data_mapp_ng_param[c][i + 1];
                        writeCoefU(header, out,
                                next.f_pred_linear_interp_value);
                    }
                } else {
                    int poly_order = param.poly_order_minus1 + 1;
                    for (int p = 0; p <= poly_order; p++) {
                        writeCoefS(header, out, param.f_poly_coef[p]);
                    }
                } // Non-linear
            }

            case MAPPING_MMR -> {
                out.u2(param.mmr_order_minus1);

                writeCoefS(header, out, param.f_mmr_constant);

                int mmr_order = param.mmr_order_minus1 + 1;
                for (int p = 0; p <= mmr_order; p++) {
                    writeCoefS(header, out, param.f_mmr_coef[p]);
                }
            }

            default -> throw new IllegalStateException(
                    "mapping_idc: " + mapping_idc);
        }
    }

    @Override
    public void print(RpuHeader header, RbspPrinter out) {
        for (int c = 0; c < NUM_CMPS; c++) {
            out.i32("cmp", c);
            out.enter();

            int end = header.pred_pivot_value[c].length - 1;
            num_mapping_param_predictors[c] = new byte[end];
            for (int i = 0; i < end; i++) {
                out.i32("idx", i);
                out.enter();

                out.ue("mapping_idc", mapping_idc[c][i]);

                if (num_mapping_param_predictors[c][i] > 0) {
                    out.u1("mapping_param_pred", mapping_param_pred[c][i]);
                } else {
                    mapping_param_pred[c][i] = false;
                }

                if (!mapping_param_pred[c][i]) {
                    ++num_mapping_param_predictors[c][i];
                }

                if (!mapping_param_pred[c][i]) {
                    out.enter();
                    printParam(header, out, c, i);
                    out.leave();
                } else if (num_mapping_param_predictors[c][i] > 1) {
                    out.ue("diff_pred_part_idx_mapping_min",
                            diff_pred_part_idx_mapping_min[c][i]);
                }

                out.leave();
            } // pivot idx

            out.leave();
        } // cmp
    }

    private void printParam(RpuHeader header, RbspPrinter out, int c, int i) {
        Param param = rpu_data_mapp_ng_param[c][i];

        switch (mapping_idc[c][i]) {
            case MAPPING_POLYNOMIAL -> {
                out.ue("poly_order_minus1", param.poly_order_minus1);
                if (param.poly_order_minus1 == 0) {
                    out.u1("linear_interp_flag", param.linear_interp_flag);
                }

                if (param.poly_order_minus1 == 0 && param.linear_interp_flag) {
                    printCoefU(header, out, "pred_linear_interp_value",
                            param.f_pred_linear_interp_value);

                    if (i == header.pred_pivot_value[c].length - 2) {
                        Param next = rpu_data_mapp_ng_param[c][i + 1];

                        printCoefU(header, out, "pred_linear_interp_value",
                                next.f_pred_linear_interp_value);
                    }
                } else {
                    int poly_order = param.poly_order_minus1 + 1;
                    for (int p = 0; p <= poly_order; p++) {
                        printCoefS(header, out, "poly_coef[" + p + "]",
                                param.f_poly_coef[p]);
                    }
                } // Non-linear
            }

            case MAPPING_MMR -> {
                out.u2("mmr_order_minus1", param.mmr_order_minus1);

                printCoefS(header, out, "mmr_constant", param.f_mmr_constant);

                int mmr_order = param.mmr_order_minus1 + 1;
                for (int p = 0; p <= mmr_order; p++) {
                    printCoefS(header, out, "mmr_coef[" + p + "]",
                            param.f_mmr_coef[p]);
                }
            }

            default -> throw new IllegalStateException(
                    "mapping_idc: " + mapping_idc);
        }
    }
}
