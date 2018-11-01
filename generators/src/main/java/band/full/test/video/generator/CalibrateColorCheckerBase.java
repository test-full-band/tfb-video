package band.full.test.video.generator;

import static band.full.core.ArrayMath.multiply;
import static band.full.core.Quantizer.round;
import static band.full.core.color.CIEXYZ.ILLUMINANT_D50;
import static band.full.core.color.ChromaticAdaptation.bradford;
import static band.full.test.video.generator.ColorChecker.CLASSIC_24;
import static band.full.test.video.generator.ColorChecker.CLASSIC_24_NAMES;
import static java.lang.Character.toUpperCase;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.core.color.Matrix3x3;
import band.full.test.video.encoder.EncoderParameters;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

/**
 * Base class for creating single-color patches in the middle of the screen with
 * specified area percentage.
 *
 * @author Igor Malinin
 */
@TestInstance(PER_CLASS)
public abstract class CalibrateColorCheckerBase
        extends CalibrateColorPatchesBase {
    private final Matrix3x3 ADAPTATION;

    public CalibrateColorCheckerBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder + "/Calibrate", group);

        ADAPTATION = bradford(ILLUMINANT_D50, primaries.white.CIEXYZ());
    }

    @Override
    @ParameterizedTest(name = "{arguments}")
    @MethodSource("args")
    public void generate(Args args) {
        super.generate(args);
    }

    protected Stream<Args> args() {
        return IntStream.of(5, 10, 20).boxed().flatMap(this::colorchecker);
    }

    public Stream<Args> colorchecker(int window) {
        Builder<Args> builder = Stream.builder();

        for (int i = 0; i < CLASSIC_24.size(); i++) {
            var column = CLASSIC_24.get(i);

            for (int j = 0; j < column.size(); j++) {
                builder.add(colorchecker(window, i, j));
            }
        }

        return builder.build();
    }

    public Args colorchecker(int window, int i, int j) {
        String sequence = String.valueOf((char) ('A' + i)) + (j + 1);
        String name = CLASSIC_24_NAMES.get(i).get(j);

        double[] buf = CLASSIC_24.get(i).get(j).CIEXYZ().array();

        double peak = transfer.getNominalDisplayPeakLuminance();
        if (peak > 100.0) { // Scale to 100 nit for HDR
            multiply(buf, buf, 100.0 / peak);
        }

        matrix.XYZtoRGB.multiply(ADAPTATION.multiply(buf, buf), buf);

        int[] yuv = round(
                matrix.toCodes(matrix.fromLinearRGB(buf, buf), buf));

        return new Args("ColorChecker",
                sequence + "-" + toCamelCase(name), "ColorChecker",
                sequence + " - " + name, window, yuv);
    }

    private String toCamelCase(final String init) {
        final StringBuilder ret = new StringBuilder(init.length());

        for (String word : init.split("\\s")) {
            if (!word.isEmpty()) {
                ret.append(toUpperCase(word.charAt(0)));
                ret.append(word.substring(1));
            }
        }

        return ret.toString();
    }
}
