package band.full.test.video.patterns.u4k.dv;

import static band.full.core.Quantizer.round;
import static band.full.core.Resolution.STD_2160p;
import static band.full.video.buffer.Framerate.FPS_23_976;
import static band.full.video.dolby.IPTPQc2.PQ10IPTc2;
import static band.full.video.dolby.IPTPQc2.PQ12IPTc2;
import static band.full.video.dolby.VdrDmDataPayload.IPTPQ_YCCtoRGB_coef;
import static band.full.video.dolby.VdrDmDataPayload.IPTPQ_YCCtoRGB_offset;
import static band.full.video.dolby.VdrDmDataPayload.IPTPQc2_RGBtoLMS_coef;
import static band.full.video.itu.h265.NALUnitType.AUD_NUT;
import static band.full.video.itu.h265.NALUnitType.PREFIX_SEI_NUT;
import static band.full.video.itu.h265.NALUnitType.UNSPEC62;
import static band.full.video.itu.h265.SEI.PayloadType.user_data_unregistered;

import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.MuxerDLBMP4;
import band.full.test.video.generator.MuxerFactory;
import band.full.test.video.generator.NalUnitPostProcessor;
import band.full.video.dolby.RPU;
import band.full.video.dolby.RpuDataMapping;
import band.full.video.dolby.RpuHeader;
import band.full.video.dolby.VdrDmDataPayload;
import band.full.video.itu.TransferCharacteristics;
import band.full.video.itu.h265.H265ReaderAnnexB;
import band.full.video.itu.h265.H265WriterAnnexB;
import band.full.video.itu.h265.NALU;
import band.full.video.itu.h265.NALUnit;
import band.full.video.itu.h265.SEI;
import band.full.video.itu.h265.SEI.Message;
import band.full.video.smpte.st2094.ST2094_10;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DolbyVisionProfile5 {
    public static final EncoderParameters DV_P5 = new EncoderParameters(
            STD_2160p, PQ10IPTc2, FPS_23_976);

    public static final String MP4DV_BRANDS = "mp42,dby1";

    public static <A> NalUnitPostProcessor<A> processor(RPU rpu) {
        return processor((args, fragment, frame) -> rpu);
    }

    public static <A> NalUnitPostProcessor<A> processor(RpuFactory<A> rpu) {
        return (args, fragment, in, out) -> process(
                args, fragment, in, out, rpu);
    }

    public static MuxerFactory muxer() {
        return (dir, name, brand) -> new MuxerDLBMP4(
                dir, name, MP4DV_BRANDS, "5");
    }

    @SuppressWarnings("resource") // NB! Do not close what we don't own
    static <A> void process(A args, int fragment,
            InputStream in, OutputStream out, RpuFactory<A> rpu)
            throws IOException {
        var reader = new H265ReaderAnnexB(in);
        var writer = new H265WriterAnnexB(out);

        // Avoid inserting RPU before first Access Unit Delimiter
        writer.write(reader.context, reader.read());

        for (int frame = 0;;) {
            NALUnit nalu = reader.read();

            // Insert RPU at the end of Access Unit
            if (nalu == null || nalu.type == AUD_NUT) {
                byte[] bytes = rpu.create(args, fragment, frame++).toBytes(256);
                writer.write(reader.context, new NALU(UNSPEC62, bytes));
            }

            if (nalu == null) {
                break;
            }

            if (nalu.type == PREFIX_SEI_NUT) {
                Message msg = ((SEI) nalu).messages.get(0);
                if (msg.payloadType == user_data_unregistered.code) {
                    if (fragment > 0) {
                        continue; // Write codec info only once!
                    }
                }
            }

            writer.write(reader.context, nalu);
        }

        writer.flush();
    }

    static RpuHeader rpuHeader() {
        var header = new RpuHeader();

        header.rpu_type = 2;
        header.rpu_format = 18;
        header.vdr_rpu_profile = 0;
        header.vdr_rpu_level = 0;
        header.vdr_seq_info_present = true;
        header.chroma_resampling_explicit_filter = false;
        header.coefficient_data_type = 0;
        header.coefficient_log2_denom = 23;
        header.vdr_rpu_normalized_idc = 1;
        header.BL_video_full_range = true;
        header.BL_bit_depth_minus8 = 2;
        header.EL_bit_depth_minus8 = 2;
        header.vdr_bit_depth_minus8 = 4;
        header.spatial_resampling_filter = false;
        header.el_spatial_resampling_filter = false;
        header.disable_residual = true;
        header.vdr_dm_metadata_present = true; // true
        header.use_prev_vdr_rpu = false;
        header.vdr_rpu_id = 0;
        header.mapping_color_space = 0;
        header.mapping_chroma_format_idc = 0;
        header.pred_pivot_value[0] = new int[] {0, 1023};
        header.pred_pivot_value[1] = new int[] {0, 1023};
        header.pred_pivot_value[2] = new int[] {0, 1023};
        header.num_x_partitions_minus1 = 0;
        header.num_y_partitions_minus1 = 0;

        return header;
    }

    static RpuDataMapping rpuDataMappingNominal() {
        var mapping = new RpuDataMapping();

        mapping.mapping_idc[0] = new int[] {0};
        mapping.num_mapping_param_predictors[0] = new byte[1];
        mapping.mapping_param_pred[0] = new boolean[1];
        mapping.rpu_data_mapp_ng_param[0] = new RpuDataMapping.Param[2];
        {
            var param = mapping.rpu_data_mapp_ng_param[0][0] =
                    new RpuDataMapping.Param();
            param.poly_order_minus1 = 0;
            param.linear_interp_flag = false;
            // nominal (0.0 + 1.0 * x) I' reshaping
            param.f_poly_coef = new int[] {0, 1 << 23};
        }

        mapping.mapping_idc[1] = new int[] {0};
        mapping.num_mapping_param_predictors[1] = new byte[1];
        mapping.mapping_param_pred[1] = new boolean[1];
        mapping.rpu_data_mapp_ng_param[1] = new RpuDataMapping.Param[2];
        {
            var param = mapping.rpu_data_mapp_ng_param[1][0] =
                    new RpuDataMapping.Param();
            param.poly_order_minus1 = 0;
            param.linear_interp_flag = false;
            // nominal (0.5 + 2.0 * x) P' reshaping
            param.f_poly_coef = new int[] {-1 << 22, 2 << 23};
        }

        mapping.mapping_idc[2] = new int[] {0};
        mapping.num_mapping_param_predictors[2] = new byte[1];
        mapping.mapping_param_pred[2] = new boolean[1];
        mapping.rpu_data_mapp_ng_param[2] = new RpuDataMapping.Param[2];
        {
            var param = mapping.rpu_data_mapp_ng_param[2][0] =
                    new RpuDataMapping.Param();
            param.poly_order_minus1 = 0;
            param.linear_interp_flag = false;
            // nominal (0.5 + 2.0 * x) T' reshaping
            param.f_poly_coef = new int[] {-1 << 22, 2 << 23};
        }

        return mapping;
    }

    static VdrDmDataPayload dmDataPayload(
            ST2094_10.DisplayManagementBlock... blocks) {
        var dm = new VdrDmDataPayload();
        dm.affected_dm_metadata_id = 0;
        dm.current_dm_metadata_id = 0;
        dm.YCCtoRGB_coef = IPTPQ_YCCtoRGB_coef;
        dm.YCCtoRGB_offset = IPTPQ_YCCtoRGB_offset;
        dm.RGBtoLMS_coef = IPTPQc2_RGBtoLMS_coef;
        dm.scene_refresh_flag = 1;
        dm.signal_bit_depth = 12;
        dm.signal_color_space = 2;
        dm.signal_chroma_format = 0;
        dm.signal_full_range = 1;
        dm.source_min_PQ = 0;
        dm.source_max_PQ = 4095;
        dm.ext_blocks = blocks;
        return dm;
    }

    static ST2094_10.ContentRange dmContentRangeSDR() {
        var block = new ST2094_10.ContentRange();
        block.min_PQ = 0;
        block.max_PQ = 2048;
        block.avg_PQ = 1024;
        return block;
    }

    static ST2094_10.ContentRange dmContentRange10K() {
        var block = new ST2094_10.ContentRange();
        block.min_PQ = 0;
        block.max_PQ = 4095;
        block.avg_PQ = 2048;
        return block;
    }

    static ST2094_10.ContentRange dmContentRangeSDR100(double area) {
        TransferCharacteristics transfer = PQ12IPTc2.transfer;

        var block = new ST2094_10.ContentRange();
        block.min_PQ = 0;
        block.max_PQ = (short) round(transfer.fromLinear(0.01) * 4095);
        block.avg_PQ = (short) round(transfer.fromLinear(0.01 * area) * 4095);
        return block;
    }

    static ST2094_10.TrimPass dmTrimPassSDR() {
        var block = new ST2094_10.TrimPass();
        block.target_max_PQ = 2081;
        return block;
    }
}
