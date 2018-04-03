package band.full.video.encoder;

import static band.full.video.itu.ColorRange.FULL;
import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.util.Collections.addAll;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public class EncoderHEVC extends EncoderY4M {
    protected EncoderHEVC(String name, EncoderParameters parameters)
            throws IOException {
        super(name, parameters);
    }

    @Override
    public boolean checkBitdepth(int depth) {
        return depth == 8 || depth == 10 || depth == 12;
    }

    @Override
    protected ProcessBuilder createProcessBuilder() {
        var builder = new ProcessBuilder(getExecutable(),
                Y4M.isPipe() ? "-" : y4m.getPath(),
                out.getPath())
                        .redirectOutput(INHERIT)
                        .redirectError(INHERIT);

        int bitdepth = matrix.bitdepth;
        int colorprim = matrix.primaries.code;
        int transfer = matrix.transfer.code();
        int colormatrix = matrix.code;

        var command = builder.command();

        addAll(command, "--y4m", "--preset", getPresetParam(),
                "--profile", bitdepth == 8 ? "main" : "main" + bitdepth);

        // TODO: passing/detecting chromaloc; avoid hardcoding
        addAll(builder.command(),
                "--colorprim", Integer.toString(colorprim),
                "--transfer", Integer.toString(transfer),
                "--colormatrix", Integer.toString(colormatrix),
                "--chromaloc", "2"); // chroma_loc_info_present_flag

        parameters.masterDisplay.ifPresent(
                md -> addAll(command, "--master-display", md));

        command.addAll(parameters.encoderOptions);

        if (LOSSLESS) {
            command.add("--lossless");
        } else {
            if (!QUICK) {
                command.add("--cu-lossless");
            }

            addAll(command, // "--tune", "film", "--uhd-bd",
                    "--level-idc", "5.1", "--high-tier", "--hrd",
                    "--vbv-maxrate", "160000", "--vbv-bufsize", "160000");
        }

        int rate = (int) (parameters.framerate.rate + 0.5f);

        addAll(command, "--aud", "--no-open-gop",
                "--repeat-headers", // [ref.1]
                "--no-opt-qp-pps", // repeat HDR SEI and
                "--no-opt-ref-list-length-pps", // avoid MP4Box errors
                "--keyint", Integer.toString(rate), "--min-keyint", "1",
                "--range", matrix.range == FULL ? "full" : "limited",
                "--output-depth", Integer.toString(bitdepth));

        return builder;
    }

    @Override
    public String getExecutable() {
        return "x265";
    }

    @Override
    public String getFormat() {
        return "hevc";
    }

    @Override
    public String getBrand() {
        return "hvc1";
    }

    public static void encode(String name, EncoderParameters parameters,
            Consumer<EncoderY4M> consumer) {
        try (EncoderHEVC encoder = new EncoderHEVC(name, parameters)) {
            consumer.accept(encoder);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

// References:
// 1. https://bitbucket.org/multicoreware/x265/issues/309/mp4box-incompatibility
// 2. https://forum.doom9.org/showthread.php?p=1803907#post1803907

// "--ref=5",
// "--limit-refs=0",
// "--sao",
// "--aq-mode=1",
// "--aq-strength=1.00",
// "--cutree",
// "--bframes=0",
// "--no-amp",
// "--no-tskip",
// "--limit-modes",
// "--no-b-pyramid",
// "--rd=4",
// "--rskip",
// "--psy-rd=2.00",
// "--psy-rdoq=1.00",
// "--qpstep=4",
// "--bitrate=40000",
// "--vbv-maxrate=40000",
// "--vbv-bufsize=40000",
// "--vbv-init=0.9",
// "--ipratio=1.40",
// "--qg-size=32",
// "--max-cll", "1000,400",

// "--tune=grain",
