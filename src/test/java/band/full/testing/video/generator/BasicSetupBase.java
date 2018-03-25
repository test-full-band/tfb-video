package band.full.testing.video.generator;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.testing.video.encoder.EncoderParameters;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * Set of common basic setup and test patterns.
 *
 * @author Igor Malinin
 */
@TestInstance(PER_CLASS)
public abstract class BasicSetupBase {
    protected final GeneratorFactory factory;
    protected final EncoderParameters params;
    protected final String folder;
    protected final String pattern;

    protected final CheckerboardGenerator checkerboard;

    protected BasicSetupBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        this.factory = factory;
        this.params = params;
        this.folder = folder;
        this.pattern = pattern;

        checkerboard = new CheckerboardGenerator(
                factory, params, folder, pattern);
    }

    @Test
    @Disabled("TODO")
    public void blackLevel() {
        new BlackLevelGenerator(factory, params, folder, pattern).generate();
    }

    @ParameterizedTest
    @MethodSource("checkerboard")
    public void checkerboard(CheckerboardGenerator.Args args) {
        checkerboard.generate(args);
    }

    public Stream<CheckerboardGenerator.Args> checkerboard() {
        return checkerboard.args();
    }
}
