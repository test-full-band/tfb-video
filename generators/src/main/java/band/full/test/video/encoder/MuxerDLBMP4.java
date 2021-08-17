package band.full.test.video.encoder;

import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.exists;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

/**
 * @author Igor Malinin
 */
public class MuxerDLBMP4 extends Muxer {
    private static final String MP4BASE_GITHUB =
            "https://github.com/DolbyLaboratories/dlb_mp4base/raw/master/bin/";

    private static final boolean OS_WINDOWS;
    private static final boolean OS_MAC;

    private static final String MP4MUXER;

    static {
        String os = System.getProperty("os.name");

        OS_WINDOWS = os.contains("Windows");
        OS_MAC = os.contains("Mac");

        MP4MUXER = "target/" + (OS_WINDOWS ? "mp4muxer.exe" : "mp4muxer");
    }

    private static synchronized void downloadMuxerBinary() {
        Path path = Path.of(MP4MUXER);
        if (exists(path)) return;

        try {
            String suffix = OS_WINDOWS ? ".exe" : OS_MAC ? "_mac" : "";
            URL url = new URL(MP4BASE_GITHUB + "mp4muxer" + suffix);

            try (var in = url.openStream()) {
                copy(in, path);
            }

            if (!OS_WINDOWS) {
                var process = new ProcessBuilder(
                        "chmod", "+x", MP4MUXER
                ).redirectOutput(INHERIT).redirectErrorStream(true).start();

                if (process.waitFor() != 0)
                    throw new IOException("chmod finished with error: "
                            + process.exitValue());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public final String profile;

    public MuxerDLBMP4(File dir, String name, String brand, String profile)
            throws IOException {
        super(dir, name, brand);
        this.profile = profile;

        downloadMuxerBinary();
    }

    @Override
    public String mux(String video, String audio)
            throws IOException, InterruptedException {
        File in = new File(dir, video);
        File out = new File(dir, name + MP4_SUFFIX);

        var builder = new ProcessBuilder(
                // "MP4Box", "-noprog", "-new", out.getPath(),
                // "-brand", "mp42", "-ab", brand, // "-no-iod",
                // "-add", in.getPath() + ":dv-profile=5", "-add", audio
                // TODO
                MP4MUXER, "-o", out.getPath(), "--overwrite",
                "-i", in.getPath(), "-i", audio,
                "--dv-profile", profile, "--mpeg4-comp-brand", brand
        ).redirectOutput(INHERIT).redirectErrorStream(true);

        System.out.println();
        System.out.println(builder.command());

        var process = builder.start();

        if (process.waitFor() != 0)
            throw new IOException("mp4muxer finished with error: "
                    + process.exitValue());

        return name + MP4_SUFFIX;
    }
}
