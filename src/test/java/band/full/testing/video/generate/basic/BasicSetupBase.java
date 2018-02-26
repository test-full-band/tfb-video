package band.full.testing.video.generate.basic;

import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.generate.GeneratorFactory;

import org.junit.jupiter.api.Test;

/**
 * Set of common basic setup and test patterns.
 *
 * @author Igor Malinin
 */
public abstract class BasicSetupBase {
    public final GeneratorFactory factory;
    public final EncoderParameters params;
    public final String folder;

    protected BasicSetupBase(GeneratorFactory factory,
            EncoderParameters params, String folder) {
        this.factory = factory;
        this.params = params;
        this.folder = folder;
    }

    @Test
    public void blackLevel() {
        new BlackLevelGenerator(factory, params, folder, "BlackLevel")
                .generate();
    }

    @Test
    public void checkerboard() {
        new CheckerboardGenerator(factory, params, folder, "Checkerboard")
                .generate();
    }
}
