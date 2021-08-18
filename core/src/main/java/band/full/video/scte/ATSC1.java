package band.full.video.scte;

import static band.full.core.ArrayMath.toHexString;

import band.full.video.itu.T35;
import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.smpte.st2094.ST2094_10;

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
    public static final short PROVIDER_CODE = 0x0031;

    /**
     * This is a 32 bit code that indicates the contents of the
     * <code>user_structure()</code>.
     */
    public static final int USER_IDENTIFIER = 0x47413934; // "GA94"

    public static final byte MARKER_BITS = (byte) 0xFF;

    public short user_data_type_code; // u8
    public Payload user_data_type_structure;

    public ATSC1() {}

    public ATSC1(ST2094_10 dm) {
        user_data_type_code = 0x09;
        user_data_type_structure = dm;
    }

    public ATSC1(RbspReader in) {
        read(null, in);
    }

    @Override
    public int size(NalContext context) {
        return user_data_type_structure.size(context) + 2;
    }

    @Override
    public void read(NalContext context, RbspReader in) {
        user_data_type_code = in.u8();

        user_data_type_structure = switch (user_data_type_code) {
            case 0x09 -> new ST2094_10(context, in);
            default -> new Payload.Bytes(in, (in.available() >> 3) - 1);
        };

        int marker_bits = in.i8();
        if (marker_bits != -1) throw new IllegalStateException();
    }

    @Override
    public void write(NalContext context, RbspWriter out) {
        // ATSC1_data()
        out.u8(user_data_type_code);
        user_data_type_structure.write(context, out);
        out.i8(MARKER_BITS);
    }

    @Override
    public void print(NalContext context, RbspPrinter out) {
        out.raw("country_code: 0xB5 [United States]");
        out.raw("provider_code: 0x0031 [ATSC]");
        out.raw("user_identifier: 0x47413934 \"GA94\" [ATSC1]");

        if (user_data_type_code == 0x09) {
            out.raw("user_data_type_code: 0x09, SMPTE ST.2094-10");
        } else {
            String udtc = toHexString((byte) user_data_type_code);
            out.raw("user_data_type_code: 0x" + udtc);
        }

        user_data_type_structure.print(context, out);
    }
}
