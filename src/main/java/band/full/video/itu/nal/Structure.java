package band.full.video.itu.nal;

import java.io.PrintStream;

public interface Structure {
    void read(RbspReader reader);

    void write(RbspWriter writer);

    void print(PrintStream ps);
}
