package band.full.testing.video.encoder;

import static band.full.testing.video.core.Framerate.toFrames;
import static java.lang.Boolean.getBoolean;
import static java.lang.Math.min;
import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.lang.System.arraycopy;
import static java.lang.System.getProperty;
import static java.nio.charset.StandardCharsets.US_ASCII;

import band.full.testing.video.core.FrameBuffer;
import band.full.testing.video.itu.ColorMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.function.Supplier;

/**
 * @author Igor Malinin
 */
public abstract class EncoderY4M implements AutoCloseable {
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

    static final IO Y4M = IO.get("encoder.file.y4m");

    final IO OUT = IO.get("encoder.file." + getFormat());

    private static final byte[] FRAME_HEADER = "FRAME\n".getBytes(US_ASCII);

    public static final boolean LOSSLESS = getBoolean("encoder.lossless");

    /**
     * Quick mode limits duration of render calls to 15 frames and frame rate to
     * one frame per second. This is useful for quick testing the build
     * procedures. Produced videos will not be fully usable!
     */
    public static final boolean QUICK = getBoolean("encoder.quick");

    private int qframes;

    public final String name;
    public final EncoderParameters parameters;

    public final ColorMatrix matrix;

    public final File dir;
    public final File y4m;
    public final File out;
    public final File mp4;

    private final byte[] frameBuffer;

    private Process process;
    private final OutputStream yuv4mpegOut;

    protected EncoderY4M(String name, EncoderParameters parameters)
            throws IOException {
        this.name = name;
        this.parameters = parameters;

        matrix = parameters.matrix;

        String root = "target/testing-video"
                + (LOSSLESS ? "-lossless" : "");

        String prefix = root + "/" + name;

        y4m = new File(prefix + ".y4m");
        out = new File(prefix + "." + getFormat());
        mp4 = new File(prefix + ".mp4");

        dir = mp4.getParentFile();

        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) throw new IOException(
                    "Cannot create directory: " + dir);
        }

        var resolution = parameters.resolution;

        int frameLength = resolution.width * resolution.height * 3
                / 2 * y4mBytesPerSample();
        frameBuffer = new byte[FRAME_HEADER.length + frameLength];
        arraycopy(FRAME_HEADER, 0, frameBuffer, 0, FRAME_HEADER.length);

        yuv4mpegOut = open();

        String header = "YUV4MPEG2"
                + " W" + resolution.width + " H" + resolution.height
                + " F" + (QUICK ? "1:1" : parameters.framerate)
                + " Ip A1:1 C420p" + matrix.bitdepth + "\n";

        yuv4mpegOut.write(header.getBytes(US_ASCII));
    }

    private OutputStream open() throws IOException {
        if (!Y4M.isPipe()) return new FileOutputStream(y4m);

        if (QUICK) {
            System.err.println("Encoding in QUICK mode."
                    + " This is only for build testing purposes, "
                    + "resulting files will not be fully usable!");
        }

        if (LOSSLESS) {
            System.out.println("Generating lossless encode...");
        } else {
            System.out.println("Generating normal encode...");
        }

        var builder = createProcessBuilder();

        System.out.println(builder.command());

        process = builder.start();

        return process.getOutputStream();
    }

    // TODO lost a call
    protected abstract boolean checkBitdepth(int depth);

    public abstract String getExecutable();

    public abstract String getFormat();

    protected abstract ProcessBuilder createProcessBuilder();

    protected String getPresetParam() {
        if (QUICK) return "ultrafast";

        switch (parameters.preset) {
            // speed-up lossless encode a bit as we do not trade it for quality
            case FAST:
                return LOSSLESS ? "ultrafast" : "fast";
            case SLOW:
                return LOSSLESS ? "fast" : "slow";
            case VERYSLOW:
                return "veryslow";
            case PLACEBO:
                return "placebo";

            default:
                throw new IllegalStateException("preset=" + parameters.preset);
        }
    }

    @Override
    public void close() throws IOException, InterruptedException {
        yuv4mpegOut.close();

        if (!Y4M.isPipe()) {
            var builder = createProcessBuilder();

            System.out.println(builder.command());

            process = builder.start();
        }

        int result = process.waitFor();
        if (result != 0)
            throw new IOException(getExecutable() + " failed: " + result);

        if (!Y4M.isKeepFile()) {
            y4m.delete();
        }

        mp4.delete(); // force overwrite

        var builder = new ProcessBuilder("MP4Box",
                "-add", out.getPath(),
                "-brand", "hvc1", mp4.getPath())
                        .redirectOutput(INHERIT)
                        .redirectError(INHERIT);

        builder.command().addAll(parameters.muxerOptions);

        System.out.println();
        System.out.println(builder.command());

        builder.start().waitFor();

        if (!OUT.isKeepFile()) {
            out.delete();
        }
    }

    private void fillFrame(int offset, short[] pixels) {
        boolean extended = matrix.bitdepth > 8;
        byte[] buf = frameBuffer;

        for (int i = 0, j = offset; i < pixels.length; i++) {
            int sample = pixels[i];

            // LittleEndian
            buf[j++] = (byte) sample;
            if (extended) {
                buf[j++] = (byte) (sample >>> 8);
            }
        }
    }

    public void render(FrameBuffer fb) {
        int bps = y4mBytesPerSample();

        int offsetY = FRAME_HEADER.length;
        int offsetCb = offsetY + fb.Y.pixels.length * bps;
        int offsetCr = offsetCb + fb.U.pixels.length * bps;

        fillFrame(offsetY, fb.Y.pixels);
        fillFrame(offsetCb, fb.U.pixels);
        fillFrame(offsetCr, fb.V.pixels);

        try {
            yuv4mpegOut.write(frameBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void render(int frames, Supplier<FrameBuffer> supplier) {
        if (QUICK && parameters.framerate.rate > 1.0) {
            int rate = (int) parameters.framerate.rate;

            if (qframes > rate * 60) return;
            frames = min(frames, rate * 10);

            for (int i = qframes % rate; i < frames; i += rate) {
                render(supplier.get());
            }

            qframes += frames;
            return;
        }

        for (int i = 0; i < frames; i++) {
            render(supplier.get());
        }
    }

    public void render(Duration duration, Supplier<FrameBuffer> supplier) {
        render(toFrames(parameters.framerate.rate, duration), supplier);
    }

    private int y4mBytesPerSample() {
        return matrix.bitdepth > 8 ? 2 : 1;
    }

    public FrameBuffer newFrameBuffer() {
        return new FrameBuffer(parameters.resolution, matrix);
    }
}
