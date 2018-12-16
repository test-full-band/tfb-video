package band.full.test.video.generator;

import static band.full.test.video.generator.NalUnitPostProcessor.defaultNalUnitPostProcessor;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.MuxerMP4Box;

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
    @FunctionalInterface
    protected static interface Generator<T extends GeneratorBase<A>, A> {
        @SuppressWarnings("rawtypes")
        T create(GeneratorFactory factory, EncoderParameters params,
                NalUnitPostProcessor processor, MuxerFactory muxer,
                String folder, String group);
    }

    protected <T extends GeneratorBase<A>, A> T create(Generator<T, A> gen) {
        return gen.create(factory, params, processor, muxer, folder, group);
    }

    protected final GeneratorFactory factory;
    protected final EncoderParameters params;
    protected final NalUnitPostProcessor<?> processor;
    protected final MuxerFactory muxer;
    protected final String folder;
    protected final String group;

    protected final CheckerboardGenerator checkerboard;
    protected final LinesGenerator lines;
    protected final GammaGenerator gamma;

    protected BasicSetupBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        this(factory, params, defaultNalUnitPostProcessor(), MuxerMP4Box::new,
                folder, group);
    }

    protected BasicSetupBase(GeneratorFactory factory,
            EncoderParameters params, NalUnitPostProcessor<?> processor,
            MuxerFactory muxer, String folder, String group) {
        this.factory = factory;
        this.params = params;
        this.processor = processor;
        this.muxer = muxer;
        this.folder = folder + "/Basic";
        this.group = group;

        checkerboard = create(CheckerboardGenerator::new);
        lines = create(LinesGenerator::new);
        gamma = create(GammaGenerator::new);
    }

    @Test
    @Disabled("TODO")
    public void blackLevel() {
        create(BlackLevelGenerator::new).generate(null);
    }

    @Test
    public void blackPLUGE() {
        create(BlackPLUGEGenerator::new).generate(null);
    }

    @Disabled
    @Test // TODO check into account dynamic metadata (DV)
    public void gamma() {
        create(GammaGenerator::new).generate(null);
    }

    @ParameterizedTest
    @MethodSource("checkerboard")
    public void checkerboard(CheckerboardGenerator.Args args) {
        checkerboard.generate(args);
    }

    public Stream<CheckerboardGenerator.Args> checkerboard() {
        return checkerboard.args();
    }

    @ParameterizedTest
    @MethodSource("lines")
    public void lines(LinesGenerator.Args args) {
        lines.generate(args);
    }

    public Stream<LinesGenerator.Args> lines() {
        return lines.args();
    }
}
