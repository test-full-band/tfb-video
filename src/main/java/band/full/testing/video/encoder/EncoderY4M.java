package band.full.testing.video.encoder;

import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.lang.System.arraycopy;
import static java.lang.System.getProperty;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Collections.addAll;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.core.Framerate;
import band.full.testing.video.core.Resolution;

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
    public final Resolution resolution;
    public final Framerate fps;
    public final int bitdepth;

    public final File dir;
    public final File y4m;
    public final File out;
    public final File mp4;

    private final byte[] frameBuffer;

    private Process process;
    private OutputStream yuv4mpegOut;

    public EncoderY4M(String name, Resolution resolution, Framerate fps,
            int bitdepth) throws IOException {
        this.name = name;
        this.resolution = resolution;
        this.fps = fps;
        this.bitdepth = bitdepth;

        String prefix = "target/video/" + name;

        y4m = new File(prefix + ".y4m");
        out = new File(prefix + ".out");
        mp4 = new File(prefix + ".mp4");

        dir = mp4.getParentFile();

        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) { throw new IOException(
                    "Cannot create directory: " + dir); }
        }

        int frameLength = resolution.width * resolution.height * 3
                / 2 * y4mBytesPerSample();
        frameBuffer = new byte[FRAME_HEADER.length + frameLength];
        arraycopy(FRAME_HEADER, 0, frameBuffer, 0, FRAME_HEADER.length);

        yuv4mpegOut = open();

        String header = "YUV4MPEG2"
                + " W" + resolution.width + " H" + resolution.height
                + " F" + fps + " Ip A1:1 C420p" + bitdepth + "\n";

        yuv4mpegOut.write(header.getBytes(US_ASCII));
    }

    private int y4mBytesPerSample() {
        return bitdepth > 8 ? 2 : 1;
    }

    private OutputStream open() throws IOException {
        if (!IO.Y4M.isPipe()) { return new FileOutputStream(y4m); }

        ProcessBuilder builder = new ProcessBuilder(getExecutable(),
                "-", out.getPath())
                        .redirectOutput(INHERIT)
                        .redirectError(INHERIT);

        addEncoderParams(builder.command());

        process = builder.start();

        return unwrap(process.getOutputStream());
    }

    protected abstract boolean checkBitdepth(int depth);

    public abstract String getExecutable();

    public abstract String getFFMpegFormat();

    protected void addEncoderParams(List<String> command) {
        addAll(command, "--y4m", "--preset=veryslow");
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
            process = builder.start();
            process.waitFor();
        }

        int result = process.waitFor();
        if (result != 0) { throw new IOException("x265 failed: " + result); }

        if (IO.Y4M.isTempFile()) {
            y4m.delete();
        }

        new ProcessBuilder("ffmpeg", "-f", getFFMpegFormat(),
                "-i", out.getPath(), "-c", "copy", "-y", // force overwrite
                mp4.getPath())
                        .redirectOutput(INHERIT)
                        .redirectError(INHERIT)
                        .start().waitFor();

        out.delete();
    }

    private void fillFrame(int offset, short[] pixels) throws IOException {
        byte[] buf = frameBuffer;

        for (int i = 0, j = offset; i < pixels.length; i++) {
            int sample = pixels[i];

            // LittleEndian
            buf[j++] = (byte) sample;
            if (bitdepth > 8) {
                buf[j++] = (byte) (sample >>> 8);
            }
        }
    }

    public void render(CanvasYCbCr canvas) throws IOException {
        int offsetY = FRAME_HEADER.length;
        int offsetCb = offsetY + canvas.Y.pixels.length * y4mBytesPerSample();
        int offsetCr = offsetCb + canvas.Cb.pixels.length * y4mBytesPerSample();

        fillFrame(offsetY, canvas.Y.pixels);
        fillFrame(offsetCb, canvas.Cb.pixels);
        fillFrame(offsetCr, canvas.Cr.pixels);

        yuv4mpegOut.write(frameBuffer);
    }

    public void render(int frames, Supplier<CanvasYCbCr> supplier)
            throws IOException {
        for (int i = 0; i < frames; i++) {
            render(supplier.get());
        }
    }

    public void render(Duration duration, Supplier<CanvasYCbCr> supplier)
            throws IOException {
        render(fps.toFrames(duration), supplier);
    }
}
