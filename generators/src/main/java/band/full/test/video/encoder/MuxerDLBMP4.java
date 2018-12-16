package band.full.test.video.encoder;

import static java.lang.ProcessBuilder.Redirect.INHERIT;

import java.io.File;
import java.io.IOException;

/**
 * @author Igor Malinin
 */
public class MuxerDLBMP4 extends Muxer {
    public final String profile;

    public MuxerDLBMP4(File dir, String name, String brand, String profile)
            throws IOException {
        super(dir, name, brand);
        this.profile = profile;
    }

    @Override
    public String mux(String video, String audio)
            throws IOException, InterruptedException {
        File in = new File(dir, video);
        File out = new File(dir, name + MP4_SUFFIX);

        var builder = new ProcessBuilder(
                // TODO
                "/Users/igor/work/tfb/mp4muxer", "-o", out.getPath(),
                "--overwrite",
                "-i", in.getPath(), "-i", audio,
                "--dv-profile", profile, "--mpeg4-comp-brand", brand
        ).redirectOutput(INHERIT).redirectError(INHERIT);

        System.out.println();
        System.out.println(builder.command());

        Process process = builder.start();

        if (process.waitFor() != 0)
            throw new IOException("mp4muxer finished with error: "
                    + process.exitValue());

        return name + MP4_SUFFIX;
    }
}
