package band.full.test.video.generator;

import band.full.test.video.encoder.Muxer;

import java.io.File;
import java.io.IOException;

@FunctionalInterface
public interface MuxerFactory {
    Muxer create(File dir, String pattern, String brand) throws IOException;
}
