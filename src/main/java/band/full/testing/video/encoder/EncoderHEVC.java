package band.full.testing.video.encoder;

import static band.full.testing.video.encoder.DecoderY4M.decode;
import static band.full.testing.video.encoder.EncoderParameters.UHD4K_MAIN8;
import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.util.Collections.addAll;

import java.io.IOException;
import java.util.List;
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
        ProcessBuilder builder = new ProcessBuilder(getExecutable(),
                IO.Y4M.isPipe() ? "-" : y4m.getPath(),
                out.getPath())
                        .redirectOutput(INHERIT)
                        .redirectError(INHERIT);

        int bitdepth = parameters.bitdepth;

        List<String> command = builder.command();

        addAll(command, "--y4m", "--preset", getPresetParam(),
                "--profile", bitdepth == 8 ? "main" : "main" + bitdepth);

        encoderParameters.masterDisplay.ifPresent(
                md -> addAll(command, "--master-display", md));

        command.addAll(encoderParameters.encoderOptions);

        if (LOSSLESS) {
            command.add("--lossless");
        } else if (!QUICK) {
            addAll(command, "--cu-lossless");
        }

        addAll(command, "--repeat-headers",
                "--no-opt-qp-pps", "--no-opt-ref-list-length-pps", // [ref.1]
                "--range", parameters.range.toString(),
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

    public static void encode(String name, Consumer<EncoderY4M> consumer) {
        encode(name, UHD4K_MAIN8, consumer);
    }

    public static void encode(String name,
            Consumer<EncoderY4M> ec, Consumer<DecoderY4M> dc) {
        encode(name, UHD4K_MAIN8, ec, dc);
    }

    public static void encode(String name, EncoderParameters parameters,
            Consumer<EncoderY4M> consumer) {
        try (EncoderHEVC encoder = new EncoderHEVC(name, parameters)) {
            consumer.accept(encoder);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void encode(String name, EncoderParameters parameters,
            Consumer<EncoderY4M> ec, Consumer<DecoderY4M> dc) {
        encode(name, parameters, ec);
        decode(name, parameters, dc);
    }
}

// References:
// 1. https://bitbucket.org/multicoreware/x265/issues/309/mp4box-incompatibility
// 2. https://forum.doom9.org/showthread.php?p=1803907#post1803907

// "--level-idc=51",
// "--ref=5",
// "--limit-refs=0",
// "--sao",
// "--aq-mode=1",
// "--aq-strength=1.00",
// "--cutree",
// "--min-keyint=2",
// "--keyint=24",
// "--bframes=0",
// "--no-amp",
// "--no-tskip",
// "--limit-modes",
// "--repeat-headers",
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
