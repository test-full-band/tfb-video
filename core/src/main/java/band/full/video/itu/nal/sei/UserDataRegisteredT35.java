package band.full.video.itu.nal.sei;

import band.full.video.itu.T35;
import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.scte.ATSC1;

/**
 * D.2.6 User data registered by Recommendation ITU-T T.35 SEI message syntax
 * <p>
 * <code>payloadType == 4</code><br>
 * <code>user_data_registered_itu_t_t35(payloadSize)</code>
 *
 * @author Igor Malinin
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserDataRegisteredT35 implements T35, Payload {
    public short country_code; // u8
    public short provider_code; // u16 stored as i16
    public int user_identifier; // u32 stored as i32

    public Payload user_structure;

    public UserDataRegisteredT35() {}

    public UserDataRegisteredT35(ATSC1 atsc1) {
        country_code = ATSC1.COUNTRY_CODE;
        provider_code = ATSC1.PROVIDER_CODE;
        user_identifier = ATSC1.USER_IDENTIFIER;
        user_structure = atsc1;
    }

    public UserDataRegisteredT35(NalContext context, RbspReader in,
            int size) {
        int start = in.available();
        read(context, in);
        if (start - in.available() != size << 3)
            throw new IllegalArgumentException();
    }

    @Override
    public int size(NalContext context) {
        int size = 1;
        if (country_code == UNITED_STATES) {
            size += 2;
            if (provider_code == ATSC1.PROVIDER_CODE) {
                size += 4;
                if (user_identifier == ATSC1.USER_IDENTIFIER)
                    return size + user_structure.size(context);
            }
        }

        return size + user_structure.size(context);
    }

    @Override
    public void read(NalContext context, RbspReader in) {
        country_code = in.u8();
        if (country_code == UNITED_STATES) {
            provider_code = in.i16();
            if (provider_code == ATSC1.PROVIDER_CODE) {
                user_identifier = in.i32();
                if (user_identifier == ATSC1.USER_IDENTIFIER) {
                    user_structure = new ATSC1(in);
                    return;
                }
            }
        }

        user_structure = new Payload.Bytes(in);
    }

    @Override
    public void write(NalContext context, RbspWriter out) {
        out.u8(country_code);
        if (country_code == UNITED_STATES) {
            out.u16(provider_code);
            if (provider_code == ATSC1.PROVIDER_CODE) {
                out.i32(user_identifier);
                if (user_identifier == ATSC1.USER_IDENTIFIER) {
                    user_structure.write(context, out);
                    return;
                }
            }
        }

        user_structure.write(context, out);
    }

    @Override
    public void print(NalContext context, RbspPrinter out) {
        out.u8("country_code", country_code);
        if (country_code == UNITED_STATES) {
            out.printH("provider_code", 16, provider_code);
            if (provider_code == ATSC1.PROVIDER_CODE) {
                out.printH("user_identifier", 32, user_identifier);
                if (user_identifier == ATSC1.USER_IDENTIFIER) {
                    user_structure.print(context, out);
                    return;
                }
            }
        }

        user_structure.print(context, out);
    }
}
