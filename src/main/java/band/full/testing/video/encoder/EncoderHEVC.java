package band.full.testing.video.encoder;

import static band.full.testing.video.encoder.EncoderParameters.MAIN8;
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
    protected void addEncoderParams(List<String> command) {
        super.addEncoderParams(command);

        if (LOSSLESS) {
            command.add("--lossless");
        } else if (!QUICK) {
            addAll(command, "--cu-lossless");
        }

        addAll(command, "--repeat-headers", "--range=limited",
                "--output-depth=" + parameters.bitdepth);
    }

    @Override
    public String getExecutable() {
        return "x265";
    }

    @Override
    public String getFFMpegFormat() {
        return "hevc";
    }

    public static void encode(String name, Consumer<EncoderY4M> consumer) {
        encode(name, MAIN8, consumer);
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
