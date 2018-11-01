package band.full.test.video.generator;

import static java.lang.Boolean.getBoolean;
import static java.lang.System.getProperty;
import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;

import band.full.test.video.encoder.EncoderAVC;
import band.full.test.video.encoder.EncoderHEVC;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public enum GeneratorFactory {
    AVC(EncoderAVC::encode, "isom" /* avc1 */, "H.264-AVC") {
        @Override
        EncoderParameters lossless(EncoderParameters template) {
            return template.withEncoderOptions(prepend(template.encoderOptions,
                    "--preset", getPresetParam(), "--qp", "0"));
        }

        @Override
        EncoderParameters bluray(EncoderParameters template) {
            return template.withEncoderOptions(prepend(template.encoderOptions,
                    "--preset", getPresetParam(),
                    // "--tune", "film", "--slices", "4",
                    "--bluray-compat", "--level", "4.1",
                    "--vbv-maxrate", "40000", "--vbv-bufsize", "30000",
                    "--crf", "1", "--qpmax", "4", "--psnr", "--ssim"));
        }
    },

    HEVC(EncoderHEVC::encode, "hvc1", "H.265-HEVC") {
        @Override
        EncoderParameters lossless(EncoderParameters template) {
            return template.withEncoderOptions(prepend(template.encoderOptions,
                    "--preset", getPresetParam(), "--lossless"));
        }

        @Override
        EncoderParameters bluray(EncoderParameters template) {
            return template.withEncoderOptions(prepend(template.encoderOptions,
                    "--preset", getPresetParam(),
                    // "--uhd-bd",
                    "--level-idc", "5.1", "--high-tier", "--hrd",
                    "--vbv-maxrate", "160000", "--vbv-bufsize", "160000",
                    "--crf", "0", "--qpmax", "4", "--cu-lossless",
                    "--no-rskip", "--psnr", "--ssim"));
        }
    };

    /**
     * Quick mode limits duration of render calls to 15 frames and frame rate to
     * one frame per second. This is useful for quick testing the build
     * procedures. Produced videos will not be fully usable!
     */
    public static final boolean QUICK = getBoolean("encoder.quick");

    public static final boolean LOSSLESS = getBoolean("encoder.lossless");

    abstract EncoderParameters lossless(EncoderParameters template);

    abstract EncoderParameters bluray(EncoderParameters template);

    static String[] prepend(List<String> options, String... args) {
        return concat(stream(args), options.stream()).toArray(String[]::new);
    }

    protected String getPresetParam() {
        // speed-up quick mode to fastest possible
        if (QUICK) return "ultrafast";
        // speed-up lossless encode a bit as we do not trade it for quality
        if (LOSSLESS) return "fast";
        // reasonable speed/quality ratio for production
        return "slow";
    }

    enum IO {
        PIPE, TEMP_FILE, KEEP_FILE;

        private static IO get(String property) {
            switch (getProperty(property, "pipe")) {
                case "temp":
                    return TEMP_FILE;
                case "keep":
                    return KEEP_FILE;
            }
            return PIPE;
        }

        boolean isPipe() {
            return this == PIPE;
        }

        boolean isKeepFile() {
            return this == IO.KEEP_FILE;
        }
    }

    final IO OUT = IO.get("encoder.file.annexb");

    public final Encoder encoder;
    public final String brand;
    public final String folder;

    GeneratorFactory(Encoder encoder, String brand, String folder) {
        this.encoder = encoder;
        this.brand = brand;
        this.folder = folder;
    }

    public File greet(String folder, String name) {
        System.out.println(LOSSLESS
                ? "Generating lossless encode..."
                : "Generating normal encode...");

        return new File("target/video-"
                + (LOSSLESS ? "lossless/" : "main/")
                + folder);
    }

    private EncoderParameters enrich(EncoderParameters ep) {
        return LOSSLESS ? lossless(ep) : bluray(ep);
    }

    public String encode(File dir, String name,
            EncoderParameters ep, Consumer<EncoderY4M> ec)
            throws IOException, InterruptedException {
        return encoder.encode(dir, name, enrich(ep), ec);
    }

    @FunctionalInterface
    interface Encoder {
        String encode(File dir, String name, EncoderParameters ep,
                Consumer<EncoderY4M> consumer)
                throws IOException, InterruptedException;
    }
}
