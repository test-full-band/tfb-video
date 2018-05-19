package band.full.video.itu.nal;

import java.io.PrintStream;

public interface Structure<C extends NalContext> {
    void read(C context, RbspReader reader);

    void write(C context, RbspWriter writer);

    void print(C context, PrintStream ps);
}
