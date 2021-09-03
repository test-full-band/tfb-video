package band.full.test.video.patterns.u4k.dv;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.DV_P5;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.dmContentRange10K;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.dmContentRangeSDR;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.dmDataPayload;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.muxer;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.processor;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.rpuDataMappingNominal;
import static band.full.test.video.patterns.u4k.dv.DolbyVisionProfile5.rpuHeader;
import static band.full.video.dolby.IPTPQc2.PQ10IPTc2;
import static java.util.function.Function.identity;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants2DBase10HDR;
import band.full.video.dolby.RPU;

import java.util.stream.Stream;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2D2160pDVp5 extends Quants2DBase10HDR {
    private static final RPU RPU = new RPU(
            rpuHeader(), rpuDataMappingNominal(),
            dmDataPayload(dmContentRange10K()));

    private static final RPU RPU_SDR = new RPU(
            rpuHeader(), rpuDataMappingNominal(),
            dmDataPayload(dmContentRangeSDR()));

    private static RPU rpu(Args args, int fragment, int frame) {
        double sdrCode = PQ10IPTc2.toLumaCode(
                PQ10IPTc2.transfer.fromLinear(0.01));

        return (args.yMin() + ROWS < sdrCode) ? RPU_SDR : RPU;
    }

    public Quants2D2160pDVp5() {
        super(HEVC, DV_P5, processor(Quants2D2160pDVp5::rpu), muxer(),
                "UHD4K/DVp5", "U4K_DVp5");
    }

    @Override
    protected Stream<Args> args() {
        return Stream.of(
                quants("NearBlack", 0, 32),
                quants("DarkGray", 64, 128),
                quants("Gray", 256),
                quants("LightGray", 384),
                quants("NearWhite", 512),
                quants("Bright", 640),
                quants("Brighter", 768),
                quants("Brightest", 896)
        ).flatMap(identity());
    }
}
