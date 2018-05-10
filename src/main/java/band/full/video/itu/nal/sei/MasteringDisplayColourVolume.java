package band.full.video.itu.nal.sei;

import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

import java.io.PrintStream;

/**
 * @author Igor Malinin
 */
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

    public MasteringDisplayColourVolume(RbspReader reader, int size) {
        if (size != size()) throw new IllegalArgumentException();
        read(reader);
    }

    @Override
    public int size() {
        return 24;
    }

    @Override
    public void read(RbspReader reader) {
        for (int i = 0; i < 3; i++) {
            display_primaries_x[i] = reader.readUInt(16);
            display_primaries_y[i] = reader.readUInt(16);
        }

        white_point_x = reader.readUInt(16);
        white_point_y = reader.readUInt(16);
        max_display_mastering_luminance = reader.readS32();
        min_display_mastering_luminance = reader.readS32();
    }

    @Override
    public void write(RbspWriter writer) {
        for (int i = 0; i < 3; i++) {
            writer.writeU(16, display_primaries_x[i]);
            writer.writeU(16, display_primaries_y[i]);
        }

        writer.writeU(16, white_point_x);
        writer.writeU(16, white_point_y);
        writer.writeS32(max_display_mastering_luminance);
        writer.writeS32(min_display_mastering_luminance);
    }

    private static final String[] COLORS = {"Red", "Green", "Blue", "White"};

    @Override
    public void print(PrintStream ps) {
        for (int i = 0; i < 3; i++) {
            ps.print("      ");
            ps.println(COLORS[i]);
            ps.print("        display_primaries_x: ");
            ps.println(display_primaries_x[i]);
            ps.print("        display_primaries_y: ");
            ps.println(display_primaries_y[i]);
        }

        ps.print("      white_point_x: ");
        ps.println(white_point_x);
        ps.print("      white_point_y: ");
        ps.println(white_point_y);
        ps.print("      max_display_mastering_luminance: ");
        ps.println(max_display_mastering_luminance);
        ps.print("      min_display_mastering_luminance: ");
        ps.println(min_display_mastering_luminance);
    }
}
