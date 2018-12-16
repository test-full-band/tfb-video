package band.full.video.itu.h265;

import static java.lang.Math.min;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

/**
 * 7.3.4 Scaling list data syntax
 * <p>
 * <code>scaling_list_data()</code>
 *
 * @author Igor Malinin
 */
public class ScalingListData implements Structure<H265Context> {
    public boolean[][] pred_mode_flag; // u(1)
    public int[][] pred_matrix_id_delta; // ue(v)
    public int[][] dc_coef_minus8; // se(v)
    public int[][][] ScalingList;

    @Override
    public void read(H265Context context, RbspReader in) {
        pred_mode_flag = new boolean[4][];
        pred_matrix_id_delta = new int[4][];
        dc_coef_minus8 = new int[2][];
        ScalingList = new int[4][][];

        for (int sizeId = 0; sizeId < 4; sizeId++) {
            pred_mode_flag[sizeId] = new boolean[4];
            pred_matrix_id_delta[sizeId] = new int[4];
            if (sizeId > 1) {
                dc_coef_minus8[sizeId - 2] = new int[4];
            }
            ScalingList[sizeId] = new int[4][];

            for (int matrixId = 0; matrixId < 6;
                    matrixId += (sizeId == 3) ? 3 : 1) {
                pred_mode_flag[sizeId] = new boolean[4];
                pred_matrix_id_delta[sizeId] = new int[4];
                if (sizeId > 1) {
                    dc_coef_minus8[sizeId - 2] = new int[4];
                }
                ScalingList[sizeId] = new int[4][];

                boolean pred_mode = in.u1();
                pred_mode_flag[sizeId][matrixId] = pred_mode;

                if (!pred_mode) {
                    pred_matrix_id_delta[sizeId][matrixId] = in.ue();
                } else {
                    int nextCoef = 8;
                    int coefNum = min(64, (1 << (4 + (sizeId << 1))));

                    if (sizeId > 1) {
                        dc_coef_minus8[sizeId - 2][matrixId] = in.se();
                        nextCoef = dc_coef_minus8[sizeId - 2][matrixId] + 8;
                    }

                    ScalingList[sizeId][matrixId] = new int[coefNum];
                    for (int i = 0; i < coefNum; i++) {
                        int delta_coef = in.se();
                        nextCoef = (nextCoef + delta_coef + 256) % 256;
                        ScalingList[sizeId][matrixId][i] = nextCoef;
                    }
                }
            }
        }
    }

    @Override
    public void write(H265Context context, RbspWriter out) {
        throw new NoSuchMethodError(); // TODO
    }

    @Override
    public void print(H265Context context, RbspPrinter out) {
        throw new NoSuchMethodError(); // TODO
    }
}
