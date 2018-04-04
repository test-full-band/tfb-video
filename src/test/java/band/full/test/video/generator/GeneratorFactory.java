package band.full.test.video.generator;

import static java.lang.Boolean.getBoolean;
import static java.lang.System.getProperty;

import band.full.video.encoder.DecoderY4M;
import band.full.video.encoder.EncoderAVC;
import band.full.video.encoder.EncoderHEVC;
import band.full.video.encoder.EncoderParameters;
import band.full.video.encoder.EncoderY4M;
import band.full.video.encoder.MuxerMP4;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public enum GeneratorFactory {
    AVC(EncoderAVC::encode, "isom" /* avc1 */, "H.264-AVC"),
    HEVC(EncoderHEVC::encode, "hvc1", "H.265-HEVC");

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

    public static final boolean LOSSLESS = getBoolean("encoder.lossless");

    // File y4m = new File(prefix + ".y4m");
    // File out = new File(prefix + "." + getFormat());
    // File mp4 = new File(prefix + ".mp4");

    GeneratorFactory(Encoder encoder, String brand, String folder) {
        this.encoder = encoder;
        this.brand = brand;
        this.folder = folder;
    }

    private File greet(String folder, String name) {
        System.out.println(LOSSLESS
                ? "Generating lossless encode..."
                : "Generating normal encode...");

        return new File("target/video-"
                + (LOSSLESS ? "lossless" : "main")
                + "/" + folder + "/" + name).getParentFile();
    }

    public void generate(String folder, String name, EncoderParameters ep,
            Consumer<EncoderY4M> ec, Consumer<DecoderY4M> dc) {
        File dir = greet(folder, name);
        try {
            String out = encoder.encode(dir, name, ep, ec);
            String mp4 = new MuxerMP4(dir, name, brand, ep.muxerOptions)
                    .addInput(out).mux();
            DecoderY4M.decode(dir, mp4, ep, dc);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public <A> void generate(String folder, String name, EncoderParameters ep,
            A args,
            ParametrizedConsumer<EncoderY4M, A> ec,
            ParametrizedConsumer<DecoderY4M, A> dc) {
        File dir = greet(folder, name);
        try {
            String out = encoder.encode(dir, name, ep, e -> ec.accept(e, args));
            String mp4 = new MuxerMP4(dir, name, brand, ep.muxerOptions)
                    .addInput(out).mux();
            DecoderY4M.decode(dir, mp4, ep, d -> dc.accept(d, args));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    interface Encoder {
        String encode(File dir, String name, EncoderParameters ep,
                Consumer<EncoderY4M> consumer)
                throws IOException, InterruptedException;
    }

    @FunctionalInterface
    interface ParametrizedConsumer<T, A> {
        void accept(T t, A args);
    }
}
