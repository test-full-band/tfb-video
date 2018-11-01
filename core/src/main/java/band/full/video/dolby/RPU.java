package band.full.video.dolby;

import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

import java.io.PrintStream;

/**
 * Reference Processing Unit Metadata Message.
 * <p>
 * <code>rpu_data_rbsp()</code>
 *
 * @author Igor Malinin
 */
public class RPU {
    public class Header implements Structure {
        public byte rpu_type; // u(6)
        public short rpu_format; // u(11)
        public byte vdr_rpu_profile; // u(4)
        public byte vdr_rpu_level; // u(4)
        public boolean vdr_seq_info_present; // u(1)
        public boolean chroma_resampling_explicit_filter; // u(1)
        public byte coefficient_data_type; // u(2)
        public int coefficient_log2_denom; // ue(v)
        public byte vdr_rpu_normalized_idc; // u(2)
        public boolean BL_video_full_range; // u(1)
        public int BL_bit_depth_minus8; // ue(v)
        public int EL_bit_depth_minus8; // ue(v)
        public int vdr_bit_depth_minus8; // ue(v)
        public boolean spatial_resampling_filter; // u(1)
        // reserved_zero_3bits // u(3)
        public boolean el_spatial_resampling_filter; // u(1)
        public boolean disable_residual; // u(1)
        public boolean vdr_dm_metadata_present; // u(1)
        public boolean use_prev_vdr_rpu; // u(1)
        public int prev_vdr_rpu_id; // ue(v)
        public int vdr_rpu_id; // ue(v)
        public int mapping_color_space; // ue(v)
        public int mapping_chroma_format_idc; // ue(v)
        public int[] num_pivots_minus2; // ue(v)
        public int[][] pred_pivot_value; // u(v)
        public byte nlq_method_idc; // u(3)
        public int num_x_partitions_minus1; // ue(v)
        public int num_y_partitions_minus1; // ue(v)

        @Override
        public void read(NalContext context, RbspReader reader) {
            // TODO Auto-generated method stub
        }

        @Override
        public void write(NalContext context, RbspWriter writer) {
            // TODO Auto-generated method stub
        }

        @Override
        public void print(NalContext context, PrintStream ps) {
            // TODO Auto-generated method stub
        }
    }
}
