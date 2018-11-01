package band.full.test.video.generator;

import static java.lang.String.format;

import band.full.test.video.encoder.EncoderParameters;
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
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder, group);
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
        StringBuilder buf = new StringBuilder(args.file);
        if (args.window != 0) {
            buf.append(args.window);
        }

        return buf.append(PG_SEPARATOR).append(group)
                .append('-').append(args.sequence).toString();
    }
}
