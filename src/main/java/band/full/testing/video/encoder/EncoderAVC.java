package band.full.testing.video.encoder;

import static band.full.testing.video.encoder.DecoderY4M.decode;
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
            int rate = (int) (parameters.framerate.rate + 0.5f);

            addAll(command, "--tune", "film", "--slices", "4",
                    "--bluray-compat", "--level", "4.1",
                    "--keyint", "" + rate, "--open-gop", "bluray",
                    "--vbv-maxrate", "40000", "--vbv-bufsize", "30000");

            if (!QUICK) {
                addAll(command, "--crf", "1");
            }
        }

        int colorprim = matrix.primaries.code;
        int transfer = parameters.transfer.code();
        int colormatrix = matrix.code;

        addAll(command, "--preset", getPresetParam(),
                "--range", matrix.range == FULL ? "pc" : "tv",
                "--colorprim", getColorPrimString(colorprim),
                "--transfer", getTransferString(transfer),
                "--colormatrix", getColorMatrixString(colormatrix));

        return builder;
    }

    public static String getColorPrimString(int code) {
        switch (code) {
            case 1:
                return "bt709";
            case 2:
                return "undef";
            case 4:
                return "bt470m";
            case 5:
                return "bt470bg";
            case 6:
                return "smpte170m";
            case 7:
                return "smpte240m";
            case 8:
                return "film";
            case 9:
                return "bt2020";
            case 10:
                return "smpte428";
            case 11:
                return "smpte431";
            case 12:
                return "smpte432";
            default:
                throw new IllegalArgumentException(
                        "Unknown colour_primaries: " + code);
        }
    }

    public static String getTransferString(int code) {
        switch (code) {
            case 1:
                return "bt709";
            case 2:
                return "undef";
            case 4:
                return "bt470m";
            case 5:
                return "bt470bg";
            case 6:
                return "smpte170m";
            case 7:
                return "smpte240m";
            case 8:
                return "linear";
            case 9:
                return "log100";
            case 10:
                return "log316";
            case 11:
                return "iec61966-2-4";
            case 12:
                return "bt1361e";
            case 13:
                return "iec61966-2-1";
            case 14:
                return "bt2020-10";
            case 15:
                return "bt2020-12";
            case 16:
                return "smpte2084";
            case 17:
                return "smpte428";
            case 18:
                return "arib-std-b67";
            default:
                throw new IllegalArgumentException(
                        "Unknown transfer_characteristic: " + code);
        }
    }

    public static String getColorMatrixString(int code) {
        switch (code) {
            case 0:
                return "GBR";
            case 1:
                return "bt709";
            case 2:
                return "undef";
            case 4:
                return "fcc";
            case 5:
                return "bt470bg";
            case 6:
                return "smpte170m";
            case 7:
                return "smpte240m";
            case 8:
                return "YCgCo";
            case 9:
                return "bt2020nc";
            case 10:
                return "bt2020c";
            case 11:
                return "smpte2085";
            case 12:
                return "chroma-derived-nc";
            case 13:
                return "chroma-derived-c";
            case 14:
                return "ictcp";
            default:
                throw new IllegalArgumentException(
                        "Unknown colour_matrix: " + code);
        }
    }

    @Override
    public String getExecutable() {
        return "x264";
    }

    @Override
    public String getFormat() {
        return "h264";
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
