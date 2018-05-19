package band.full.test.video.experimental.dv10;

import static band.full.core.Resolution.STD_2160p;
import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.buffer.Framerate.FPS_23_976;
import static band.full.video.encoder.EncoderParameters.MASTER_DISPLAY;
import static band.full.video.itu.BT2020.CIE_B;
import static band.full.video.itu.BT2020.CIE_D65;
import static band.full.video.itu.BT2020.CIE_G;
import static band.full.video.itu.BT2020.CIE_R;
import static band.full.video.itu.ColorRange.FULL;

import band.full.core.color.Primaries;
import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.BasicSetupHDRBase;
import band.full.video.encoder.EncoderParameters;
import band.full.video.itu.ICtCp;
import band.full.video.smpte.ST2084;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pDV10 extends BasicSetupHDRBase {
    public static ST2084 PQ = new ST2084() {
        @Override
        public int code() {
            return 2;
        }
    };

    public static final Primaries PRIMARIES = new Primaries(2,
            CIE_R, CIE_G, CIE_B, CIE_D65);

    public static final EncoderParameters DV10 = new EncoderParameters(
            STD_2160p, new ICtCp(2, PQ, PRIMARIES, 10, FULL), FPS_23_976)
                    .withMasterDisplay(MASTER_DISPLAY);

    public BasicSetup2160pDV10() {
        super(HEVC, DV10, "UHD4K/DV10", "U4K_DV10");
    }
}
