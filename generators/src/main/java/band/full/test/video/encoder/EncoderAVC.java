package band.full.test.video.encoder;

import static band.full.video.itu.ColorRange.FULL;
import static java.lang.ProcessBuilder.Redirect.INHERIT;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public class EncoderAVC extends EncoderY4M {
    public static final String AVC_SUFFIX = ".h264";

    private EncoderAVC(File dir, String name, EncoderParameters parameters)
            throws IOException {
        super(dir, name, parameters);
    }

    @Override
    public boolean checkBitdepth(int depth) {
        return depth == 8;
    }

    @Override
    protected ProcessBuilder createProcessBuilder() {
        int rate = (int) (parameters.framerate.rate + 0.5f);
        int colorprim = matrix.primaries.code;
        int transfer = matrix.transfer.code();
        int colormatrix = matrix.code;

        var builder = new ProcessBuilder(
                getExecutable(), "--demuxer", "y4m",
                Y4M.isPipe() ? "-" : name + ".y4m", "-o", name + AVC_SUFFIX,
                "--keyint", Integer.toString(rate),
                "--range", matrix.range == FULL ? "pc" : "tv",
                "--colorprim", getColorPrimString(colorprim),
                "--transfer", getTransferString(transfer),
                "--colormatrix", getColorMatrixString(colormatrix),
                "--chromaloc", "2" // chroma_loc_info_present_flag
        ).directory(dir).redirectOutput(INHERIT).redirectErrorStream(true);
        // TODO: passing/detecting chromaloc; avoid hardcoding

        builder.command().addAll(parameters.encoderOptions);

        return builder;
    }

    public static String getColorPrimString(int code) {
        return switch (code) {
            case 1 -> "bt709";
            case 2 -> "undef";
            case 4 -> "bt470m";
            case 5 -> "bt470bg";
            case 6 -> "smpte170m";
            case 7 -> "smpte240m";
            case 8 -> "film";
            case 9 -> "bt2020";
            case 10 -> "smpte428";
            case 11 -> "smpte431";
            case 12 -> "smpte432";
            default -> throw new IllegalArgumentException(
                    "Unknown colour_primaries: " + code);
        };
    }

    public static String getTransferString(int code) {
        return switch (code) {
            case 1 -> "bt709";
            case 2 -> "undef";
            case 4 -> "bt470m";
            case 5 -> "bt470bg";
            case 6 -> "smpte170m";
            case 7 -> "smpte240m";
            case 8 -> "linear";
            case 9 -> "log100";
            case 10 -> "log316";
            case 11 -> "iec61966-2-4";
            case 12 -> "bt1361e";
            case 13 -> "iec61966-2-1";
            case 14 -> "bt2020-10";
            case 15 -> "bt2020-12";
            case 16 -> "smpte2084";
            case 17 -> "smpte428";
            case 18 -> "arib-std-b67";
            default -> throw new IllegalArgumentException(
                    "Unknown transfer_characteristic: " + code);
        };
    }

    public static String getColorMatrixString(int code) {
        return switch (code) {
            case 0 -> "GBR";
            case 1 -> "bt709";
            case 2 -> "undef";
            case 4 -> "fcc";
            case 5 -> "bt470bg";
            case 6 -> "smpte170m";
            case 7 -> "smpte240m";
            case 8 -> "YCgCo";
            case 9 -> "bt2020nc";
            case 10 -> "bt2020c";
            case 11 -> "smpte2085";
            case 12 -> "chroma-derived-nc";
            case 13 -> "chroma-derived-c";
            case 14 -> "ictcp";
            default -> throw new IllegalArgumentException(
                    "Unknown colour_matrix: " + code);
        };
    }

    @Override
    public String getExecutable() {
        return "x264";
    }

    public static String encode(File dir, String name,
            EncoderParameters parameters, Consumer<EncoderY4M> consumer)
            throws IOException, InterruptedException {
        try (var encoder = new EncoderAVC(dir, name, parameters)) {
            consumer.accept(encoder);
            return name + AVC_SUFFIX;
        }
    }
}
