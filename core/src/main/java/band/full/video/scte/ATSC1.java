package band.full.video.scte;

import static band.full.core.ArrayMath.toHexString;

import band.full.video.itu.T35;
import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.smpte.st2094.ST2094_10;

import java.io.PrintStream;

/**
 * <code>ATSC1_data()</code>
 *
 * @author Igor Malinin
 * @see <a href=
 *      "https://www.scte.org/documents/pdf/Standards/ANSI_SCTE%20128-1%202013.pdf">
 *      ANSI/SCTE 128-1 2013</a>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ATSC1 implements T35, Payload {
    /**
     * A fixed 8-bit field, the value of which shall be 0xB5.
     */
    public static final int COUNTRY_CODE = UNITED_STATES;

    /**
     * A fixed 16-bit field registered by the ATSC. The value shall be 0x0031.
     */
    public static final int PROVIDER_CODE = 0x0031;

    /**
     * This is a 32 bit code that indicates the contents of the
     * <code>user_structure()</code>.
     */
    public static final int USER_IDENTIFIER = 0x47413934; // "GA94"

    public static final byte MARKER_BITS = (byte) 0xFF;

    public short user_data_type_code; // u8
    public Payload user_data_type_structure;

    public ATSC1() {}

    public ATSC1(RbspReader reader) {
        read(null, reader);
    }

    @Override
    public int size(NalContext context) {
        return user_data_type_structure.size(context) + 2;
    }

    @Override
    public void read(NalContext context, RbspReader reader) {
        user_data_type_code = reader.readUShort(8);

        switch (user_data_type_code) {
            case 0x09: {
                user_data_type_structure = new ST2094_10(context, reader);
                break;
            }

            default:
                int available = reader.available() >> 3;
                user_data_type_structure =
                        new Payload.Bytes(reader, available - 1);
        }

        int marker_bits = reader.readByte();
        if (marker_bits != -1) throw new IllegalStateException();
    }

    @Override
    public void write(NalContext context, RbspWriter writer) {
        // ATSC1_data()
        writer.writeU(8, user_data_type_code);
        user_data_type_structure.write(context, writer);
        writer.writeS8(MARKER_BITS);
    }

    @Override
    public void print(NalContext context, PrintStream ps) {
        ps.println("      country_code: 0xB5 [United States]");
        ps.println("      provider_code: 0x0031 [ATSC]");
        ps.println("      user_identifier: 0x47413934 \"GA94\" [ATSC1]");

        ps.print("      user_data_type_code: 0x");
        ps.print(toHexString((byte) user_data_type_code));
        switch (user_data_type_code) {
            case 0x09:
                ps.println(", SMPTE ST.2094-10");
                break;

            default:
                ps.println();
        }

        user_data_type_structure.print(context, ps);
    }
}
