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

    protected <T extends GeneratorBase<A>, A> T create(Generator<T, A> gen,
            NalUnitPostProcessor<?> processor) {
        return gen.create(factory, params, processor, muxer, folder, group);
    }

    protected <T extends GeneratorBase<A>, A> void generate(Generator<T, A> gen,
            NalUnitPostProcessor<?> processor) {
        create(gen, processor).generate();
    }

    protected <T extends GeneratorBase<A>, A> void generate(Generator<T, A> gen,
            NalUnitPostProcessor<?> processor, A args) {
        create(gen, processor).generate(args);
    }

    protected final GeneratorFactory factory;
    protected final EncoderParameters params;
    protected final MuxerFactory muxer;
    protected final String folder;
    protected final String group;

    protected final CheckerboardGenerator checkerboard;
    protected final LinesGenerator lines;
    protected final GammaGenerator gamma;
    protected final ColorRampsGenerator colorRamps;

    protected BasicSetupBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        this(factory, params, MuxerMP4Box::new, folder, group);
    }

    protected BasicSetupBase(GeneratorFactory factory,
            EncoderParameters params, MuxerFactory muxer,
            String folder, String group) {
        this.factory = factory;
        this.params = params;
        this.muxer = muxer;
        this.folder = folder + "/Basic";
        this.group = group;

        checkerboard = create(CheckerboardGenerator::new, processor());
        lines = create(LinesGenerator::new, processor());
        gamma = create(GammaGenerator::new, processor());
        colorRamps = create(ColorRampsGenerator::new, processorSDR());
    }

    protected NalUnitPostProcessor<?> processor() {
        return defaultNalUnitPostProcessor();
    }

    /**
     * For patterns that are by design limited with SDR
     * <p>
     * To be customized separately for static or dynamic HDR metadata
     */
    protected NalUnitPostProcessor<?> processorSDR() {
        return defaultNalUnitPostProcessor();
    }

    @Test
    @Disabled("TODO")
    public void blackLevel() {
        generate(BlackLevelGenerator::new, processor());
    }

    @Test
    public void blackPLUGE() {
        generate(BlackPLUGEGenerator::new, processorSDR());
    }

    @Test
    public void colorChecker24() {
        generate(ColorCheckerGenerator.Classic24::new, processorSDR());
    }

    @Test
    public void colorCheckerSG() {
        generate(ColorCheckerGenerator.DigitalSG::new, processorSDR());
    }

    @Test
    public void colorRamps() {
        colorRamps.generate();
    }

    @Test // TODO check into account dynamic metadata (DV)
    public void gamma() {
        gamma.generate();
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
