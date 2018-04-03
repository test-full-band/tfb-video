package band.full.test.video.generator;

import static band.full.core.Quantizer.round;
import static band.full.core.color.CIEXYZ.ILLUMINANT_D50;
import static band.full.core.color.ChromaticAdaptation.bradford;
import static band.full.test.video.generator.ColorChecker.CLASSIC_24;
import static band.full.test.video.generator.ColorChecker.CLASSIC_24_NAMES;
import static java.lang.Character.toUpperCase;
import static java.lang.String.format;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.video.encoder.EncoderParameters;
import band.full.video.itu.ICtCp;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Base class for creating single-color patches in the middle of the screen with
 * specified area percentage.
 *
 * @author Igor Malinin
 */
@TestInstance(PER_CLASS)
public class ColorPatchesGenerator extends PatchesGenerator {
    public ColorPatchesGenerator(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, pattern);
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("colorchecker")
    public void colorchecker(Args args) {
        generate(args);
    }

    public Stream<Args> colorchecker(int window) {
        var adaptation = bradford(
                ILLUMINANT_D50, matrix.primaries.white.CIEXYZ());

        var result = new ArrayList<Args>();

        for (int i = 0; i < CLASSIC_24.size(); i++) {
            String alpha = String.valueOf((char) ('A' + i));

            var column = CLASSIC_24.get(i);
            var names = CLASSIC_24_NAMES.get(i);

            for (int j = 0; j < column.size(); j++) {
                String sequence = alpha + (j + 1);
                String name = names.get(j);

                double[] buf = column.get(j).CIEXYZ().array();
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
            if (!(ret.length() == init.length())) {
                ret.append(" ");
            }
        }

        return ret.toString();
    }

    @Override
    protected String getBottomCenterText(Args args) {
        String fmt = matrix instanceof ICtCp ? "I%d T%d P%d" : "Y%d U%d V%d";

        double[] buf = matrix.fromCodes(args.yuv, new double[3]);
        matrix.toRGBCodes(matrix.toRGB(buf, buf), buf);

        var df = new DecimalFormat("#.#");

        return format(fmt + "    |    R%s G%s B%s",
                args.yuv[0], args.yuv[1], args.yuv[2],
                df.format(buf[0]), df.format(buf[1]), df.format(buf[2]));
    }

    @Override
    protected String getFileName(Args args) {
        if (args.window == 0) return factory.folder + '/' + folder +
                format("/Fill/%s/%s-%s-%s",
                        args.file, args.file, pattern, args.sequence);

        return factory.folder + '/' + folder +
                format("/Win%02d/%s/%s%d-%s-%s",
                        args.window, args.file, args.file, args.window,
                        pattern, args.sequence);
    }
}
