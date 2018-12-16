package band.full.test.video.encoder;

import java.io.File;
import java.io.IOException;

/**
 * @author Igor Malinin
 */
public abstract class Muxer {
    public static final String MP4_SUFFIX = ".mp4";

    public final File dir;
    public final String name;
    public final String brand;

    public Muxer(File dir, String name, String brand) throws IOException {
        this.dir = dir;
        this.name = name;
        this.brand = brand;
    }

    public abstract String mux(String video, String audio)
            throws IOException, InterruptedException;
}
