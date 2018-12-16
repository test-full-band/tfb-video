package band.full.test.video.generator;

import static band.full.test.video.generator.GeneratorFactory.LOSSLESS;
import static band.full.test.video.generator.NalUnitPostProcessor.defaultNalUnitPostProcessor;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static javafx.scene.text.Font.font;

import band.full.core.Resolution;
import band.full.core.Window;
import band.full.core.color.Primaries;
import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;
import band.full.test.video.encoder.MuxerMP4Box;
import band.full.video.buffer.Framerate;
import band.full.video.itu.ColorMatrix;
import band.full.video.itu.TransferCharacteristics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * @author Igor Malinin
 */
public abstract class GeneratorBase<A> {
    protected static final int PATTERN_SECONDS = 60;

    protected static final String INTRO = "intro";
    protected static final int INTRO_SECONDS = 5;

    protected static final String BODY = "body";
    protected static final int BODY_SECONDS = PATTERN_SECONDS - INTRO_SECONDS;

    protected static final char PG_SEPARATOR = LOSSLESS ? '=' : '~';

    public final GeneratorFactory factory;
    public final EncoderParameters params;
    public final String folder, group;
    private final Function<A, String> pattern;
    private final NalUnitPostProcessor<A> processor;
    private final MuxerFactory muxer;

    // direct access to commonly used parameters
    public final Resolution resolution;
    public final ColorMatrix matrix;
    public final Framerate framerate;
    public final Primaries primaries;
    public final TransferCharacteristics transfer;
    public final int bitdepth, width, height, gop;

    public GeneratorBase(GeneratorFactory factory, EncoderParameters params,
            String folder, String pattern, String group) {
        this(factory, params, defaultNalUnitPostProcessor(), MuxerMP4Box::new,
                folder, noargs -> pattern, group);
    }

    public GeneratorBase(GeneratorFactory factory, EncoderParameters params,
            String folder, Function<A, String> pattern, String group) {
        this(factory, params, defaultNalUnitPostProcessor(), MuxerMP4Box::new,
                folder, pattern, group);
    }

    public GeneratorBase(GeneratorFactory factory, EncoderParameters params,
            NalUnitPostProcessor<A> processor,
            String folder, String pattern, String group) {
        this(factory, params, processor, MuxerMP4Box::new,
                folder, noargs -> pattern, group);
    }

    public GeneratorBase(GeneratorFactory factory, EncoderParameters params,
            NalUnitPostProcessor<A> processor, MuxerFactory muxer,
            String folder, String pattern, String group) {
        this(factory, params, processor, muxer,
                folder, noargs -> pattern, group);
    }

    public GeneratorBase(GeneratorFactory factory, EncoderParameters params,
            NalUnitPostProcessor<A> processor, MuxerFactory muxer,
            String folder, Function<A, String> pattern, String group) {
        this.factory = factory;
        this.params = params;
        this.folder = folder;
        this.pattern = pattern;
        this.group = group;
        this.processor = processor;
        this.muxer = muxer;

        resolution = params.resolution;
        matrix = params.matrix;
        framerate = params.framerate;

        primaries = matrix.primaries;
        transfer = matrix.transfer;
        bitdepth = matrix.bitdepth;

        width = resolution.width;
        height = resolution.height;

        gop = round(framerate.rate);
    }

    public void generate(A args) {
        var pattern = getPattern(args);
        var dir = factory.greet(getFolder(args), pattern);
        try {
            String audio = audio();

            var inputs = encode(dir, args);
            var joined = new File(dir, pattern + factory.suffix);

            try (OutputStream out = new FileOutputStream(joined)) {
                int fragment = 0;
                for (String input : inputs) {
                    File file = new File(dir, input);
                    try (InputStream in = new FileInputStream(file)) {
                        processor.process(args, fragment++, in, out);
                    }
                }
            }

            if (!dir.isDirectory() && !dir.mkdirs())
                throw new IOException("Cannot create directory: " + dir);

            String mp4 = muxer.create(dir, pattern, factory.brand)
                    .mux(pattern + factory.suffix, audio);

            // joined.delete();
            inputs.stream().distinct()
                    .forEach(input -> new File(dir, input).delete());

            verify(dir, mp4, args);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected String audio() {
        return silence(PATTERN_SECONDS);
    }

    /**
     * In help to avoid messages about missing audio during play back generate
     * placeholder digital silence audio track.
     */
    protected String silence(int seconds) {
        String name = "target/silence" + seconds + "s.ac3";

        if (new File(name).exists()) return name;

        var builder = new ProcessBuilder(
                "ffmpeg", "-f", "s16le", "-ar", "48000", "-ac", "2",
                "-i", "pipe:0", name
        ).redirectOutput(INHERIT).redirectError(INHERIT);

        try {
            System.out.println(builder.command());

            var process = builder.start();

            try (var out = process.getOutputStream()) {
                var buf = new byte[8192]; // silence
                int length = 4 * 48000 * seconds;
                do {
                    out.write(buf, 0, min(length, buf.length));
                    length -= buf.length;
                } while (length > 0);
            }

            int result = process.waitFor();
            if (result != 0)
                throw new IOException("ffmpeg failed: " + result);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return name;
    }

    public List<String> encode(File dir, A args)
            throws IOException, InterruptedException {
        return encode(dir, args, BODY, PATTERN_SECONDS);
    }

    public final List<String> encode(File dir, A args, String phase, int repeat)
            throws IOException, InterruptedException {
        String pattern = getPattern(args);
        String name = phase == null ? pattern : pattern + "-" + phase;

        String out = factory.encode(dir, name, params,
                e -> encode(e, args, phase));

        return range(0, repeat).boxed().map(i -> out).collect(toList());
    }

    protected void verify(File dir, String mp4, A args) {
        verify(dir, mp4, 0, 2, args);
    }

    protected final void verify(File dir, String mp4, int ss, int to, A args) {
        String ssStr = ss == 0 ? null : Integer.toString(ss);
        String toStr = to == 0 ? null : Integer.toString(to);
        verify(dir, mp4, ssStr, toStr, args);
    }

    protected void verify(File dir, String mp4, String ss, String to, A args) {
        if (muxer != (MuxerFactory) MuxerMP4Box::new)
            return; // Skip verification of non-standard content (such as DV)

        DecoderY4M.decode(dir, mp4, params, ss, to, d -> verify(d, args));
    }

    protected String getFolder(A args) {
        return factory.folder + '/' + folder;
    }

    protected String getPattern(A args) {
        return pattern.apply(args) + PG_SEPARATOR + group;
    }

    protected Label text(Window window, Color color, String text) {
        return text(window.x, window.y, window.width, window.height,
                color, text);
    }

    protected Label text(Window window, Color color, Font font, String text) {
        return text(window.x, window.y, window.width, window.height,
                color, font, text);
    }

    protected Label text(int x, int y, int w, int h,
            Color color, String text) {
        return text(x, y, w, h, color, font(height / 54), text);
    }

    protected Label text(int x, int y, int w, int h,
            Color color, Font font, String text) {
        var l = new Label(text);
        l.setFont(font);
        l.setTextFill(color);
        l.setTextAlignment(TextAlignment.CENTER);
        l.setAlignment(Pos.CENTER);
        l.relocate(x, y);
        l.setPrefSize(w, h);

        return l;
    }

    protected abstract void encode(EncoderY4M e, A args, String phase);

    protected abstract void verify(DecoderY4M d, A args);
}
