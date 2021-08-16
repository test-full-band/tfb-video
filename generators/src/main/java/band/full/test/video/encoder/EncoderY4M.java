package band.full.test.video.encoder;

import static band.full.video.buffer.Framerate.toFrames;
import static java.lang.Boolean.getBoolean;
import static java.lang.Math.min;
import static java.lang.System.arraycopy;
import static java.lang.System.getProperty;
import static java.nio.charset.StandardCharsets.US_ASCII;

import band.full.video.buffer.FrameBuffer;
import band.full.video.itu.ColorMatrix;

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

    private static final byte[] FRAME_HEADER = "FRAME\n".getBytes(US_ASCII);

    /**
     * Quick mode limits duration of render calls to 15 frames and frame rate to
     * one frame per second. This is useful for quick testing the build
     * procedures. Produced videos will not be fully usable!
     */
    // TODO: remove from here to test/java
    public static final boolean QUICK = getBoolean("encoder.quick");

    private int qframes;

    public final String name;
    public final EncoderParameters parameters;

    public final ColorMatrix matrix;

    public final File dir;
    private final File y4m;

    private final byte[] frameBuffer;

    private Process process;
    private final OutputStream yuv4mpegOut;

    protected EncoderY4M(File dir, String name, EncoderParameters parameters)
            throws IOException {
        this.dir = dir;
        this.name = name;
        this.parameters = parameters;

        y4m = new File(dir, name + ".y4m");

        matrix = parameters.matrix;

        if (!dir.isDirectory() && !dir.mkdirs())
            throw new IOException("Cannot create directory: " + dir);

        var resolution = parameters.resolution;

        int frameLength = resolution.width() * resolution.height() * 3
                / 2 * y4mBytesPerSample();
        frameBuffer = new byte[FRAME_HEADER.length + frameLength];
        arraycopy(FRAME_HEADER, 0, frameBuffer, 0, FRAME_HEADER.length);

        yuv4mpegOut = open();

        String header = "YUV4MPEG2"
                + " W" + resolution.width() + " H" + resolution.height()
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

        var builder = createProcessBuilder();

        System.out.println();
        System.out.println("> " + dir);
        System.out.println(builder.command());

        process = builder.start();

        return process.getOutputStream();
    }

    // TODO lost a call
    protected abstract boolean checkBitdepth(int depth);

    public abstract String getExecutable();

    protected abstract ProcessBuilder createProcessBuilder();

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
