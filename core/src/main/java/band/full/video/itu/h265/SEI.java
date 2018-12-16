package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.PREFIX_SEI_NUT;
import static band.full.video.itu.h265.NALUnitType.SUFFIX_SEI_NUT;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

import band.full.video.itu.h265.sei.PicTiming;
import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;
import band.full.video.itu.nal.sei.MasteringDisplayColourVolume;
import band.full.video.itu.nal.sei.UserDataRegisteredT35;

import java.util.ArrayList;
import java.util.List;

/**
 * 7.3.2.4 Supplemental enhancement information RBSP syntax
 * <p>
 * <code>sei_rbsp()</code>
 *
 * @author Igor Malinin
 */
public class SEI extends NALUnit {
    public enum PayloadType {
        buffering_period(0),
        pic_timing(1),
        pan_scan_rect(2),
        filler_payload(3),
        user_data_registered_itu_t_t35(4),
        user_data_unregistered(5),
        recovery_point(6),
        scene_info(9),
        picture_snapshot(15),
        progressive_refinement_segment_start(16),
        progressive_refinement_segment_end(17),
        film_grain_characteristics(19),
        post_filter_hint(22),
        tone_mapping_info(23),
        frame_packing_arrangement(45),
        display_orientation(47),
        /** specified in ISO/IEC 23001-11 */
        green_metadata(56),
        structure_of_pictures_info(128),
        active_parameter_sets(129),
        decoding_unit_info(130),
        temporal_sub_layer_zero_index(131),
        decoded_picture_hash(132),
        scalable_nesting(133),
        region_refresh_info(134),
        no_display(135),
        time_code(136),
        mastering_display_colour_volume(137),
        segmented_rect_frame_packing_arrangement(138),
        temporal_motion_constrained_tile_sets(139),
        chroma_resampling_filter_hint(140),
        knee_function_info(141),
        colour_remapping_info(142),
        deinterlaced_field_identification(143),
        content_light_level_info(144),
        dependent_rap_indication(145),
        coded_region_completion(146),
        alternative_transfer_characteristics(147),
        ambient_viewing_environment(148),
        /** specified in Annex F */
        layers_not_present(160),
        /** specified in Annex F */
        inter_layer_constrained_tile_sets(161),
        /** specified in Annex F */
        bsp_nesting(162),
        /** specified in Annex F */
        bsp_initial_arrival_time(163),
        /** specified in Annex F */
        sub_bitstream_property(164),
        /** specified in Annex F */
        alpha_channel_info(165),
        /** specified in Annex F */
        overlay_info(166),
        /** specified in Annex F */
        temporal_mv_prediction_constraints(167),
        /** specified in Annex F */
        frame_field_info(168),
        /** specified in Annex G */
        three_dimensional_reference_displays_info(176),
        /** specified in Annex G */
        depth_representation_info(177),
        /** specified in Annex G */
        multiview_scene_info(178),
        /** specified in Annex G */
        multiview_acquisition_info(179),
        /** specified in Annex G */
        multiview_view_position(180),
        /** specified in Annex I */
        alternative_depth_info(181);

        private static final PayloadType[] CACHE = new PayloadType[182];
        static {
            stream(values()).forEach(t -> CACHE[t.code] = t);
        }

        public final int code;

        private PayloadType(int code) {
            this.code = code;
        }

        public static PayloadType get(int code) {
            return code < CACHE.length ? CACHE[code] : null;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class Message implements Structure<H265Context> {
        public int payloadType;
        public Payload payload;

        public Message() {}

        public Message(PayloadType payloadType, Payload payload) {
            this(payloadType.ordinal(), payload);
        }

        public Message(int payloadType, Payload payload) {
            this.payloadType = payloadType;
            this.payload = payload;
        }

        public Message(H265Context context, RbspReader in) {
            read(context, in);
        }

        @Override
        public void read(H265Context context, RbspReader in) {
            payloadType = readValue(in);
            int size = readValue(in);
            PayloadType type = PayloadType.get(payloadType);
            if (type != null) {
                switch (type) {
                    case pic_timing:
                        if (context.sps == null) {
                            break; // will not be able to parse it
                        }
                        payload = new PicTiming(context, in, size);
                        return;

                    case user_data_registered_itu_t_t35:
                        payload = new UserDataRegisteredT35(
                                context, in, size);
                        return;

                    case mastering_display_colour_volume:
                        payload = new MasteringDisplayColourVolume(
                                context, in, size);
                        return;

                    default: // default
                }
            }

            payload = new Payload.Bytes(in, size);
        }

        int readValue(RbspReader in) {
            int value = 0;
            int last = in.u8();
            while (last == 255) { // 0xFF
                value += 255;
                last = in.u8();
            }
            return value + last;
        }

        @Override
        public void write(H265Context context, RbspWriter out) {
            writeValue(out, payloadType);
            writeValue(out, payload.size(context));
            payload.write(context, out);
        }

        void writeValue(RbspWriter out, int value) {
            while (value >= 255) {
                out.u8(255);
                value -= 255;
            }
            out.u8(value);
        }

        @Override
        public void print(H265Context context, RbspPrinter out) {
            StringBuilder buf = new StringBuilder();
            buf.append("SEI Message ").append(payloadType);

            PayloadType type = PayloadType.get(payloadType);
            if (type != null) {
                buf.append(" [").append(type).append("]");
            }

            buf.append(", size: ").append(payload.size(context));

            out.raw(buf.toString());

            out.enter();
            payload.print(context, out);
            out.leave();
        }
    }

    public List<Message> messages;

    public SEI(NALUnitType type) {
        super(type);
    }

    public static SEI PREFIX_SEI(Message... messages) {
        SEI sei = new SEI(PREFIX_SEI_NUT);
        sei.messages = asList(messages);
        return sei;
    }

    public static SEI SUFFIX_SEI(Message... messages) {
        SEI sei = new SEI(SUFFIX_SEI_NUT);
        sei.messages = asList(messages);
        return sei;
    }

    @Override
    public void read(H265Context context, RbspReader in) {
        messages = new ArrayList<>();
        do {
            messages.add(new Message(context, in));
        } while (in.available() > 8);
        if (!in.isByteAligned()) throw new IllegalStateException();
        if (in.u8() != 0x80) throw new IllegalStateException();
    }

    @Override
    public void write(H265Context context, RbspWriter out) {
        for (Message m : messages) {
            m.write(context, out);
        }
        if (!out.isByteAligned()) throw new IllegalStateException();
        out.u8(0x80);
    }

    @Override
    public void print(H265Context context, RbspPrinter out) {
        for (Message m : messages) {
            m.print(context, out);
        }
    }
}
