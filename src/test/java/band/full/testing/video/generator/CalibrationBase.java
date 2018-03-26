package band.full.testing.video.generator;

import static band.full.testing.video.color.CIEXYZ.ILLUMINANT_D50;
import static band.full.testing.video.color.ChromaticAdaptation.bradford;
import static band.full.testing.video.core.Quantizer.round;
import static band.full.testing.video.core.Window.proportional;
import static band.full.testing.video.core.Window.screen;
import static band.full.testing.video.core.Window.square;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;
import static band.full.testing.video.generator.ColorChecker.CLASSIC_24;
import static band.full.testing.video.generator.ColorChecker.CLASSIC_24_NAMES;
import static band.full.testing.video.generator.GeneratorFactory.HEVC;
import static band.full.testing.video.itu.BT1886.TRUE_BLACK_TRANSFER;
import static java.lang.Math.floor;
import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.stream.IntStream.concat;
import static java.util.stream.IntStream.iterate;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.concat;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.text.Font.font;
import static javafx.scene.text.TextAlignment.CENTER;
import static javafx.scene.text.TextAlignment.LEFT;
import static javafx.scene.text.TextAlignment.RIGHT;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.testing.video.color.CIEXYZ;
import band.full.testing.video.color.CIExy;
import band.full.testing.video.color.CIExyY;
import band.full.testing.video.core.FrameBuffer;
import band.full.testing.video.core.Window;
import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;
import band.full.testing.video.executor.FxImage;
import band.full.testing.video.itu.ICtCp;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * Base class for creating single-color patches in the middle of the screen with
 * specified area percentage.
 *
 * @author Igor Malinin
 */
@TestInstance(PER_CLASS)
public class CalibrationBase
        extends ParametrizedGeneratorBase<CalibrationBase.Args> {
    protected static final Duration DURATION_INTRO = ofSeconds(5);
    protected static final Duration DURATION = ofSeconds(25);

    public static class Args {
        public final String file;
        public final String sequence;
        public final String set;
        public final String label;
        public final int window; // Use 0 for 100% screen fill!
        public final int[] yuv;

        public Args(String file, String sequence, String set,
                String label, int window, int[] yuv) {
            this.file = file;
            this.sequence = sequence;
            this.set = set;
            this.label = label;
            this.window = window;
            this.yuv = yuv;
        }

        public Args(String file, String sequence, String set, String label,
                int window, int y, int u, int v) {
            this(file, sequence, set, label, window, new int[] {y, u, v});
        }

        @Override
        public String toString() {
            return format("Win%02d %s %03d:%03d:%03d",
                    window, sequence, yuv[0], yuv[1], yuv[2]);
        }
    }

    public CalibrationBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, pattern);
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("grayscale")
    public void grayscale(Args args) {
        generate(args);
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("colorchecker")
    public void colorchecker(Args args) {
        generate(args);
    }

    public Stream<Args> grayscale() {
        return IntStream.of(0, 5, 10, 20, 50).boxed().flatMap(this::grayscale);
    }

    public Stream<Args> colorchecker() {
        return IntStream.of(5, 10, 20).boxed().flatMap(this::colorchecker);
    }

    public Stream<Args> grayscale(int window) {
        var percents = concat(range(0, 5),
                iterate(5, i -> i < 100, i -> i + 5));

        // show brightest and darkest patterns in the beginning
        var first = Stream.of(gray(window, "$$", matrix.YMAX));
        var steps = percents.mapToObj(grayArgs(window));
        var white = grayscaleWhite(window);
        return concat(concat(first, steps), white);
    }

    /** White and Whiter than White (WtW) for narrow range encodes */
    public Stream<Args> grayscaleWhite(int window) {
        if (matrix.YMAX == matrix.VMAX)
            return Stream.of(gray(window, "X0", matrix.YMAX));

        int yX5 = round(matrix.toLumaCode(1.05));

        return Stream.of(
                gray(window, "X0", matrix.YMAX),
                gray(window, "X5", yX5), // WtW
                gray(window, "X9", matrix.VMAX));
    }

    private IntFunction<Args> grayArgs(int window) {
        return n -> {
            double ye = n / 100.0;
            int y = round(matrix.toLumaCode(ye));
            double percent = matrix.fromLumaCode(y) * 100.0;
            String sequence = format("%02.0f", percent);
            return gray(window, sequence, y);
        };
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

                double[] buf = column.get(j).CIEXYZ().array();
                matrix.XYZtoRGB.multiply(adaptation.multiply(buf, buf), buf);

                int[] yuv = round(
                        matrix.toCodes(matrix.fromLinearRGB(buf, buf), buf));

                result.add(new Args("ColorChecker", sequence, "ColorChecker",
                        sequence + " - " + names.get(j), window, yuv));
            }
        }

        return result.stream();
    }

    public Args gray(int window, String sequence, int y) {
        int c0 = matrix.ACHROMATIC;
        return new Args("Grayscale", sequence, "Grayscale",
                format("%.1f%% White", matrix.fromLumaCode(y) * 100.0),
                window, y, c0, c0);
    }

    protected String formatCIE(CIExyY xyY) {
        double l = xyY.Y * matrix.transfer.getNominalDisplayPeakLuminance();
        String luminance = formatLuminance(l);
        return format("CIE(x=%.5f, y=%.5f) %s", xyY.x, xyY.y, luminance);
    }

    /**
     * Limit fractional part in rage of 0 to 6 digits while trying to keep
     * precision at 5 digits.
     */
    protected String formatLuminance(double l) {
        int d = max(4 - (int) floor(max(-2.0, log10(l))), 0);
        return format("%." + d + "f cd/m²", l);
    }

    protected String getTopLeftText(Args args) {
        if (matrix.transfer.code() == 18) return "Hybrid Log-Gamma";
        if (matrix.transfer.isDefinedByEOTF()) return "Encoded with EOTF";

        CIExyY xyY = getColor(args);

        return format("OETF: %s %s", matrix.transfer, formatCIE(xyY));
    }

    protected String getBottomLeftText(Args args) {
        String name = matrix.transfer.isDefinedByEOTF()
                ? matrix.transfer.toString()
                : "BT.1866";

        DoubleUnaryOperator eotf = matrix.transfer.isDefinedByEOTF()
                ? matrix.transfer::toLinear
                : TRUE_BLACK_TRANSFER::eotf;

        CIExyY xyY = getColor(args, eotf);

        return format("EOTF: %s %s", name, formatCIE(xyY));
    }

    protected String getTopCenterText(Args args) {
        return args.label;
    }

    protected String getBottomCenterText(Args args) {
        int[] yuv = args.yuv;

        if (matrix.isAchromatic(yuv)) return "Code " + yuv[0];

        String fmt = matrix instanceof ICtCp ? "ITP" : "YUV";

        double[] buf = matrix.fromCodes(yuv, new double[3]);
        matrix.toRGBCodes(matrix.toRGB(buf, buf), buf);

        var df = new DecimalFormat("#.#");

        return format("%s %d:%d:%d        RGB %s:%s:%s",
                fmt, yuv[0], yuv[1], yuv[2],
                df.format(buf[0]), df.format(buf[1]), df.format(buf[2]));
    }

    protected String getTopRightText(Args args) {
        return args.set;
    }

    protected String getBottomRightText(Args args) {
        return format("%dx%dp", resolution.width, resolution.height);
    }

    @Override
    protected String getFileName(Args args) {
        if (args.window == 0) return factory.folder + '/' + folder +
                format("/Fill/%s/%s-%s-%s-Y%03d",
                        args.file, args.file, pattern, args.sequence,
                        args.yuv[0]); // TODO: color?

        return factory.folder + '/' + folder +
                format("/Win%02d/%s/%s%d-%s-%s-Y%03d",
                        args.window, args.file, args.file, args.window,
                        pattern, args.sequence, args.yuv[0]); // TODO: color?
    }

    @Override
    protected void encode(EncoderY4M e, Args args) {
        var win = getWindow(args.window);

        var fb = e.newFrameBuffer();
        fb.fillRect(win.x, win.y, win.width, win.height, args.yuv);

        FxImage.overlay(overlay(args), fb);
        e.render(DURATION_INTRO, () -> fb);

        fb.clear();
        fb.fillRect(win.x, win.y, win.width, win.height, args.yuv);
        e.render(DURATION, () -> fb);
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        d.read(fb -> verify(fb, args));
    }

    protected void verify(FrameBuffer fb, Args args) {
        var win = getUnmarkedWindow(args.window);

        // TODO near-lossless target, allow up to 1% tiny single-step misses
        fb.verifyRect(win.x, win.y, win.width, win.height, args.yuv);
    }

    private Window getWindow(int window) {
        if (window == 0) return screen(resolution);

        double area = window / 100.0;

        return window < 50
                ? square(resolution, area)
                : proportional(resolution, area);
    }

    private Window getUnmarkedWindow(int window) {
        if (window > 0) return getWindow(window);

        int height = resolution.height - resolution.height / 12;
        return Window.center(resolution, resolution.width, height);
    }

    protected Parent overlay(Args args) {
        Color fill = getTextFill(args);

        TextFlow topLeft = text(fill, getTopLeftText(args), LEFT);
        TextFlow topCenter = text(fill, getTopCenterText(args), CENTER);
        TextFlow topRight = text(fill, getTopRightText(args), RIGHT);
        TextFlow bottomLeft = text(fill, getBottomLeftText(args), LEFT);
        TextFlow bottomCenter = text(fill, getBottomCenterText(args), CENTER);
        TextFlow bottomRight = text(fill, getBottomRightText(args), RIGHT);

        StackPane top = new StackPane(topLeft, topCenter, topRight);
        StackPane bottom = new StackPane(bottomLeft, bottomCenter, bottomRight);

        BorderPane.setMargin(top, new Insets(20));
        BorderPane.setMargin(bottom, new Insets(20));

        BorderPane layout = new BorderPane();
        layout.setBackground(EMPTY);
        layout.setTop(top);
        layout.setBottom(bottom);

        return layout;
    }

    protected Color getTextFill(Args args) {
        double ye = matrix.fromLumaCode(args.yuv[0]);

        DoubleUnaryOperator eotfi = matrix.transfer.isDefinedByEOTF()
                ? matrix.transfer::fromLinear
                : TRUE_BLACK_TRANSFER::eotfi;

        double peak = matrix.transfer.getNominalDisplayPeakLuminance();
        double minY = eotfi.applyAsDouble(1.0 / peak);

        if (args.window == 0)
            return ye > minY ? Color.BLACK : Color.gray(ye + minY);

        double maxY = eotfi.applyAsDouble(20.0 / peak);
        return Color.gray(min(max(ye, minY), maxY));
    }

    protected TextFlow text(Color fill, String text, TextAlignment alignment) {
        Text label = new Text(text);
        label.setFont(font(resolution.height / 54));
        label.setFill(fill);

        TextFlow flow = new TextFlow(label);
        flow.setTextAlignment(alignment);
        return flow;
    }

    protected CIExyY getColor(Args args) {
        return getColor(args, matrix.transfer::toLinear);
    }

    protected CIExyY getColor(Args args, DoubleUnaryOperator eotf) {
        if (args.yuv[0] <= matrix.YMIN) {
            // fake color value for pure black
            CIExy white = matrix.primaries.white;
            return new CIExyY(white.x, white.y, 0);
        }

        double[] buf = matrix.fromCodes(args.yuv, new double[3]);
        matrix.toLinearRGB(eotf, buf, buf);
        matrix.RGBtoXYZ.multiply(buf, buf);

        return new CIEXYZ(buf).CIExyY();
    }

    public static void main(String[] args) {
        System.out.println(new CalibrationBase(HEVC, HDR10, "X", "X")
                .getColor(new Args("File", "SN", "Set",
                        "Label", 10, 0, 512, 512)));
    }
}
