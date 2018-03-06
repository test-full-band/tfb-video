package band.full.testing.video.generate.base;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.generate.GeneratorFactory;

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
    public final GeneratorFactory factory;
    public final EncoderParameters params;
    public final String folder;

    CheckerboardGenerator checkerboard;

    protected BasicSetupBase(GeneratorFactory factory,
            EncoderParameters params, String folder) {
        this.factory = factory;
        this.params = params;
        this.folder = folder;

        checkerboard = new CheckerboardGenerator(factory, params, folder);
    }

    @Test
    public void blackLevel() {
        new BlackLevelGenerator(factory, params, folder).generate();
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
