package band.full.video.encoder;

import static band.full.video.buffer.Framerate.toFrames;
import static band.full.video.encoder.EncoderY4M.LOSSLESS;
import static band.full.video.encoder.EncoderY4M.QUICK;
import static java.lang.Math.min;
import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Arrays.stream;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toMap;

import band.full.video.buffer.FrameBuffer;
import band.full.video.itu.ColorMatrix;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public class DecoderY4M implements AutoCloseable {
    private static final byte[] FRAME_HEADER = "FRAME\n".getBytes(US_ASCII);

    private int qframes;

    public final String name;
    public final EncoderParameters parameters;

    public final ColorMatrix matrix;

    public final File dir;
    public final File mp4;

    private final byte[] frameBuffer;
    private int framesRead;

    private Process process;
    private final InputStream yuv4mpegIn;

    protected DecoderY4M(String name, EncoderParameters parameters)
            throws IOException {
        this.name = name;
        this.parameters = parameters;

        matrix = parameters.matrix;

        String root = "target/video-"
                + (LOSSLESS ? "lossless" : "main");

        String prefix = root + "/" + name;

        mp4 = new File(prefix + ".mp4");

        dir = mp4.getParentFile();

        var resolution = parameters.resolution;

        int frameLength = resolution.width * resolution.height * 3
                / 2 * y4mBytesPerSample();
        frameBuffer = new byte[FRAME_HEADER.length + frameLength];

        yuv4mpegIn = open();

        var headers = readY4Mheader();

        verify(headers, 'W', resolution.width);
        verify(headers, 'H', resolution.height);
        verify(headers, 'F', QUICK ? "1:1" : parameters.framerate.toString());
        verify(headers, 'I', "p");
        verify(headers, 'A', "1:1");
        verify(headers, 'C', y4mPixelFormats());
    }

    private void verify(Map<Character, String> headers, char c, String string) {
        String value = headers.get(c);

        if (!string.equals(value))
            throw new RuntimeException(
                    "Unexpected YUV4MPEG2 header: " + c + value);
    }

    private void verify(Map<Character, String> headers, char c,
            Set<String> strings) {
        String value = headers.get(c);

        if (!strings.contains(value))
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
        var builder = new ProcessBuilder("ffmpeg",
                "-flags2", "+showall",
                "-i", mp4.getPath(),
                "-pix_fmt", ffmpegPixelFormat(),
                "-f", "yuv4mpegpipe",
                "-strict", "-1",
                "-").redirectError(INHERIT);

        System.out.println(builder.command());

        process = builder.start();

        return process.getInputStream();
    }

    private Map<Character, String> readY4Mheader() throws IOException {
        byte[] YUV4MPEG2 = "YUV4MPEG2 ".getBytes(US_ASCII);
        int length = YUV4MPEG2.length;

        int n = yuv4mpegIn.readNBytes(frameBuffer, 0, length);
        if (n == 0)
            throw new EOFException("Expecting YUV4MPEG2 header");

        if (!Arrays.equals(frameBuffer, 0, length, YUV4MPEG2, 0, length))
            throw new IOException("Expecting YUV4MPEG2 header");

        var buf = new StringBuffer();
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
        FrameBuffer fb = newFrameBuffer();
        while (read(fb)) {
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
        var buf = frameBuffer;

        for (int i = 0, j = offset, length = pixels.length; i < length; i++) {
            // LittleEndian
            int sample = buf[j++] & 0xFF;

            if (y4mBytesPerSample > 1) {
                sample += (buf[j++] & 0xFF) << 8;
            }

            pixels[i] = (short) sample;
        }
    }

    public boolean read(FrameBuffer fb) {
        int n = readFrameBuffer();

        if (n == 0) return false; // EOF

        if (n < frameBuffer.length)
            throw new RuntimeException("EOF in the middle of a FRAME");

        int offsetY = FRAME_HEADER.length;

        if (!Arrays.equals(frameBuffer, 0, offsetY, FRAME_HEADER, 0, offsetY))
            throw new RuntimeException("Expecting FRAME header");

        int bps = y4mBytesPerSample();
        int offsetCb = offsetY + fb.Y.pixels.length * bps;
        int offsetCr = offsetCb + fb.U.pixels.length * bps;

        fillPlane(offsetY, fb.Y.pixels);
        fillPlane(offsetCb, fb.U.pixels);
        fillPlane(offsetCr, fb.V.pixels);

        ++framesRead;

        return true;
    }

    private int readFrameBuffer() {
        int pos = 0;

        try {
            for (int end = frameBuffer.length; pos < end;) {
                // performance optimization; otherwise extremely slow
                int block = min(end - pos, 16384);

                int r = yuv4mpegIn.read(frameBuffer, pos, block);
                if (r < 0) {
                    break; // 'Normal' EOF
                }

                pos += r;
            }
        } catch (IOException e) {
            // Unfortunately Process API doesn't behave well and an
            // IOException is thrown instead of -1 returned by read()
            // with 'Stream closed' message in the underlying process
            // is finished already (happens all the time)!
        }

        return pos;
    }

    public void read(Consumer<FrameBuffer> consumer) {
        var fb = newFrameBuffer();

        while (read(fb)) {
            consumer.accept(fb);
        }
    }

    public void read(int frames, Consumer<FrameBuffer> consumer) {
        var fb = newFrameBuffer();

        if (QUICK && parameters.framerate.rate > 1.0) {
            int rate = (int) parameters.framerate.rate;

            if (qframes > rate * 60) return;
            frames = min(frames, rate * 10);

            for (int i = qframes % rate; i < frames; i += rate) {
                if (!read(fb))
                    throw new RuntimeException("Not enough frames");

                consumer.accept(fb);
            }

            qframes += frames;
            return;
        }

        for (int i = 0; i < frames; i++) {
            if (!read(fb))
                throw new RuntimeException("Not enough frames");

            consumer.accept(fb);
        }
    }

    public void read(Duration duration, Consumer<FrameBuffer> consumer) {
        read(toFrames(parameters.framerate.rate, duration), consumer);
    }

    private int y4mBytesPerSample() {
        return matrix.bitdepth > 8 ? 2 : 1;
    }

    private Set<String> y4mPixelFormats() {
        return matrix.bitdepth > 8
                ? singleton("420p" + matrix.bitdepth)
                : Set.of("420mpeg2", "420jpeg", "420paldv");
        // For 8 bit output the choice is illogical, so accept any
        // Works this way:
        // * chromaloc=0 => mpeg2, also the 'default' if unset
        // * chromaloc=1 => paldv
        // * chromaloc=2..5 => jpeg, i.e. otherwise
        // In reality chromaloc=2 is co-sited with luma
        // and in JPEG chroma is not co-sited with luma
    }

    private String ffmpegPixelFormat() {
        return matrix.bitdepth > 8
                ? "yuv420p" + matrix.bitdepth + "le"
                : "yuv420p";
    }

    public FrameBuffer newFrameBuffer() {
        return new FrameBuffer(parameters.resolution, matrix);
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
