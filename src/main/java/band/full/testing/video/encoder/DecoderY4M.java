package band.full.testing.video.encoder;

import static band.full.testing.video.core.Framerate.toFrames;
import static band.full.testing.video.encoder.EncoderY4M.LOSSLESS;
import static band.full.testing.video.encoder.EncoderY4M.QFRAMES;
import static band.full.testing.video.encoder.EncoderY4M.QRATE;
import static band.full.testing.video.encoder.EncoderY4M.QUICK;
import static java.lang.Math.min;
import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import band.full.testing.video.core.CanvasYCbCr;
import band.full.testing.video.core.Resolution;
import band.full.testing.video.itu.YCbCr;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public class DecoderY4M implements AutoCloseable {
    private static final byte[] FRAME_HEADER = "FRAME\n".getBytes(US_ASCII);

    private static final Field FILTER_IN_FIELD;

    static {
        try {
            FILTER_IN_FIELD = FilterInputStream.class.getDeclaredField("in");
            FILTER_IN_FIELD.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Assuming input stream is buffering FilterInputStream return the unwrapped
     * instance.<br>
     * We don't need the buffering as we already have large buffers and very
     * efficient input. As raw video streams are extremely large we need every
     * bit of throughput.
     */
    static InputStream unwrap(InputStream in)
            throws IOException {
        if (in instanceof FilterInputStream) {
            try {
                return (FileInputStream) FILTER_IN_FIELD.get(in);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IOException(e);
            }
        }

        // Use the original one on platforms where we don't know how to unwrap
        return in;
    }

    public final String name;
    public final YCbCr parameters;
    public final EncoderParameters encoderParameters;

    public final File dir;
    public final File mp4;

    private final byte[] frameBuffer;
    private int framesRead;

    private Process process;
    private final InputStream yuv4mpegIn;

    protected DecoderY4M(String name, EncoderParameters encoderParameters)
            throws IOException {
        this.name = name;
        this.encoderParameters = encoderParameters;
        parameters = encoderParameters.parameters;

        String root = "target/testing-video" + (LOSSLESS ? "-lossless" : "");
        String prefix = root + "/" + name;

        mp4 = new File(prefix + ".mp4");

        dir = mp4.getParentFile();

        Resolution resolution = encoderParameters.resolution;

        int frameLength = resolution.width * resolution.height * 3
                / 2 * y4mBytesPerSample();
        frameBuffer = new byte[FRAME_HEADER.length + frameLength];

        yuv4mpegIn = open();

        Map<Character, String> headers = readY4Mheader();

        verify(headers, 'W', resolution.width);
        verify(headers, 'H', resolution.height);
        verify(headers, 'F',
                QUICK ? QRATE : encoderParameters.framerate.toString());
        verify(headers, 'I', "p");
        verify(headers, 'A', "1:1");
        verify(headers, 'C', y4mPixelFormat());
    }

    private void verify(Map<Character, String> headers, char c, String string) {
        String value = headers.get(c);

        if (!string.equals(value))
            throw new RuntimeException(
                    "Unexpected YUV4MPEG2 header: " + c + value);
    }

    private void verify(Map<Character, String> headers, char c, int number) {
        String value = headers.get(c);

        if (!Integer.toString(number).equals(value))
            throw new RuntimeException(
                    "Unexpected YUV4MPEG2 header: " + c + value);
    }

    private InputStream open() throws IOException {
        ProcessBuilder builder = new ProcessBuilder("ffmpeg",
                "-flags2", "+showall",
                "-i", mp4.getPath(),
                "-pix_fmt", ffmpegPixelFormat(),
                "-f", "yuv4mpegpipe",
                "-strict", "-1",
                "-").redirectError(INHERIT);

        System.out.println(builder.command());

        process = builder.start();

        return unwrap(process.getInputStream());
    }

    private Map<Character, String> readY4Mheader() throws IOException {
        byte[] YUV4MPEG2 = "YUV4MPEG2 ".getBytes(US_ASCII);

        for (int i = 0, length = YUV4MPEG2.length, n; i < length; i += n) {
            n = yuv4mpegIn.read(frameBuffer, i, length - i);
            if (n == -1)
                throw new EOFException("Expecting YUV4MPEG2 header");
        }

        for (int i = 0, length = YUV4MPEG2.length; i < length; i++) {
            if (frameBuffer[i] != YUV4MPEG2[i])
                throw new IOException("Expecting YUV4MPEG2 header");
        }

        StringBuffer buf = new StringBuffer();
        while (true) {
            int ch = yuv4mpegIn.read();
            if (ch < 0)
                throw new EOFException();
            if (ch > 127)
                throw new IOException("Unexpeced value YUV4MPEG2 header");

            if (ch == '\n') {
                break;
            }

            buf.append((char) ch);
        }

        return stream(buf.toString().split("\\s+"))
                .collect(toMap(p -> p.charAt(0), p -> p.substring(1)));
    }

    @Override
    public void close() throws IOException, InterruptedException {
        System.out.println("Frames read: " + framesRead);

        int framesSkipped = 0;
        CanvasYCbCr canvas = newCanvas();
        while (read(canvas)) {
            // skip frames to the end of file
            ++framesSkipped;
        }

        System.out.println("Frames skipped: " + framesSkipped);

        yuv4mpegIn.close();

        int result = process.waitFor();
        if (result != 0)
            throw new IOException("ffmpeg failed: " + result);
    }

    private void fillPlane(int offset, short[] pixels) {
        int y4mBytesPerSample = y4mBytesPerSample();
        byte[] buf = frameBuffer;

        for (int i = 0, j = offset, length = pixels.length; i < length; i++) {
            // LittleEndian
            int sample = buf[j++] & 0xFF;

            if (y4mBytesPerSample > 1) {
                sample += (buf[j++] & 0xFF) << 8;
            }

            pixels[i] = (short) sample;
        }
    }

    public boolean read(CanvasYCbCr canvas) {
        try {
            int n;
            try {
                n = yuv4mpegIn.read(frameBuffer);
                if (n == -1) return false;
            } catch (IOException e) {
                // Unfortunately Process API doesn't behave well and an
                // IOException is thrown instead of -1 returned by read()
                // with 'Stream closed' message in the underlying process
                // is finished already (happens all the time)!
                return false;
            }

            for (int i = n, length = frameBuffer.length; i < length; i += n) {
                n = yuv4mpegIn.read(frameBuffer, i, length - i);
                if (n == -1)
                    throw new EOFException("EOF in the middle of a FRAME");
            }

            for (int i = 0, length = FRAME_HEADER.length; i < length; i++) {
                if (frameBuffer[i] != FRAME_HEADER[i])
                    throw new IOException("Expecting FRAME header");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int bps = y4mBytesPerSample();

        int offsetY = FRAME_HEADER.length;
        int offsetCb = offsetY + canvas.Y.pixels.length * bps;
        int offsetCr = offsetCb + canvas.Cb.pixels.length * bps;

        fillPlane(offsetY, canvas.Y.pixels);
        fillPlane(offsetCb, canvas.Cb.pixels);
        fillPlane(offsetCr, canvas.Cr.pixels);

        ++framesRead;

        return true;
    }

    public void read(Consumer<CanvasYCbCr> consumer) {
        CanvasYCbCr canvas = newCanvas();

        while (read(canvas)) {
            consumer.accept(canvas);
        }
    }

    public void read(int frames, Consumer<CanvasYCbCr> consumer) {
        CanvasYCbCr canvas = newCanvas();

        for (int i = 0; i < frames; i++) {
            if (!read(canvas))
                throw new RuntimeException("Not enough frames");

            consumer.accept(canvas);
        }
    }

    public void read(Duration duration, Consumer<CanvasYCbCr> consumer) {
        float rate = QUICK ? 1f : encoderParameters.framerate.rate;
        int frames = toFrames(rate, duration);
        read(QUICK ? min(QFRAMES, frames) : frames, consumer);
    }

    private int y4mBytesPerSample() {
        return parameters.bitdepth > 8 ? 2 : 1;
    }

    private String y4mPixelFormat() {
        return parameters.bitdepth > 8 ? "420p" + parameters.bitdepth
                : "420mpeg2";
    }

    private String ffmpegPixelFormat() {
        return parameters.bitdepth > 8
                ? "yuv420p" + parameters.bitdepth + "le"
                : "yuv420p";
    }

    public CanvasYCbCr newCanvas() {
        return new CanvasYCbCr(encoderParameters.resolution,
                encoderParameters.parameters);
    }

    public static void decode(String name, EncoderParameters parameters,
            Consumer<DecoderY4M> consumer) {
        try (DecoderY4M decoder = new DecoderY4M(name, parameters)) {
            consumer.accept(decoder);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
