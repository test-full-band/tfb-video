package band.full.test.video.encoder;

import static java.lang.ProcessBuilder.Redirect.INHERIT;

import java.io.File;
import java.io.IOException;

/**
 * @author Igor Malinin
 */
public class MuxerMP4Box extends Muxer {
    public MuxerMP4Box(File dir, String name, String brand) throws IOException {
        super(dir, name, brand);
    }

    @Override
    public String mux(String video, String audio)
            throws IOException, InterruptedException {
        File in = new File(dir, video);
        File out = new File(dir, name + MP4_SUFFIX);

        var builder = new ProcessBuilder(
                "MP4Box", "-noprog", "-new", out.getPath(), "-brand", brand,
                "-add", in.getPath(), "-add", audio
        ).redirectOutput(INHERIT).redirectErrorStream(true);

        System.out.println();
        System.out.println(builder.command());

        var process = builder.start();

        if (process.waitFor() != 0)
            throw new IOException("MP4Box finished with error: "
                    + process.exitValue());

        return name + MP4_SUFFIX;
    }
}
