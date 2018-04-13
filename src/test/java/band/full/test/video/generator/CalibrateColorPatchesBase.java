package band.full.test.video.generator;

import static java.lang.String.format;

import band.full.video.encoder.EncoderParameters;
import band.full.video.itu.ICtCp;

import java.text.DecimalFormat;

/**
 * Base class for creating single-color patches in the middle of the screen with
 * specified area percentage.
 *
 * @author Igor Malinin
 */
public abstract class CalibrateColorPatchesBase extends CalibratePatchesBase {
    public CalibrateColorPatchesBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, pattern);
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
    protected String getFolder(Args args) {
        if (args.window == 0) return factory.folder + '/' + folder +
                format("/Fill/%s", args.file);

        return factory.folder + '/' + folder +
                format("/Win%02d/%s", args.window, args.file);
    }

    @Override
    protected String getPattern(Args args) {
        if (args.window == 0)
            return format("%s-%s-%s",
                    args.file, pattern, args.sequence);

        return format("%s%d-%s-%s",
                args.file, args.window, pattern, args.sequence);
    }
}
