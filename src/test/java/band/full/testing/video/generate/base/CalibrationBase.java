package band.full.testing.video.generate.base;

import static band.full.testing.video.color.CIEXYZ.ILLUMINANT_D50;
import static band.full.testing.video.color.ChromaticAdaptation.bradford;
import static band.full.testing.video.core.Quantizer.round;
import static band.full.testing.video.core.Window.proportional;
import static band.full.testing.video.core.Window.square;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;
import static band.full.testing.video.generate.base.ColorChecker.DIGITAL_SG;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.concat;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.text.Font.font;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.testing.video.color.CIELab;
import band.full.testing.video.color.CIEXYZ;
import band.full.testing.video.color.CIExy;
import band.full.testing.video.color.CIExyY;
import band.full.testing.video.color.Matrix3x3;
import band.full.testing.video.core.FrameBuffer;
import band.full.testing.video.core.Window;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.executor.FxImage;
import band.full.testing.video.generate.GeneratorFactory;
import band.full.testing.video.generate.ParametrizedGeneratorBase;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

/**
 * Base class for creating single-color patches in the middle of the screen with
 * specified area percentage.
 *
 * @author Igor Malinin
 */
@TestInstance(PER_CLASS)
public class CalibrationBase
        extends ParametrizedGeneratorBase<CalibrationBase.Args> {
    protected static final Duration DURATION = ofSeconds(30);

    public static class Args {
        public final String label;
        public final String file;
        public final int window;
        public final String sequence;
        public final int y, u, v;

        public Args(String label, String file, int window, String sequence,
                int y, int u, int v) {
            this.label = label;
            this.file = file;
            this.window = window;
            this.sequence = sequence;
            this.y = y;
            this.u = u;
            this.v = v;
        }

        @Override
        public String toString() {
            return format("Win%02d %s %03d:%03d:%03d",
                    window, sequence, y, u, v);
        }
    }

    public CalibrationBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, pattern);
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("grayscales")
    public void grayscale(Args args) {
        generate(args);
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("colorchecker")
    public void colorchecker(Args args) {
        generate(args);
    }

    private static final int GRAYSCALE_STEPS = 20;

    public Stream<Args> grayscales() {
        return IntStream.of(5, 10, 20, 50).boxed().flatMap(this::grayscale);
    }

    public List<Args> colorchecker() {
        Matrix3x3 adaptation = bradford(
                ILLUMINANT_D50, matrix.primaries.white.CIEXYZ());

        List<Args> result = new ArrayList<>();

        for (int i = 0; i < DIGITAL_SG.size(); i++) {
            String alpha = String.valueOf((char) ('A' + i));

            List<CIELab> column = DIGITAL_SG.get(i);
            for (int j = 0; j < column.size(); j++) {
                double[] buf = column.get(j).CIEXYZ().array();
                matrix.XYZtoRGB.multiply(adaptation.multiply(buf, buf), buf);

                int[] yuv = round(
                        matrix.toCodes(matrix.fromLinearRGB(buf, buf), buf));

                result.add(new Args("colorchecker", "ColorChecker", 10,
                        alpha + (j + 1), yuv[0], yuv[1], yuv[2]));
            }
        }

        return result;
    }

    public Stream<Args> grayscale(int window) {
        // show brightest and darkest patterns in the beginning
        Stream<Args> first = Stream.of(gray(window, "$$", matrix.YMAX));
        Stream<Args> mid = range(0, GRAYSCALE_STEPS).mapToObj(grayArgs(window));
        Stream<Args> last = Stream.of(gray(window, "X0", matrix.YMAX));

        return concat(concat(first, mid), last);
    }

    private IntFunction<Args> grayArgs(int window) {
        return i -> {
            double ye = (double) i / GRAYSCALE_STEPS;
            int y = round(matrix.toLumaCode(ye));
            double percent = matrix.fromLumaCode(y) * 100.0;
            String sequence = format("%02.0f", percent);
            return gray(window, sequence, y);
        };
    }

    public Args gray(int window, String sequence, int y) {
        int c0 = matrix.ACHROMATIC;
        return new Args("grayscale", "Gray", window, sequence, y, c0, c0);
    }

    protected String getLabelText(Args args) {
        double ye = matrix.fromLumaCode(args.y);
        CIExyY xyY = getColor(args);

        double nominalDisplayPeakLuminance;
        switch (matrix.transfer.code()) {
            case 16:
                nominalDisplayPeakLuminance = 10_000.0;
                break;

            case 18:
                nominalDisplayPeakLuminance = 1_000.0;
                break;

            default:
                nominalDisplayPeakLuminance = 100.0;
                break;
        }

        // TODO BT.1888 EOTF for BT.709 OETF

        return format(
                "%s %s CIE(x=%.4f, y=%.4f) %.1f cd/m² %.1f%% [%d:%d:%d]",
                pattern, args.label, xyY.x, xyY.y,
                xyY.Y * nominalDisplayPeakLuminance,
                ye * 100.0, args.y, args.u, args.v);
    }

    @Override
    protected String getFileName(Args args) {
        int window = args.window;
        String win = window <= 0 ? "Fill" : format("Win%02d", window);

        return factory.folder + '/' + folder + '/' +
                format("%s/%s%d-%s-%s-Y%03d",
                        win, args.file, window,
                        pattern, args.sequence, args.y);
    }

    @Override
    protected void encode(EncoderY4M e, Args args) {
        Window win = getWindow(args.window);

        FrameBuffer fb = e.newFrameBuffer();

        fb.fillRect(win.x, win.y, win.width, win.height,
                args.y, args.u, args.v);

        FxImage.overlay(overlay(args), fb);

        e.render(DURATION, () -> fb);
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        d.read(fb -> verify(fb, args));
    }

    protected void verify(FrameBuffer fb, Args args) {
        Window win = getWindow(args.window);

        // TODO near-lossless target, allow up to 1% tiny single-step misses
        fb.verifyRect(win.x, win.y, win.width, win.height,
                args.y, args.u, args.v);
    }

    private Window getWindow(int window) {
        if (window == 0)
            return new Window(0, 0, resolution.width, resolution.height);

        double area = window / 100.0;

        return window < 50
                ? square(resolution, area)
                : proportional(resolution, area);
    }

    protected Parent overlay(Args args) {
        double ye = matrix.fromLumaCode(args.y);

        Label label = new Label(getLabelText(args));
        label.setFont(font(resolution.height / 54));
        label.setTextFill(Color.gray(max(0.25, min(0.5, ye))));

        BorderPane.setMargin(label, new Insets(20));
        BorderPane layout = new BorderPane();
        layout.setBackground(EMPTY);
        layout.setBottom(label);
        return layout;
    }

    protected CIExyY getColor(Args args) {
        double[] buf = {args.y, args.u, args.v};
        matrix.fromCodes(buf, buf);

        if (buf[0] <= 0.0) {
            // fake color value for pure black
            CIExy white = matrix.primaries.white;
            return new CIExyY(white.x, white.y, 0);
        }

        matrix.toLinearRGB(buf, buf);
        matrix.RGBtoXYZ.multiply(buf, buf);

        return new CIEXYZ(buf).CIExyY();
    }

    public static void main(String[] args) {
        System.out.println(new CalibrationBase(HEVC, HDR10, "X", "X")
                .getColor(new Args("X", "X", 10, "X", 0, 512, 512)));
    }
}
