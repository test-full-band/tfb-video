package band.full.video.itu.nal.sei;

import static band.full.core.ArrayMath.toHexString;

import band.full.video.itu.T35;
import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.scte.ATSC1;

import java.io.PrintStream;

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
    public short country_code;
    public int provider_code;
    public int user_identifier;
    public Payload user_structure;

    public UserDataRegisteredT35() {}

    public UserDataRegisteredT35(NalContext context, RbspReader reader,
            int size) {
        int start = reader.available();
        read(context, reader);
        if (start - reader.available() != size << 3)
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
    public void read(NalContext context, RbspReader reader) {
        country_code = reader.readUShort(8);

        if (country_code == UNITED_STATES) {
            provider_code = reader.readUInt(16);
            if (provider_code == ATSC1.PROVIDER_CODE) {
                user_identifier = reader.readS32();
                if (user_identifier == ATSC1.USER_IDENTIFIER) {
                    user_structure = new ATSC1(reader);
                    return;
                }
            }
        }

        user_structure = new Payload.Bytes(reader);
    }

    @Override
    public void write(NalContext context, RbspWriter writer) {
        writer.writeU(8, country_code);
        if (country_code == UNITED_STATES) {
            writer.writeU(16, provider_code);
            if (provider_code == ATSC1.PROVIDER_CODE) {
                writer.writeS32(user_identifier);
                if (user_identifier == ATSC1.USER_IDENTIFIER) {
                    user_structure.write(context, writer);
                    return;
                }
            }
        }

        user_structure.write(context, writer);
    }

    @Override
    public void print(NalContext context, PrintStream ps) {
        ps.print("      country_code: ");
        ps.println(country_code);
        if (country_code == UNITED_STATES) {
            ps.print("      provider_code: 0x");
            ps.println(toHexString((short) provider_code));
            if (provider_code == ATSC1.PROVIDER_CODE) {
                ps.print("      user_identifier: 0x");
                ps.println(toHexString(user_identifier));
                if (user_identifier == ATSC1.USER_IDENTIFIER) {
                    user_structure.print(context, ps);
                    return;
                }
            }
        }

        user_structure.print(context, ps);
    }
}
