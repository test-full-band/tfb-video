package band.full.testing.video.encoder;

import static band.full.testing.video.core.Framerate.FPS_23_976;
import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.encoder.Preset.SLOW;
import static band.full.testing.video.itu.BT2020.BT2020_10bit;
import static band.full.testing.video.itu.BT709.BT709;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import band.full.testing.video.core.Framerate;
import band.full.testing.video.core.Resolution;
import band.full.testing.video.itu.YCbCr;

import java.util.List;

public class EncoderParameters {
    public static final EncoderParameters HDR10 =
            new EncoderParameters(STD_2160p, BT2020_10bit, FPS_23_976);

    public static final EncoderParameters MAIN8 =
            new EncoderParameters(STD_2160p, BT709, FPS_23_976);

    public final Resolution resolution;
    public final YCbCr parameters;
    public final Framerate framerate;
    public final Preset preset;
    public final List<String> encoderOptions;
    public final List<String> ffmpegOptions;

    public EncoderParameters(Resolution resolution, YCbCr parameters,
            Framerate framerate) {
        this(resolution, parameters, framerate, SLOW);
    }

    public EncoderParameters(Resolution resolution, YCbCr parameters,
            Framerate framerate, Preset preset) {
        this(resolution, parameters, framerate, preset,
                emptyList(), emptyList());
    }

    public EncoderParameters(Resolution resolution, YCbCr parameters,
            Framerate framerate, Preset preset,
            List<String> encoderOptions, List<String> ffmpegOptions) {
        this.resolution = resolution;
        this.parameters = parameters;
        this.framerate = framerate;
        this.preset = preset;
        this.encoderOptions = encoderOptions;
        this.ffmpegOptions = ffmpegOptions;
    }

    public EncoderParameters withFramerate(Framerate framerate) {
        return new EncoderParameters(resolution, parameters,
                framerate, preset, encoderOptions, ffmpegOptions);
    }

    public EncoderParameters withPreset(Preset preset) {
        return new EncoderParameters(resolution, parameters,
                framerate, preset, encoderOptions, ffmpegOptions);
    }

    public EncoderParameters withEncoderOptions(String... options) {
        return withEncoderOptions(asList(options));
    }

    public EncoderParameters withEncoderOptions(List<String> options) {
        return new EncoderParameters(resolution, parameters,
                framerate, preset, options, ffmpegOptions);
    }

    public EncoderParameters withFfmpegOptions(String... ptions) {
        return withFfmpegOptions(asList(ptions));
    }

    public EncoderParameters withFfmpegOptions(List<String> options) {
        return new EncoderParameters(resolution, parameters,
                framerate, preset, encoderOptions, options);
    }
}
