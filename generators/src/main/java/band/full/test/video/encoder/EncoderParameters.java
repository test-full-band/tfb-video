package band.full.test.video.encoder;

import static band.full.core.Resolution.STD_1080p;
import static band.full.core.Resolution.STD_2160p;
import static band.full.core.Resolution.STD_720p;
import static band.full.video.buffer.Framerate.FPS_23_976;
import static band.full.video.itu.BT2020.BT2020_10bit;
import static band.full.video.itu.BT709.BT709_8bit;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import band.full.core.Resolution;
import band.full.video.buffer.Framerate;
import band.full.video.itu.BT2100;
import band.full.video.itu.ColorMatrix;

import java.util.List;
import java.util.Optional;

public class EncoderParameters {
    public static final String MASTER_DISPLAY_PRIMARIES =
            "G(13250,34500)B(7500,3000)R(34000,16000)WP(15635,16450)";

    public static final String MASTER_DISPLAY =
            MASTER_DISPLAY_PRIMARIES + "L(10000000,5)";

    public static final EncoderParameters HD_MAIN = new EncoderParameters(
            STD_720p, BT709_8bit, FPS_23_976);

    public static final EncoderParameters FULLHD_MAIN8 = new EncoderParameters(
            STD_1080p, BT709_8bit, FPS_23_976);

    public static final EncoderParameters UHD4K_MAIN8 = new EncoderParameters(
            STD_2160p, BT709_8bit, FPS_23_976);

    public static final EncoderParameters UHD4K_MAIN10 = new EncoderParameters(
            STD_2160p, BT2020_10bit, FPS_23_976);

    public static final EncoderParameters HLG10 = new EncoderParameters(
            STD_2160p, BT2100.HLG10, FPS_23_976);

    public static final EncoderParameters HLG10ITP = new EncoderParameters(
            STD_2160p, BT2100.HLG10ITP, FPS_23_976);

    public static final EncoderParameters HDR10 = new EncoderParameters(
            STD_2160p, BT2100.PQ10, FPS_23_976)
                    .withMasterDisplay(MASTER_DISPLAY);

    public static final EncoderParameters HDR10FR = new EncoderParameters(
            STD_2160p, BT2100.PQ10FR, FPS_23_976)
                    .withMasterDisplay(MASTER_DISPLAY);

    public static final EncoderParameters HDR10ITP = new EncoderParameters(
            STD_2160p, BT2100.PQ10ITP, FPS_23_976)
                    .withMasterDisplay(MASTER_DISPLAY);

    public final Resolution resolution;
    public final ColorMatrix matrix;
    public final Framerate framerate;
    public final Optional<String> masterDisplay; // HEVC HDR only
    public final List<String> encoderOptions;

    public EncoderParameters(Resolution resolution, ColorMatrix matrix,
            Framerate framerate) {
        this(resolution, matrix, framerate, empty(), emptyList());
    }

    private EncoderParameters(Resolution resolution, ColorMatrix matrix,
            Framerate framerate, Optional<String> masterDisplay,
            List<String> encoderOptions) {
        this.resolution = resolution;
        this.matrix = matrix;
        this.framerate = framerate;
        this.masterDisplay = masterDisplay;
        this.encoderOptions = encoderOptions;
    }

    public EncoderParameters withFramerate(Framerate framerate) {
        return new EncoderParameters(resolution, matrix,
                framerate, masterDisplay, encoderOptions);
    }

    public EncoderParameters withMasterDisplay(String masterDisplay) {
        return new EncoderParameters(resolution, matrix,
                framerate, ofNullable(masterDisplay), encoderOptions);
    }

    public EncoderParameters withEncoderOptions(String... options) {
        return withEncoderOptions(asList(options));
    }

    public EncoderParameters withEncoderOptions(List<String> options) {
        return new EncoderParameters(resolution, matrix,
                framerate, masterDisplay, options);
    }
}
