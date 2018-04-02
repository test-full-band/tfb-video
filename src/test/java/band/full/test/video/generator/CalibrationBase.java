package band.full.test.video.generator;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.test.video.generator.PatchesGenerator.Args;
import band.full.video.encoder.EncoderParameters;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Base class for creating single-color patches in the middle of the screen with
 * specified area percentage.
 *
 * @author Igor Malinin
 */
@TestInstance(PER_CLASS)
public class CalibrationBase {
    protected final GrayscalePatchesGenerator grayscale;
    protected final ColorPatchesGenerator color;

    public CalibrationBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        grayscale = new GrayscalePatchesGenerator(
                factory, params, folder, pattern);

        color = new ColorPatchesGenerator(
                factory, params, folder, pattern);
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("grayscale")
    public void grayscale(Args args) {
        grayscale.generate(args);
    }

    public Stream<Args> grayscale() {
        return IntStream.of(0, 5, 10, 20, 50).boxed()
                .flatMap(grayscale::grayscale);
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("colorchecker")
    public void colorchecker(Args args) {
        color.generate(args);
    }

    public Stream<Args> colorchecker() {
        return IntStream.of(5, 10, 20).boxed()
                .flatMap(color::colorchecker);
    }
}
