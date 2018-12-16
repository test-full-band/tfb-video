package band.full.video.itu.h265;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

/**
 * 7.3.7 Short-term reference picture set syntax
 * <p>
 * <code>st_ref_pic_set(stRpsIdx)</code>
 *
 * @author Igor Malinin
 */
public class StRefPicSet implements Structure<H265Context> {
    public final byte stRpsIdx;
    public final byte stRpsNum;

    public boolean inter_ref_pic_set_prediction_flag;
    public int delta_idx_minus1;
    public boolean delta_rps_sign;
    public int abs_delta_rps_minus1;
    public boolean[] used_by_curr_pic_flag;
    public boolean[] use_delta_flag;

    public int num_negative_pics;
    public int num_positive_pics;
    public int[] delta_poc_s0_minus1;
    public boolean[] used_by_curr_pic_s0_flag;
    public int[] delta_poc_s1_minus1;
    public boolean[] used_by_curr_pic_s1_flag;

    public StRefPicSet(byte stRpsIdx, byte stRpsNum) {
        this.stRpsIdx = stRpsIdx;
        this.stRpsNum = stRpsNum;
    }

    @Override
    public void read(H265Context context, RbspReader in) {
        if (stRpsIdx != 0) {
            inter_ref_pic_set_prediction_flag = in.u1();
        }

        if (inter_ref_pic_set_prediction_flag) {
            if (stRpsIdx == stRpsNum) {
                delta_idx_minus1 = in.ue();
            }

            int RefRpsIdx = stRpsIdx - (delta_idx_minus1 + 1);

            delta_rps_sign = in.u1();
            abs_delta_rps_minus1 = in.ue();

            for (int j = 0; j <= context.NumDeltaPocs(RefRpsIdx); j++) {
                used_by_curr_pic_flag[j] = in.u1();
                if (!used_by_curr_pic_flag[j]) {
                    use_delta_flag[j] = in.u1();
                }
            }
        } else {
            num_negative_pics = in.ue();
            num_positive_pics = in.ue();

            for (int i = 0; i < num_negative_pics; i++) {
                delta_poc_s0_minus1[i] = in.ue();
                used_by_curr_pic_s0_flag[i] = in.u1();
            }

            for (int i = 0; i < num_positive_pics; i++) {
                delta_poc_s1_minus1[i] = in.ue();
                used_by_curr_pic_s1_flag[i] = in.u1();
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
