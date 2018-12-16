package band.full.video.itu.nal;

import static java.lang.String.format;
import static java.lang.System.arraycopy;

import java.io.IOException;
import java.io.InputStream;

public abstract class NalReaderAnnexB<C extends NalContext,
        U extends NalUnit<C>> extends NalReader<C, U> {
    private byte[] buf = new byte[1_048_576]; // 1MB = 8Mb

    /** RBSP read state: start of unread nal_unit() bytes */
    private int offset;

    /** RBSP read state: end of unread buffer bytes */
    private int limit;

    /** RBSP parse state: searching for next NAL unit separator */
    private int end, next;

    private boolean eof;

    public NalReaderAnnexB(InputStream in) throws IOException {
        super(in);

        while (limit < 4 && !eof) {
            int n = in.read(buf, limit, buf.length - limit);
            if (n < 0) {
                eof = true;
                break;
            }
            limit += n;
        }

        if (buf[0] != 0 || buf[1] != 0 || buf[2] != 0 || buf[3] != 1)
            throw new IOException("Not an Annex.B stream");

        end = 0;
        next = offset = 4;
    }

    @Override
    public U read() throws IOException {
        if (next == end) return null;
        boolean zero_byte = next - end > 3;
        U u = create(context(), nalu());
        u.zero_byte = zero_byte;
        return u;
    }

    RbspReader nalu() throws IOException {
        loadStartCodePrefix();
        int j = removeEmulationPreventionBytes(buf, offset, end);
        var in = new RbspReader(buf, offset, j - offset);
        offset = next;
        return in;
    }

    private void loadStartCodePrefix() throws IOException {
        while (!findStartCodePrefix()) {
            if (buf.length - limit + offset < 65536) {
                move(new byte[buf.length * 2]);
            } else if (offset >= 65536) {
                move(buf); // just compact buffer
            }

            int n = in.read(buf, limit, buf.length - limit);
            if (n < 0) {
                eof = true;
                continue;
            }
            limit += n;
        }
    }

    private void move(byte[] dest) {
        int diff = offset - 4;
        arraycopy(buf, diff, buf = dest, 0, limit - diff);
        end -= diff;
        next -= diff;
        limit -= diff;
        offset = 4; // move with prefix data
    }

    boolean findStartCodePrefix() throws IOException {
        int pos = offset;

        for (int x = limit - 2; pos < x;) {
            if (buf[pos++] != 0x00) {
                continue;
            }

            // 0x00

            if (buf[pos++] != 0x00) {
                continue;
            }

            // 0x00_00

            switch (buf[pos++]) {
                case 0x00: // 0x00_00_00
                    if (pos == limit) {
                        if (eof) // 0x00_00_00_EOF
                            throw new IOException(INVALID_NALU + "0x000000");
                        else {
                            end = pos - 3;
                            return false;
                        }
                    }

                    if (buf[pos++] == 0x01) {
                        // 0x00_00_00_01
                        end = pos - 4;
                        next = pos;
                        return true;
                    }

                    // 0x00_00_00_xx where xx != 0x01
                    throw new IOException(format(
                            INVALID_NALU + "0x000000%02X",
                            buf[pos]));

                case 0x01: // 0x00_00_01
                    end = pos - 3;
                    next = pos;
                    return true;

                case 0x02: // 0x00_00_02
                    throw new IOException(INVALID_NALU + "0x000002");

                case 0x03: // 0x00_00_03
                    if (pos < limit) {
                        if ((buf[pos] & 0b11111100) == 0) {
                            continue; // <= 0x03
                        }

                        throw new IOException(INVALID_NALU + "0x000003"
                                + format("%02X", buf[pos]));
                    }

                    if (eof) {
                        // 0x00_00_03_EOF
                        end = next = pos;
                        return true;
                    }

                    end = pos - 3;
                    return false;

                default:
                    // just skip emulation prevention byte
                    // and higher codes
            }
        }

        if (eof) { // 0xXX_XX_EOF
            end = next = limit;
            return true;
        }

        end = pos;
        return false;
    }
}
