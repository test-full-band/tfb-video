package band.full.test.video.generator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@FunctionalInterface
public interface NalUnitPostProcessor<A> {
    /** Just copy all phases by default. */
    static <A> NalUnitPostProcessor<A> defaultNalUnitPostProcessor() {
        // FIXME acutually need to parse input and filter only first codec
        // description SEI -> TO BE MOVED TO GeneratorFactory for AVC/HEVC
        return (args, fragment, in, out) -> {
            byte[] buf = new byte[16 * 1024];
            while (true) {
                int n = in.read(buf);
                if (n < 0) return;
                out.write(buf, 0, n);
            }
        };
    };

    /**
     * Allows to parse NAL units from input stream during joining and apply some
     * logic like modifying existing or inserting additional NAL units into the
     * video stream.
     */
    void process(A args, int fragment, InputStream in, OutputStream out)
            throws IOException;
}
