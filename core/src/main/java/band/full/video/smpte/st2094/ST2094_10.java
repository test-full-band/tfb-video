package band.full.video.smpte.st2094;

import static band.full.video.dolby.IPTPQc2.PQ12IPTc2;
import static band.full.video.itu.nal.RbspWriter.countUEbits;

import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

/**
 * SMPTE ST 2094-10 Metadata Message.
 * <p>
 * <code>ST2094-10_data()</code>
 *
 * @author Igor Malinin
 * @see <a href=
 *      "https://www.atsc.org/wp-content/uploads/2017/05/A341-2018-Video-HEVC-1.pdf">
 *      A/341:2018</a> Video - HEVC
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ST2094_10 implements Payload {
    /**
     * <code>ext_dm_data_block_payload()</code>
     */
    public abstract static class DisplayManagementBlock implements Structure {
        public final int length;
        public final short level;

        public DisplayManagementBlock(int length, short level) {
            this.length = length;
            this.level = level;
        }
    }

    /** Level 1 Metadata – Content Range */
    public static class ContentRange extends DisplayManagementBlock {
        public static final short LEVEL = 1;

        public short min_PQ;
        public short max_PQ;
        public short avg_PQ;

        public ContentRange() {
            super(5, LEVEL);
        }

        @Override
        public void read(NalContext context, RbspReader in) {
            min_PQ = in.u12();
            max_PQ = in.u12();
            avg_PQ = in.u12();

            if (in.u4() != 0) throw new IllegalStateException();
        }

        @Override
        public void write(NalContext context, RbspWriter out) {
            out.u12(min_PQ);
            out.u12(max_PQ);
            out.u12(avg_PQ);

            out.u4(0);
        }

        @Override
        public void print(NalContext context, RbspPrinter out) {
            out.raw("Level 1 - Content Range");

            out.enter();
            out.raw("min_PQ: " + min_PQ + ", " + pq12(min_PQ));
            out.raw("max_PQ: " + max_PQ + ", " + pq12(max_PQ));
            out.raw("avg_PQ: " + avg_PQ + ", " + pq12(avg_PQ));
            out.leave();
        }

        private static double pq12(int code) {
            return PQ12IPTc2.transfer.toLinear(PQ12IPTc2.fromLumaCode(code))
                    * PQ12IPTc2.transfer.getNominalDisplayPeakLuminance();
        }
    }

    /** Level 2 Metadata – Trim Pass */
    public static class TrimPass extends DisplayManagementBlock {
        public static final short LEVEL = 2;

        public short target_max_PQ;
        public short trim_slope = 2048;
        public short trim_offset = 2048;
        public short trim_power = 2048;
        public short trim_chroma_weight = 2048;
        public short trim_saturation_gain = 2048;
        public short ms_weight = -1;

        public TrimPass() {
            super(11, LEVEL);
        }

        @Override
        public void read(NalContext context, RbspReader in) {
            target_max_PQ = in.u12();
            trim_slope = in.u12();
            trim_offset = in.u12();
            trim_power = in.u12();
            trim_chroma_weight = in.u12();
            trim_saturation_gain = in.u12();
            ms_weight = in.i13();

            if (in.u3() != 0) throw new IllegalStateException();
        }

        @Override
        public void write(NalContext context, RbspWriter out) {
            out.u12(target_max_PQ);
            out.u12(trim_slope);
            out.u12(trim_offset);
            out.u12(trim_power);
            out.u12(trim_chroma_weight);
            out.u12(trim_saturation_gain);
            out.i13(ms_weight);

            out.u3(0);
        }

        @Override
        public void print(NalContext context, RbspPrinter out) {
            out.raw("Level " + level + " - Trim Pass");

            out.enter();

            out.raw("target_max_PQ: " + target_max_PQ
                    + ", " + pq12(target_max_PQ));

            out.u12("trim_slope", trim_slope);
            out.u12("trim_offset", trim_offset);
            out.u12("trim_power", trim_power);
            out.u12("trim_chroma_weight", trim_chroma_weight);
            out.u12("trim_saturation_gain", trim_saturation_gain);
            out.i13("ms_weight", ms_weight);

            out.leave();
        }

        private static double pq12(int code) {
            return PQ12IPTc2.transfer.toLinear(PQ12IPTc2.fromLumaCode(code))
                    * PQ12IPTc2.transfer.getNominalDisplayPeakLuminance();
        }
    }

    /**
     * NB! Non-ST.2094-10!<br>
     * Level 3 Metadata – Content Range Offsets
     *
     * @see <a href=
     *      "https://standards.cta.tech/kwspub/published_docs/CTA-861-G_FINAL_revised_2017.pdf">
     *      CTA‐861‐G final revised 2017</a>
     */
    public static class ContentRangeOffsets extends DisplayManagementBlock {
        public static final short LEVEL = 3;

        public short min_PQ_offset;
        public short max_PQ_offset;
        public short avg_PQ_offset;

        public ContentRangeOffsets() {
            super(5, LEVEL);
        }

        @Override
        public void read(NalContext context, RbspReader in) {
            min_PQ_offset = in.u12();
            max_PQ_offset = in.u12();
            avg_PQ_offset = in.u12();

            if (in.u4() != 0) throw new IllegalStateException();
        }

        @Override
        public void write(NalContext context, RbspWriter out) {
            out.u12(min_PQ_offset);
            out.u12(max_PQ_offset);
            out.u12(avg_PQ_offset);

            out.u4(0);
        }

        @Override
        public void print(NalContext context, RbspPrinter out) {
            out.raw("Level 3 - Content Range Offsets");

            out.enter();
            out.u12("min_PQ_offset", min_PQ_offset);
            out.u12("max_PQ_offset", max_PQ_offset);
            out.u12("avg_PQ_offset", avg_PQ_offset);
            out.leave();
        }
    }

    // (DV, Non-ST.2094-10)
    /** Level 4 Metadata - unknown */
    public static class L4 extends DisplayManagementBlock {
        public static final short LEVEL = 4;

        public short f1_PQ;
        public short f2_PQ;

        public L4() {
            super(3, LEVEL);
        }

        @Override
        public void read(NalContext context, RbspReader in) {
            f1_PQ = in.u12();
            f2_PQ = in.u12();
        }

        @Override
        public void write(NalContext context, RbspWriter out) {
            out.u12(f1_PQ);
            out.u12(f2_PQ);
        }

        @Override
        public void print(NalContext context, RbspPrinter out) {
            out.raw("Level " + level + " - unknown");

            out.enter();
            out.u12("f1", f1_PQ);
            out.u12("f2", f2_PQ);
            out.leave();
        }
    }

    /** Level 5 Metadata - Active Area */
    public static class ActiveArea extends DisplayManagementBlock {
        public static final short LEVEL = 5;

        public short left_offset;
        public short right_offset;
        public short top_offset;
        public short bottom_offset;

        public ActiveArea() {
            super(7, LEVEL);
        }

        @Override
        public void read(NalContext context, RbspReader in) {
            left_offset = in.u13();
            right_offset = in.u13();
            top_offset = in.u13();
            bottom_offset = in.u13();

            if (in.u4() != 0) throw new IllegalStateException();
        }

        @Override
        public void write(NalContext context, RbspWriter out) {
            out.u13(left_offset);
            out.u13(right_offset);
            out.u13(top_offset);
            out.u13(bottom_offset);

            out.u4(0);
        }

        @Override
        public void print(NalContext context, RbspPrinter out) {
            out.raw("Level " + level + " - Active Area");

            out.enter();
            out.u13("left_offset: ", left_offset);
            out.u13("right_offset: ", right_offset);
            out.u13("top_offset: ", top_offset);
            out.u13("bottom_offset: ", bottom_offset);
            out.leave();
        }
    }

    // (DV, Non-ST.2094-10)
    /** Level 6 Metadata - Optional MaxFALL/MaxCLL metadata (Static) */
    public static class ContentLightLevel extends DisplayManagementBlock {
        public static final short LEVEL = 6;

        public int max_content_light_level; // u(16)
        public int max_pic_average_light_level; // u(16)
        public int reserved; // i(32)

        public ContentLightLevel() {
            super(8, LEVEL);
        }

        @Override
        public void read(NalContext context, RbspReader in) {
            max_content_light_level = in.u16();
            max_pic_average_light_level = in.u16();
            reserved = in.i32();
        }

        @Override
        public void write(NalContext context, RbspWriter out) {
            out.u16(max_content_light_level);
            out.u16(max_pic_average_light_level);
            out.i32(reserved);
        }

        @Override
        public void print(NalContext context, RbspPrinter out) {
            out.raw("Level " + level + " - MaxFALL/MaxCLL metadata");

            out.enter();
            out.u16("max_content_light_level", max_content_light_level);
            out.u16("max_pic_average_light_level", max_pic_average_light_level);
            out.printH("reserved", 32, reserved);
            out.leave();
        }
    }

    public static class Reserved extends DisplayManagementBlock {
        public byte[] bytes;

        public Reserved(int length, short level) {
            super(length, level);
        }

        @Override
        public void read(NalContext context, RbspReader in) {
            bytes = in.readBytes(length);
        }

        @Override
        public void write(NalContext context, RbspWriter out) {
            out.writeBytes(bytes);
        }

        @Override
        public void print(NalContext context, RbspPrinter out) {
            out.raw("Level " + level);

            out.enter();
            out.printH("bytes", bytes);
            if (level == 4) { // FIXME
                RbspReader r = new RbspReader(bytes, 0, 3);
                out.u12("hi", r.u12());
                out.u12("lo", r.u12());
            }
            out.leave();
        }
    }

    public ST2094_10() {
    }

    public ST2094_10(boolean metadata_refresh,
            DisplayManagementBlock... ext_blocks) {
        this.metadata_refresh = metadata_refresh;
        this.ext_blocks = ext_blocks;
    }

    public ST2094_10(NalContext context, RbspReader in) {
        read(context, in);
    }

    public int app_identifier = 1;
    public int app_version;
    public boolean metadata_refresh;

    public DisplayManagementBlock[] ext_blocks;

    @Override
    public int size(NalContext context) {
        int bits = countUEbits(app_identifier)
                + countUEbits(app_version) + 1;

        if (metadata_refresh) {
            bits += countUEbits(ext_blocks.length) + 7;
            bits &= ~0b111; // align

            for (var block : ext_blocks) {
                bits += countUEbits(block.length) + 8;
                bits += block.length << 3;
            }
        }

        return bits + 7 >> 3; // align
    }

    @Override
    public void read(NalContext context, RbspReader in) {
        app_identifier = in.ue();
        app_version = in.ue();

        metadata_refresh = in.u1();
        if (metadata_refresh) {
            int num_ext_blocks = in.ue();
            if (num_ext_blocks > 0) {
                while (!in.isByteAligned())
                    if (in.u1()) throw new IllegalStateException();

                ext_blocks = new DisplayManagementBlock[num_ext_blocks];
                for (int i = 0; i < num_ext_blocks; i++) {
                    int length = in.ue();
                    short level = in.u8();

                    DisplayManagementBlock block;
                    switch (level) {
                        case ContentRange.LEVEL:
                            block = new ContentRange();
                            break;

                        case TrimPass.LEVEL:
                            block = new TrimPass();
                            break;

                        case ContentRangeOffsets.LEVEL:
                            block = new ContentRangeOffsets();
                            break;

                        case L4.LEVEL:
                            block = new L4();
                            break;

                        case ActiveArea.LEVEL:
                            block = new ActiveArea();
                            break;

                        case ContentLightLevel.LEVEL:
                            block = new ContentLightLevel();
                            break;

                        default:
                            block = new Reserved(length, level);
                            break;
                    }

                    if (length != block.length)
                        throw new IllegalStateException();

                    block.read(context, in);

                    ext_blocks[i] = block;
                }
            }
        }

        while (!in.isByteAligned())
            if (in.u1()) throw new IllegalStateException();
    }

    @Override
    public void write(NalContext context, RbspWriter out) {
        out.ue(app_identifier);
        out.ue(app_version);

        out.u1(metadata_refresh);
        if (metadata_refresh) {
            out.ue(ext_blocks == null ? 0 : ext_blocks.length);
            if (ext_blocks != null && ext_blocks.length > 0) {
                while (!out.isByteAligned()) {
                    out.u1(false);
                }

                for (var block : ext_blocks) {
                    out.ue(block.length);
                    out.u8(block.level);

                    block.write(context, out);

                    while (!out.isByteAligned()) {
                        out.u1(false);
                    }
                }
            }
        }

        while (!out.isByteAligned()) {
            out.u1(false);
        }
    }

    @Override
    public void print(NalContext context, RbspPrinter out) {
        out.raw("Display Management");

        out.enter();
        out.ue("app_identifier", app_identifier);
        out.ue("app_version", app_version);

        out.u1("metadata_refresh", metadata_refresh);
        out.leave();

        if (metadata_refresh && ext_blocks != null && ext_blocks.length > 0) {
            for (var ext_block : ext_blocks) {
                ext_block.print(context, out);
            }
        }
    }
}
