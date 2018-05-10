package band.full.video.smpte.st2094;

import static band.full.core.ArrayMath.toHexString;
import static band.full.video.itu.nal.RbspWriter.countUEbits;

import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Structure;

import java.io.PrintStream;

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

    public static class ContentRange extends DisplayManagementBlock {
        public static final short LEVEL = 1;

        public short min_PQ;
        public short max_PQ;
        public short avg_PQ;

        public ContentRange() {
            super(5, LEVEL);
        }

        @Override
        public void read(RbspReader reader) {
            min_PQ = reader.readUShort(12);
            max_PQ = reader.readUShort(12);
            avg_PQ = reader.readUShort(12);

            if (reader.readUByte(4) != 0)
                throw new IllegalStateException();
        }

        @Override
        public void write(RbspWriter writer) {
            writer.writeU(12, min_PQ);
            writer.writeU(12, max_PQ);
            writer.writeU(12, avg_PQ);

            writer.writeU(4, 0);
        }

        @Override
        public void print(PrintStream ps) {
            ps.print("    Level ");
            ps.print(level);
            ps.println(" - Content Range");

            ps.print("      min_PQ: ");
            ps.println(min_PQ);
            ps.print("      max_PQ: ");
            ps.println(max_PQ);
            ps.print("      avg_PQ: ");
            ps.println(avg_PQ);
        }
    }

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
        public void read(RbspReader reader) {
            target_max_PQ = reader.readUShort(12);
            trim_slope = reader.readUShort(12);
            trim_offset = reader.readUShort(12);
            trim_power = reader.readUShort(12);
            trim_chroma_weight = reader.readUShort(12);
            trim_saturation_gain = reader.readUShort(12);
            ms_weight = reader.readSShort(13);

            if (reader.readUByte(3) != 0)
                throw new IllegalStateException();
        }

        @Override
        public void write(RbspWriter writer) {
            writer.writeU(12, target_max_PQ);
            writer.writeU(12, trim_slope);
            writer.writeU(12, trim_offset);
            writer.writeU(12, trim_power);
            writer.writeU(12, trim_chroma_weight);
            writer.writeU(12, trim_saturation_gain);
            writer.writeS(13, ms_weight);

            writer.writeU(3, 0);
        }

        @Override
        public void print(PrintStream ps) {
            ps.print("    Level ");
            ps.print(level);
            ps.println(" - Trim Pass");

            ps.print("      target_max_PQ: ");
            ps.println(target_max_PQ);
            ps.print("      trim_slope: ");
            ps.println(trim_slope);
            ps.print("      trim_offset: ");
            ps.println(trim_offset);
            ps.print("      trim_power: ");
            ps.println(trim_power);
            ps.print("      trim_chroma_weight: ");
            ps.println(trim_chroma_weight);
            ps.print("      trim_saturation_gain: ");
            ps.println(trim_saturation_gain);
            ps.print("      ms_weight: ");
            ps.println(ms_weight);
        }
    }

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
        public void read(RbspReader reader) {
            left_offset = reader.readUShort(13);
            right_offset = reader.readUShort(13);
            top_offset = reader.readUShort(13);
            bottom_offset = reader.readUShort(13);

            if (reader.readUByte(4) != 0)
                throw new IllegalStateException();
        }

        @Override
        public void write(RbspWriter writer) {
            writer.writeU(13, left_offset);
            writer.writeU(13, right_offset);
            writer.writeU(13, top_offset);
            writer.writeU(13, bottom_offset);

            writer.writeU(4, 0);
        }

        @Override
        public void print(PrintStream ps) {
            ps.print("    Level ");
            ps.print(level);
            ps.println(" - Active Area");

            ps.print("      left_offset: ");
            ps.println(left_offset);
            ps.print("      right_offset: ");
            ps.println(right_offset);
            ps.print("      top_offset: ");
            ps.println(top_offset);
            ps.print("      bottom_offset: ");
            ps.println(bottom_offset);
        }
    }

    public class Reserved extends DisplayManagementBlock {
        public byte[] bytes;

        public Reserved(int length, short level) {
            super(length, level);
        }

        @Override
        public void read(RbspReader reader) {
            bytes = reader.readBytes(length);
        }

        @Override
        public void write(RbspWriter writer) {
            writer.writeBytes(bytes);
        }

        @Override
        public void print(PrintStream ps) {
            ps.print("    Level ");
            ps.println(level);
            ps.print("      bytes: ");
            ps.println(toHexString(bytes));
        }
    }

    public ST2094_10() {}

    public ST2094_10(RbspReader reader) {
        read(reader);
    }

    public int app_identifier = 1;
    public int app_version;
    public boolean metadata_refresh;

    public DisplayManagementBlock[] ext_blocks;

    @Override
    public int size() {
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
    public void read(RbspReader reader) {
        app_identifier = reader.readUE();
        app_version = reader.readUE();

        metadata_refresh = reader.readU1();
        if (metadata_refresh) {
            int num_ext_blocks = reader.readUE();
            if (num_ext_blocks > 0) {
                while (!reader.isByteAligned())
                    if (reader.readU1()) throw new IllegalStateException();

                ext_blocks = new DisplayManagementBlock[num_ext_blocks];
                for (int i = 0; i < num_ext_blocks; i++) {
                    int length = reader.readUE();
                    short level = reader.readUShort(8);

                    DisplayManagementBlock block;
                    switch (level) {
                        case ContentRange.LEVEL:
                            block = new ContentRange();
                            break;

                        case TrimPass.LEVEL:
                            block = new TrimPass();
                            break;

                        case ActiveArea.LEVEL:
                            block = new ActiveArea();
                            break;

                        default:
                            block = new Reserved(length, level);
                            break;
                    }

                    if (length != block.length)
                        throw new IllegalStateException();

                    block.read(reader);

                    while (!reader.isByteAligned())
                        if (reader.readU1()) throw new IllegalStateException();

                    ext_blocks[i] = block;
                }
            }
        }

        while (!reader.isByteAligned())
            if (reader.readU1()) throw new IllegalStateException();
    }

    @Override
    public void write(RbspWriter writer) {
        writer.writeUE(app_identifier);
        writer.writeUE(app_version);

        writer.writeU1(metadata_refresh);
        if (metadata_refresh) {
            writer.writeUE(ext_blocks == null ? 0 : ext_blocks.length);
            if (ext_blocks != null && ext_blocks.length > 0) {
                while (!writer.isByteAligned()) {
                    writer.writeU1(false);
                }

                for (int i = 0; i < ext_blocks.length; i++) {
                    DisplayManagementBlock block = ext_blocks[i];

                    writer.writeUE(block.length);
                    writer.writeU(8, block.level);

                    block.write(writer);

                    while (!writer.isByteAligned()) {
                        writer.writeU1(false);
                    }

                    ext_blocks[i] = block;
                }
            }
        }

        while (!writer.isByteAligned()) {
            writer.writeU1(false);
        }
    }

    @Override
    public void print(PrintStream ps) {
        ps.println("    Display Management");
        ps.print("      app_identifier: ");
        ps.println(app_identifier);
        ps.print("      app_version: ");
        ps.println(app_version);

        ps.print("      metadata_refresh: ");
        ps.println(metadata_refresh);
        if (metadata_refresh && ext_blocks != null && ext_blocks.length > 0) {
            for (var ext_block : ext_blocks) {
                ext_block.print(ps);
            }
        }
    }
}
