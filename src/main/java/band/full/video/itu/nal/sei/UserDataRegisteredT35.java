package band.full.video.itu.nal.sei;

import band.full.video.itu.T35;
import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;
import band.full.video.itu.nal.Payload.Bytes;
import band.full.video.scte.ATSC1;
import band.full.video.smpte.st2094.ST2094_10;

import java.io.PrintStream;

/**
 * D.2.6 User data registered by Recommendation ITU-T T.35 SEI message syntax
 * <p>
 * <code>payloadType == 4</code><br>
 * <code>user_data_registered_itu_t_t35(payloadSize)</code>
 *
 * @author Igor Malinin
 */
public class UserDataRegisteredT35 implements T35, Payload {
    public short country_code;
    public int provider_code;
    public int user_identifier;
    public Payload user_structure;

    public UserDataRegisteredT35() {}

    public UserDataRegisteredT35(RbspReader reader, int size) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int size() {
        int size = 1;
        if (country_code == UNITED_STATES) {
            size += 2;
            if (provider_code == ATSC1.PROVIDER_CODE) {
                size += 4;
                if (user_identifier == ATSC1.USER_IDENTIFIER)
                    return size + user_structure.size();
            }
        }

        return size + user_structure.size();
    }

    @Override
    public void read(RbspReader reader) {
        country_code = reader.readUShort(8);

        if (country_code == UNITED_STATES) {
            provider_code = reader.readUInt(16);
            if (provider_code == ATSC1.PROVIDER_CODE) {
                user_identifier = reader.readS32();
                if (user_identifier == ATSC1.USER_IDENTIFIER) {
                    user_structure = new ST2094_10(reader);
                    return;
                }
            }
        }

        user_structure = new Payload.Bytes(reader);
    }

    @Override
    public void write(RbspWriter writer) {
        writer.writeU(8, country_code);
        if (country_code == UNITED_STATES) {
            writer.writeU(16, provider_code);
            if (provider_code == ATSC1.PROVIDER_CODE) {
                writer.writeS32(user_identifier);
                if (user_identifier == ATSC1.USER_IDENTIFIER) {
                    user_structure.write(writer);
                    return;
                }
            }
        }

        user_structure.write(writer);
    }

    @Override
    public void print(PrintStream ps) {
        ps.print("      country_code: ");
        ps.println(country_code);
        if (country_code == UNITED_STATES) {
            ps.print("      provider_code: ");
            ps.println(String.format("0x%4H", provider_code));
            if (provider_code == ATSC1.PROVIDER_CODE) {
                ps.print("      user_identifier: ");
                ps.println(user_identifier);
                if (user_identifier == ATSC1.USER_IDENTIFIER) {
                    user_structure.print(ps);
                    return;
                }
            }
        }

        user_structure.print(ps);
    }
}
