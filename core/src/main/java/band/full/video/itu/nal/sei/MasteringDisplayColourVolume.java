package band.full.video.itu.nal.sei;

import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

/**
 * @author Igor Malinin
 */
@SuppressWarnings("rawtypes")
public class MasteringDisplayColourVolume implements Payload {
    public int display_primaries_x[] = new int[3]; // u(16)
    public int display_primaries_y[] = new int[3]; // u(16)

    public int white_point_x; // u(16)
    public int white_point_y; // u(16)

    // they are actually specified as unsigned but are capable to fit
    // all luminance values of HDR and above up to 214748.3647 cd/m2
    public int max_display_mastering_luminance; // u(32)
    public int min_display_mastering_luminance; // u(32)

    public MasteringDisplayColourVolume() {}

    public MasteringDisplayColourVolume(NalContext context, RbspReader in,
            int size) {
        if (size != size(context)) throw new IllegalArgumentException();
        read(context, in);
    }

    @Override
    public int size(NalContext context) {
        return 24;
    }

    @Override
    public void read(NalContext context, RbspReader in) {
        for (int i = 0; i < 3; i++) {
            display_primaries_x[i] = in.u16();
            display_primaries_y[i] = in.u16();
        }

        white_point_x = in.u16();
        white_point_y = in.u16();
        max_display_mastering_luminance = in.i32();
        min_display_mastering_luminance = in.i32();
    }

    @Override
    public void write(NalContext context, RbspWriter out) {
        for (int i = 0; i < 3; i++) {
            out.u16(display_primaries_x[i]);
            out.u16(display_primaries_y[i]);
        }

        out.u16(white_point_x);
        out.u16(white_point_y);
        out.i32(max_display_mastering_luminance);
        out.i32(min_display_mastering_luminance);
    }

    private static final char[] COLORS = {'G', 'B', 'R'};

    @Override
    public void print(NalContext context, RbspPrinter out) {
        StringBuilder buf = new StringBuilder(88);
        for (int i = 0; i < 3; i++) {
            buf.append(COLORS[i]).append('(')
                    .append(display_primaries_x[i]).append(',')
                    .append(display_primaries_y[i]).append(')');
        }

        buf.append("WP(")
                .append(white_point_x).append(',')
                .append(white_point_y).append(")L(")
                .append(max_display_mastering_luminance).append(',')
                .append(min_display_mastering_luminance).append(')');

        out.raw(buf.toString());
    }
}
