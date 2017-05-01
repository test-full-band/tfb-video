package band.full.testing.video.encoder;

import static java.util.Collections.addAll;

import java.io.IOException;
import java.util.List;

import band.full.testing.video.core.Framerate;
import band.full.testing.video.core.Resolution;

/**
 * @author Igor Malinin
 */
public class EncoderHEVC extends EncoderY4M {
    public EncoderHEVC(String name, Resolution resolution, Framerate fps,
            int bitdepth) throws IOException {
        super(name, resolution, fps, bitdepth);
    }

    @Override
    public boolean checkBitdepth(int depth) {
        return depth == 8 || depth == 10 || depth == 12;
    }

    @Override
    protected void addEncoderParams(List<String> command) {
        super.addEncoderParams(command);
        addAll(command, "--output-depth", Integer.toString(bitdepth),
                "--range=limited");
    }

    @Override
    public String getExecutable() {
        return "x265";
    }

    @Override
    public String getFFMpegFormat() {
        return "hevc";
    }
}

// "--level-idc=51",
// "--ref=5",
// "--limit-refs=0",
// "--sao",
// "--aq-mode=1",
// "--aq-strength=1.00",
// "--cutree",
// "--min-keyint=2",
// "--keyint=24",
// "--bframes=0",
// "--no-amp",
// "--no-tskip",
// "--limit-modes",
// "--chromaloc=2",
// "--repeat-headers",
// "--no-b-pyramid",
// "--rd=4",
// "--rskip",
// "--psy-rd=2.00",
// "--psy-rdoq=1.00",
// "--qpstep=4",
// "--bitrate=40000",
// "--vbv-maxrate=40000",
// "--vbv-bufsize=40000",
// "--vbv-init=0.9",
// "--ipratio=1.40",
// "--qg-size=32",
// "--no-rc-grain",
// "--tune", "grain",
// "--colorprim", "bt2020",
// "--transfer", "smpte-st-2084",
// "--colormatrix", "bt2020nc",
// "--master-display",
// "G(13250,34500)B(7500,3000)R(34000,16000)WP(15635,16450)L(10000000,5)",
// "--max-cll", "1000,400",
