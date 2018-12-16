package band.full.test.video.patterns.u4k.dv;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.DV_P5;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.dmContentRangeSDR100;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.dmDataPayload;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.muxer;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.processor;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.rpuDataMappingNominal;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.rpuHeader;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.CalibrateColorCheckerBase;
import band.full.test.video.generator.CalibratePatchesBase;
import band.full.video.dolby.RPU;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class CalibrateColorChecker2160pDVp5 extends CalibrateColorCheckerBase {
    private static final RpuFactory<CalibratePatchesBase.Args> RPU =
            (args, fragment, frame) -> new RPU(
                    rpuHeader(), rpuDataMappingNominal(),
                    dmDataPayload(dmContentRangeSDR100(args.window / 100.0)));

    public CalibrateColorChecker2160pDVp5() {
        super(HEVC, DV_P5, processor(RPU), muxer(), "UHD4K/DVp5", "U4K_DVp5");
    }
}
