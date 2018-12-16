package band.full.test.video.generator;

import band.full.test.video.encoder.EncoderParameters;

import org.junit.jupiter.api.Test;

/**
 * Set of common basic setup and test patterns with HDR specific patterns.
 *
 * @author Igor Malinin
 */
public abstract class BasicSetupHDRBase extends BasicSetupBase {
    protected BasicSetupHDRBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder, group);
    }

    protected BasicSetupHDRBase(GeneratorFactory factory,
            EncoderParameters params, NalUnitPostProcessor<?> processor,
            MuxerFactory muxer, String folder, String group) {
        super(factory, params, processor, muxer, folder, group);
    }

    @Test
    public void bt2111() {
        create(BT2111Generator::new).generate(null);
    }
}
