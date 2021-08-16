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

    public static String encode(File dir, String name,
            EncoderParameters parameters, Consumer<EncoderY4M> consumer)
            throws IOException, InterruptedException {
        try (EncoderAVC encoder = new EncoderAVC(dir, name, parameters)) {
            consumer.accept(encoder);
            return name + AVC_SUFFIX;
        }
    }
}
