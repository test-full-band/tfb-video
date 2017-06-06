package band.full.testing.video.encoder;

import static band.full.testing.video.core.Framerate.toFrames;
import static java.lang.Boolean.getBoolean;
import static java.lang.Math.min;
import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.lang.System.arraycopy;
import static java.lang.System.getProperty;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Collections.addAll;

import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.core.Resolution;
import band.full.testing.video.itu.YCbCr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Igor Malinin
 */
public abstract class EncoderY4M implements AutoCloseable {
    enum IO {
        PIPE, TEMP_FILE, KEEP_FILE;

        static final IO Y4M = get("encoder.file.y4m");

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
            return Y4M == PIPE;
        }

        boolean isTempFile() {
            return Y4M == PIPE;
        }
    }

    protected static final boolean LOSSLESS = getBoolean("encoder.lossless");

    /**
     * Quick mode limits duration of render calls to 2 seconds. This is useful
     * for quick testing the build procedures. Produced videos will not be fully
     * usable!
     */
    protected static final boolean QUICK = getBoolean("encoder.quick");
    protected static final String QRATE = "1:1";
    protected static final int QFRAMES = 15;

    private static final byte[] FRAME_HEADER = "FRAME\n".getBytes(US_ASCII);

    private static final Field FILTER_OUT_FIELD;

    static {
        try {
            FILTER_OUT_FIELD = FilterOutputStream.class.getDeclaredField("out");
            FILTER_OUT_FIELD.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Assuming output stream is buffering FilterOutputStream return the
     * unwrapped instance.<br>
     * We don't need the buffering as we already have large buffers and very
     * efficient output. As raw video streams are extremely large we need every
     * bit of throughput.
     */
    static OutputStream unwrap(OutputStream out)
            throws IOException {
        if (out instanceof FilterOutputStream) {
            try {
                return (FileOutputStream) FILTER_OUT_FIELD.get(out);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IOException(e);
            }
        }

        // Use the original one on platforms where we don't know how to unwrap
        return out;
    }

    public final String name;
    public final YCbCr parameters;
    public final EncoderParameters encoderParameters;

    public final File dir;
    public final File y4m;
    public final File out;
    public final File mp4;

    private final byte[] frameBuffer;

    private Process process;
    private final OutputStream yuv4mpegOut;

    protected EncoderY4M(String name, EncoderParameters encoderParameters)
            throws IOException {
        this.name = name;
        this.encoderParameters = encoderParameters;
        parameters = encoderParameters.parameters;

        String root = "target/testing-video"
                + (LOSSLESS ? "-lossless" : "");

        String prefix = root + "/" + name;

        y4m = new File(prefix + ".y4m");
        out = new File(prefix + ".out");
        mp4 = new File(prefix + ".mp4");

        dir = mp4.getParentFile();

        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) throw new IOException(
                    "Cannot create directory: " + dir);
        }

        Resolution resolution = encoderParameters.resolution;

        int frameLength = resolution.width * resolution.height * 3
                / 2 * y4mBytesPerSample();
        frameBuffer = new byte[FRAME_HEADER.length + frameLength];
        arraycopy(FRAME_HEADER, 0, frameBuffer, 0, FRAME_HEADER.length);

        yuv4mpegOut = open();

        String header = "YUV4MPEG2"
                + " W" + resolution.width + " H" + resolution.height
                + " F" + (QUICK ? QRATE : encoderParameters.framerate)
                + " Ip A1:1 C420p" + parameters.bitdepth + "\n";

        yuv4mpegOut.write(header.getBytes(US_ASCII));
    }

    private OutputStream open() throws IOException {
        if (!IO.Y4M.isPipe()) return new FileOutputStream(y4m);

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

        ProcessBuilder builder = new ProcessBuilder(getExecutable(),
                "-", out.getPath())
                        .redirectOutput(INHERIT)
                        .redirectError(INHERIT);

        addEncoderParams(builder.command());

        System.out.println(builder.command());

        process = builder.start();

        return unwrap(process.getOutputStream());
    }

    protected abstract boolean checkBitdepth(int depth);

    public abstract String getExecutable();

    public abstract String getFFMpegFormat();

    protected void addEncoderParams(List<String> command) {
        addAll(command, "--y4m", getProfileParam());
        command.addAll(encoderParameters.encoderOptions);
    }

    private String getProfileParam() {
        if (QUICK) return "--preset=ultrafast";

        switch (encoderParameters.preset) {
            case FAST:
                return "--preset=fast";
            case SLOW:
                return "--preset=slow";
            case VERYSLOW:
                return "--preset=veryslow";
            case PLACEBO:
                return "--preset=placebo";

            default:
                return "--preset=slow";
        }
    }

    @Override
    public void close() throws IOException, InterruptedException {
        yuv4mpegOut.close();

        if (!IO.Y4M.isPipe()) {
            ProcessBuilder builder = new ProcessBuilder(getExecutable(),
                    y4m.getPath(), out.getPath())
                            .redirectOutput(INHERIT)
                            .redirectError(INHERIT);

            addEncoderParams(builder.command());

            System.out.println(builder.command());

            process = builder.start();
            process.waitFor();
        }

        int result = process.waitFor();
        if (result != 0) throw new IOException("x265 failed: " + result);

        if (IO.Y4M.isTempFile()) {
            y4m.delete();
        }

        ProcessBuilder builder = new ProcessBuilder("ffmpeg",
                "-f", getFFMpegFormat(), "-i", out.getPath(), "-c", "copy",
                "-y", // force overwrite
                mp4.getPath())
                        .redirectOutput(INHERIT)
                        .redirectError(INHERIT);

        builder.command().addAll(encoderParameters.ffmpegOptions);

        builder.start().waitFor();

        out.delete();
    }

    private void fillFrame(int offset, short[] pixels) {
        boolean extended = parameters.bitdepth > 8;
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

    public void render(CanvasYCbCr canvas) {
        int bps = y4mBytesPerSample();

        int offsetY = FRAME_HEADER.length;
        int offsetCb = offsetY + canvas.Y.pixels.length * bps;
        int offsetCr = offsetCb + canvas.Cb.pixels.length * bps;

        fillFrame(offsetY, canvas.Y.pixels);
        fillFrame(offsetCb, canvas.Cb.pixels);
        fillFrame(offsetCr, canvas.Cr.pixels);

        try {
            yuv4mpegOut.write(frameBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void render(int frames, Supplier<CanvasYCbCr> supplier) {
        for (int i = 0; i < frames; i++) {
            render(supplier.get());
        }
    }

    public void render(Duration duration, Supplier<CanvasYCbCr> supplier) {
        float rate = QUICK ? 1f : encoderParameters.framerate.rate;
        int frames = toFrames(rate, duration);
        render(QUICK ? min(QFRAMES, frames) : frames, supplier);
    }

    private int y4mBytesPerSample() {
        return parameters.bitdepth > 8 ? 2 : 1;
    }

    public CanvasYCbCr newCanvas() {
        return new CanvasYCbCr(encoderParameters.resolution,
                encoderParameters.parameters);
    }
}
