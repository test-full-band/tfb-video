package band.full.testing.video.generator;

import band.full.testing.video.encoder.EncoderParameters;

import org.junit.jupiter.api.Test;

/**
 * Set of common basic setup and test patterns with HDR specific patterns.
 *
 * @author Igor Malinin
 */
public abstract class BasicSetupHDRBase extends BasicSetupBase {
    protected BasicSetupHDRBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, pattern);
    }

    @Test
    public void bt2111() {
        new BT2111Generator(factory, params, folder, pattern).generate();
    }
}
