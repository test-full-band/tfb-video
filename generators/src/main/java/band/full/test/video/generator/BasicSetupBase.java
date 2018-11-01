package band.full.test.video.generator;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.test.video.encoder.EncoderParameters;

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
    protected final String group;

    protected final CheckerboardGenerator checkerboard;

    protected BasicSetupBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        this.factory = factory;
        this.params = params;
        this.folder = folder + "/Basic";
        this.group = group;

        checkerboard = new CheckerboardGenerator(
                factory, params, this.folder, group);
    }

    @Test
    @Disabled("TODO")
    public void blackLevel() {
        new BlackLevelGenerator(factory, params, folder, group)
                .generate(null);
    }

    @Test
    public void blackPLUGE() {
        new BlackPLUGEGenerator(factory, params, folder, group)
                .generate(null);
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
