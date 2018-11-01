package band.full.test.video.encoder;

import static java.lang.ProcessBuilder.Redirect.INHERIT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Igor Malinin
 */
public class MuxerMP4 {
    public static final String MP4_SUFFIX = ".mp4";

    public final List<String> inputs = new ArrayList<>();

    public final File dir;
    public final String name;
    public final String brand;
    public final List<String> args;

    public MuxerMP4(File dir, String name, String brand, List<String> args)
            throws IOException {
        this.dir = dir;
        this.name = name;
        this.brand = brand;
        this.args = args;

        if (!dir.isDirectory() && !dir.mkdirs())
            throw new IOException("Cannot create directory: " + dir);
    }

    public MuxerMP4 addInput(String name) {
        inputs.add(name);
        return this;
    }

    public String mux() throws IOException, InterruptedException {
        if (inputs.size() == 0)
            throw new IllegalStateException("No inputs specified!");

        var builder = new ProcessBuilder(
                "MP4Box", "-noprog", "-new", name + MP4_SUFFIX, "-brand", brand
        ).directory(dir).redirectOutput(INHERIT).redirectError(INHERIT);

        List<String> command = builder.command();
        command.addAll(args);

        command.add("-add");
        command.add(inputs.get(0));

        inputs.stream().skip(1)
                .flatMap(name -> Stream.of("-cat", name))
                .forEach(command::add);

        System.out.println();
        System.out.println("> " + dir);
        System.out.println(command);

        Process process = builder.start();

        if (process.waitFor() != 0)
            throw new IOException("MP4Box finished with error: "
                    + process.exitValue());

        return name + MP4_SUFFIX;
    }

    public void deleteInputs() {
        inputs.forEach(in -> new File(dir, in).delete());
    }
}
