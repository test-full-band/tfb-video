package band.full.video.itu.nal;

import static band.full.core.ArrayMath.toHexString;

import band.full.video.atsc.ATSC1;
import band.full.video.itu.T35;
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
public class UserDataRegisteredT35 implements T35, Structure {
    public short country_code;
    public int provider_code;
    public int user_id;
    public short data_type_code;

    public Structure user_structure;
    public byte[] bytes;

    @Override
    public void read(RbspReader reader) {
        country_code = reader.readUShort(8);
        provider_code = reader.readUInt(16);
        user_id = reader.readS32();
        data_type_code = reader.readUShort(8);

        if (country_code == ATSC1.COUNTRY_CODE
                && provider_code == ATSC1.PROVIDER_CODE
                && user_id == ATSC1.USER_IDENTIFIER) {
            if (data_type_code == 0x09) {
                user_structure = new ST2094_10();
                user_structure.read(reader);
                return;
            }
        }

        bytes = reader.readTrailingBits();
    }

    @Override
    public void write(RbspWriter writer) {
        writer.writeU(8, country_code);
        writer.writeU(16, provider_code);
        writer.writeS32(user_id);
        writer.writeU(8, data_type_code);

        if (user_structure != null) {
            user_structure.write(writer);
        }

        if (bytes != null) {
            writer.writeBytes(bytes);
        }
    }

    @Override
    public void print(PrintStream ps) {
        ps.print("    country_code: ");
        ps.println(country_code);
        ps.print("    provider_code: ");
        ps.println(provider_code);
        ps.print("    user_id: ");
        ps.println(user_id);
        ps.print("    data_type_code: ");
        ps.println(data_type_code);

        if (user_structure != null) {
            user_structure.print(ps);
        }

        if (bytes != null) {
            ps.print("    bytes: 0x");
            ps.println(toHexString(bytes));
        }
    }
}
