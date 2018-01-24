package band.full.testing.video.generate.basic;

import band.full.testing.video.generate.GeneratorBase;

import org.junit.jupiter.api.Test;

/**
 * Set of common basic setup and test patterns.
 *
 * @author Igor Malinin
 */
public abstract class BasicSetupBase {
    @Test
    public void blackLevel() {
        generate(new BlackLevelGenerator(), "BlackLevel");
    }

    @Test
    public void checkerboard() {
        generate(new CheckerboardGenerator(), "Checkerboard");
    }

    public abstract void generate(GeneratorBase generator, String fileName);
}
