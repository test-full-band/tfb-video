package band.full.test.video.generator;

import static band.full.core.Window.screen;
import static band.full.video.itu.BT1886.TRUE_BLACK_TRANSFER;
import static java.lang.Math.floor;
import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.lang.String.format;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.text.Font.font;
import static javafx.scene.text.TextAlignment.CENTER;
import static javafx.scene.text.TextAlignment.LEFT;
import static javafx.scene.text.TextAlignment.RIGHT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.core.Window;
import band.full.core.color.CIEXYZ;
import band.full.core.color.CIExy;
import band.full.core.color.CIExyY;
import band.full.test.video.executor.FrameVerifier;
import band.full.test.video.executor.FxImage;
import band.full.test.video.generator.PatchesGenerator.Args;
import band.full.video.buffer.FrameBuffer;
import band.full.video.encoder.DecoderY4M;
import band.full.video.encoder.EncoderParameters;
import band.full.video.encoder.EncoderY4M;
import band.full.video.encoder.MuxerMP4;
import band.full.video.itu.ICtCp;

import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.function.DoubleUnaryOperator;

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
 * @see GrayscalePatchesGenerator
 * @see ColorPatchesGenerator
 */
@TestInstance(PER_CLASS)
public abstract class PatchesGenerator extends GeneratorBase<Args> {
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

    public PatchesGenerator(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, pattern);
    }

    protected String formatCIE(CIExyY xyY) {
        double l = xyY.Y * transfer.getNominalDisplayPeakLuminance();
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
        if (transfer.code() == 18) return "Hybrid Log-Gamma";
        if (transfer.isDefinedByEOTF()) return "Encoded with EOTF";

        CIExyY xyY = getColor(args);

        return format("OETF: %s %s", transfer, formatCIE(xyY));
    }

    protected String getBottomLeftText(Args args) {
        boolean useMatrixTransfer = transfer.code() == 18
                || transfer.isDefinedByEOTF();

        String name = useMatrixTransfer
                ? transfer.toString()
                : "BT.1866";

        DoubleUnaryOperator eotf = useMatrixTransfer
                ? transfer::toLinear
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
    public void encode(MuxerMP4 muxer, File dir, Args args)
            throws IOException, InterruptedException {
        encode(muxer, dir, args, "intro", INTRO_SECONDS);
        encode(muxer, dir, args, null, BODY_SECONDS);
    }

    @Override
    protected void encode(EncoderY4M e, Args args, String phase) {
        var win = getWindow(args.window);

        var fb = e.newFrameBuffer();
        fb.fillRect(win.x, win.y, win.width, win.height, args.yuv);

        if (phase != null) {
            FxImage.overlay(overlay(args), fb);
        }

        e.render(gop, () -> fb);
    }

    @Override
    protected void verify(File dir, String mp4, Args args) {
        verify(dir, mp4, INTRO_SECONDS - 1, 2, args);
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        d.read(fb -> verify(fb, args));
    }

    protected void verify(FrameBuffer fb, Args args) {
        var win = getVerifyWindow(args.window);
        int perimeter = win.x + win.y;

        // near-lossless target, allow a few single-step misses
        FrameVerifier.verifyRect(args.yuv, fb,
                win, 2, perimeter / 5, perimeter / 10);
    }

    private Window getWindow(int window) {
        if (window == 0) return screen(resolution);

        // assume strong alignment
        assertEquals(0, resolution.width % 8);
        assertEquals(0, resolution.width % 8);

        return window(window / 100.0);
    }

    private Window getVerifyWindow(int window) {
        // remove patch edges
        if (window > 0) return getWindow(window).shrink(4);

        // remove areas with labels
        int height = resolution.height - resolution.height / 10;
        return center(resolution.width, height);
    }

    public Window window(double area) {
        double target = resolution.width * resolution.height * area;
        return area < 0.5 ? square(target) : proportional(target, area);
    }

    protected Window square(double target) {
        int height = align(sqrt(target), resolution.height);
        int width = align(target / height, resolution.width);
        return center(width, height);
    }

    protected Window proportional(double target, double area) {
        int width = align(resolution.width * sqrt(area), resolution.width);
        int height = align(target / width, resolution.height);
        return center(width, height);
    }

    protected Window center(int w, int h) {
        int x = (resolution.width - w) >> 1;
        int y = (resolution.height - h) >> 1;
        return new Window(x, y, w, h);
    }

    /**
     * Assume screen sides are divisible by 8.
     * <p>
     * Make patch size divisible by 8 or 16 so it will be aligned to 8 pixels by
     * centering, only reducing size.
     */
    protected int align(double suggestion, int side) {
        int mask = side % 16 == 0 ? ~0xF : ~0x7;
        int masked = (int) suggestion & mask;
        if (side % 16 != 0 && masked % 16 == 0) {
            masked -= 8; // adjust for odd (side / 8) value
        }
        return masked;
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

        boolean useMatrixTransfer = transfer.code() == 18
                || transfer.isDefinedByEOTF();

        DoubleUnaryOperator eotfi = useMatrixTransfer
                ? transfer::fromLinear
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
        return getColor(args, transfer::toLinear);
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
}
