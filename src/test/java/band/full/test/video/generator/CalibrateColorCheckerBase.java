package band.full.test.video.generator;

import static band.full.core.ArrayMath.multiply;
import static band.full.core.Quantizer.round;
import static band.full.core.color.CIEXYZ.ILLUMINANT_D50;
import static band.full.core.color.ChromaticAdaptation.bradford;
import static band.full.test.video.generator.ColorChecker.CLASSIC_24;
import static band.full.test.video.generator.ColorChecker.CLASSIC_24_NAMES;
import static java.lang.Character.toUpperCase;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.video.encoder.EncoderParameters;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Base class for creating single-color patches in the middle of the screen with
 * specified area percentage.
 *
 * @author Igor Malinin
 */
@TestInstance(PER_CLASS)
public class CalibrateColorCheckerBase extends CalibrateColorPatchesBase {
    public CalibrateColorCheckerBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder + "/Calibrate", pattern);
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("colorchecker")
    public void colorchecker(Args args) {
        generate(args);
    }

    public Stream<Args> colorchecker() {
        return IntStream.of(5, 10, 20).boxed().flatMap(this::colorchecker);
    }

    public Stream<Args> colorchecker(int window) {
        var adaptation = bradford(
                ILLUMINANT_D50, primaries.white.CIEXYZ());

        var result = new ArrayList<Args>();

        for (int i = 0; i < CLASSIC_24.size(); i++) {
            String alpha = String.valueOf((char) ('A' + i));

            var column = CLASSIC_24.get(i);
            var names = CLASSIC_24_NAMES.get(i);

            for (int j = 0; j < column.size(); j++) {
                String sequence = alpha + (j + 1);
                String name = names.get(j);

                double[] buf = column.get(j).CIEXYZ().array();

                double peak = transfer.getNominalDisplayPeakLuminance();
                if (peak > 100.0) { // Scale to 100 nit for HDR
                    multiply(buf, buf, 100.0 / peak);
                }

                matrix.XYZtoRGB.multiply(adaptation.multiply(buf, buf), buf);

                int[] yuv = round(
                        matrix.toCodes(matrix.fromLinearRGB(buf, buf), buf));

                result.add(new Args("ColorChecker",
                        sequence + "-" + toCamelCase(name), "ColorChecker",
                        sequence + " - " + name, window, yuv));
            }
        }

        return result.stream();
    }

    private String toCamelCase(final String init) {
        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split("\\s")) {
            if (!word.isEmpty()) {
                ret.append(toUpperCase(word.charAt(0)));
                ret.append(word.substring(1));
            }
        }

        return ret.toString();
    }
}
