package band.full.test.video.patterns.u4k.dv;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.DV_P5;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.dmContentRange10K;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.dmDataPayload;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.muxer;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.processor;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.rpuDataMappingNominal;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.rpuHeader;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.BasicSetupHDRBase;
import band.full.video.dolby.RPU;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pDVp5 extends BasicSetupHDRBase {
    private static final RPU RPU = new RPU(
            rpuHeader(), rpuDataMappingNominal(),
            dmDataPayload(dmContentRange10K()));

    public BasicSetup2160pDVp5() {
        super(HEVC, DV_P5, processor(RPU), muxer(), "UHD4K/DVp5", "U4K_DVp5");
    }
}
