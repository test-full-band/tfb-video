package band.full.testing.video.encoder;

import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.itu.BT2020.BT2020_10bit;
import static java.util.Collections.addAll;

import band.full.testing.video.core.Framerate;
import band.full.testing.video.core.Resolution;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public class EncoderHDR10 extends EncoderHEVC {
    private EncoderHDR10(String name, Resolution resolution, Framerate fps)
            throws IOException {
        super(name, resolution, fps, BT2020_10bit);
    }

    @Override
    public boolean checkBitdepth(int depth) {
        return depth == 8 || depth == 10 || depth == 12;
    }

    @Override
    protected void addEncoderParams(List<String> command) {
        super.addEncoderParams(command);

        addAll(command, "--profile=main10",
                "--colorprim=bt2020", "--colormatrix=bt2020nc",
                "--chromaloc=2", "--transfer=smpte-st-2084",
                "--max-cll", "0,0", // request no tone-mapping
                "--master-display", "G(13250,34500)B(7500,3000)R(34000,16000)"
                        + "WP(15635,16450)L(10000000,5)");
    }

    public static void encode(String name, Consumer<EncoderY4M> consumer) {
        encode(name, HDR10, consumer);
    }

    public static void encode(String name, EncoderParameters parameters,
            Consumer<EncoderY4M> consumer) {
        try (EncoderHEVC encoder = new EncoderHDR10(name,
                parameters.resolution, parameters.framerate)) {
            consumer.accept(encoder);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

// "--level-idc=51",
// "--ref=5",
// "--limit-refs=0",
// "--min-keyint=2",
// "--keyint=24",
// "--bframes=0",
// "--limit-modes",
// "--no-b-pyramid",
// "--rd=4",
// "--rskip",
// "--psy-rd=2.00",
// "--psy-rdoq=1.00",
// "--bitrate=40000",
// "--vbv-maxrate=40000",
// "--vbv-bufsize=40000",
// "--vbv-init=0.9",
// "--aq-mode=1",
// "--aq-strength=1.00",
// "--cutree",
// "--qg-size=32",
// "--no-rc-grain",
// "--tune", "grain",
