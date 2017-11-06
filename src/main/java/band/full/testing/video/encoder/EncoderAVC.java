package band.full.testing.video.encoder;

import static band.full.testing.video.encoder.DecoderY4M.decode;
import static band.full.testing.video.encoder.EncoderParameters.FULLHD_MAIN8;
import static band.full.testing.video.itu.ColorRange.FULL;
import static java.lang.ProcessBuilder.Redirect.INHERIT;
import static java.util.Collections.addAll;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public class EncoderAVC extends EncoderY4M {
    private EncoderAVC(String name, EncoderParameters parameters)
            throws IOException {
        super(name, parameters);
    }

    @Override
    public boolean checkBitdepth(int depth) {
        return depth == 8;
    }

    @Override
    protected ProcessBuilder createProcessBuilder() {
        ProcessBuilder builder = new ProcessBuilder(getExecutable(),
                "--demuxer", "y4m",
                IO.Y4M.isPipe() ? "-" : y4m.getPath(),
                "-o", out.getPath())
                        .redirectOutput(INHERIT)
                        .redirectError(INHERIT);

        List<String> command = builder.command();

        if (LOSSLESS) {
            addAll(command, "--qp", "0");
        } else {
            int rate = (int) (encoderParameters.framerate.rate + 0.5f);

            addAll(command, "--tune", "film", "--slices", "4",
                    "--bluray-compat", "--level", "4.1",
                    "--keyint", "" + rate, "--open-gop", "bluray",
                    "--vbv-maxrate", "40000", "--vbv-bufsize", "30000");

            if (!QUICK) {
                // try lossless if fits into bitrate? see x265 impl...
                addAll(command, "--crf", "1");
            }
        }

        addAll(command, "--preset", getProfileParam(),
                parameters.range == FULL ? "--range=pc" : "--range=tv",
                "--colorprim", "bt709",
                "--transfer", "bt709",
                "--colormatrix", "bt709");

        return builder;
    }

    @Override
    public String getExecutable() {
        return "x264";
    }

    @Override
    public String getFormat() {
        return "h264";
    }

    public static void encode(String name, Consumer<EncoderY4M> consumer) {
        encode(name, FULLHD_MAIN8, consumer);
    }

    public static void encode(String name,
            Consumer<EncoderY4M> ec, Consumer<DecoderY4M> dc) {
        encode(name, FULLHD_MAIN8, ec, dc);
    }

    public static void encode(String name, EncoderParameters parameters,
            Consumer<EncoderY4M> consumer) {
        try (EncoderAVC encoder = new EncoderAVC(name, parameters)) {
            consumer.accept(encoder);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void encode(String name, EncoderParameters parameters,
            Consumer<EncoderY4M> ec, Consumer<DecoderY4M> dc) {
        encode(name, parameters, ec);
        decode(name, parameters, dc);
    }
}
