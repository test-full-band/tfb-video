package band.full.test.video.encoder;

import static band.full.video.itu.ColorRange.FULL;
import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.util.Collections.addAll;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public class EncoderHEVC extends EncoderY4M {
    public static final String HEVC_SUFFIX = ".hevc";

    protected EncoderHEVC(File dir, String name, EncoderParameters parameters)
            throws IOException {
        super(dir, name, parameters);
    }

    @Override
    public boolean checkBitdepth(int depth) {
        return depth == 8 || depth == 10 || depth == 12;
    }

    @Override
    protected ProcessBuilder createProcessBuilder() {
        int rate = (int) (parameters.framerate.rate + 0.5f);
        int bitdepth = matrix.bitdepth;
        int colorprim = matrix.primaries.code;
        int transfer = matrix.transfer.code();
        int colormatrix = matrix.code;

        var builder = new ProcessBuilder(
                getExecutable(),
                Y4M.isPipe() ? "-" : name + ".y4m", name + HEVC_SUFFIX, "--y4m",
                "--profile", bitdepth == 8 ? "main" : "main" + bitdepth,
                "--colorprim", Integer.toString(colorprim),
                "--transfer", Integer.toString(transfer),
                "--colormatrix", Integer.toString(colormatrix),
                "--chromaloc", "2", // chroma_loc_info_present_flag
                "--repeat-headers", // [ref.1]
                "--no-opt-qp-pps", // repeat HDR SEI and
                "--no-opt-ref-list-length-pps", // avoid MP4Box errors
                "--keyint", Integer.toString(rate), "--no-open-gop", "--aud",
                "--range", matrix.range == FULL ? "full" : "limited",
                "--output-depth", Integer.toString(bitdepth)
        ).directory(dir).redirectOutput(INHERIT).redirectError(INHERIT);
        // TODO: passing/detecting chromaloc; avoid hardcoding

        var command = builder.command();

        parameters.masterDisplay.ifPresent(
                md -> addAll(command, "--master-display", md));

        command.addAll(parameters.encoderOptions);

        return builder;
    }

    @Override
    public String getExecutable() {
        return "x265";
    }

    public static String encode(File dir, String name,
            EncoderParameters parameters, Consumer<EncoderY4M> consumer)
            throws IOException, InterruptedException {
        try (EncoderHEVC encoder = new EncoderHEVC(dir, name, parameters)) {
            consumer.accept(encoder);
            return name + HEVC_SUFFIX;
        }
    }
}

// References:
// 1. https://bitbucket.org/multicoreware/x265/issues/309/mp4box-incompatibility
// 2. https://forum.doom9.org/showthread.php?p=1803907#post1803907

// "--ref=5", "--limit-refs=0",
// "--bframes=0",
// "--no-amp",
// "--no-tskip",
// "--limit-modes",
// "--no-b-pyramid",
// "--rskip",
// "--ipratio=1.40",
// "--qg-size=32",
// "--max-cll", "1000,400",
