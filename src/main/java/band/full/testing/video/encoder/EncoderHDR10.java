package band.full.testing.video.encoder;

import static java.util.Collections.addAll;

import java.io.IOException;
import java.util.List;

import band.full.testing.video.core.Framerate;
import band.full.testing.video.core.Resolution;

/**
 * @author Igor Malinin
 */
public class EncoderHDR10 extends EncoderHEVC {
    public EncoderHDR10(String name, Resolution resolution, Framerate fps)
            throws IOException {
        super(name, resolution, fps, 10);
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
}

// "--level-idc=51",
// "--ref=5",
// "--limit-refs=0",
// "--min-keyint=2",
// "--keyint=24",
// "--bframes=0",
// "--limit-modes",
// "--repeat-headers",
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
