package band.full.test.video.generator;

import static band.full.core.ArrayMath.multiply;
import static band.full.core.Quantizer.round;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.iterate;

import band.full.core.Quantizer;
import band.full.core.color.CIEXYZ;
import band.full.core.color.CIExy;
import band.full.test.video.encoder.EncoderParameters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class CalibrateSaturationBase_CalMAN
        extends CalibrateColorPatchesBase {
    public CalibrateSaturationBase_CalMAN(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder + "/Calibrate", group);
    }

    protected IntStream windows() {
        return IntStream.of(10, 20);
    }

    protected IntStream stimulus() {
        return IntStream.of(75, 100);
    }

    protected IntStream sweep() {
        return iterate(10, s -> s <= 100, s -> s + 10);
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("red")
    public void red(Args args) {
        generate(args);
    }

    public Stream<Args> red() {
        return sweep(primaries.red, "Red");
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("green")
    public void green(Args args) {
        generate(args);
    }

    public Stream<Args> green() {
        return sweep(primaries.green, "Green");
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("blue")
    public void blue(Args args) {
        generate(args);
    }

    public Stream<Args> blue() {
        return sweep(primaries.blue, "Blue");
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("cyan")
    public void cyan(Args args) {
        generate(args);
    }

    public Stream<Args> cyan() {
        return sweep(secondary(0.0, 1.0, 1.0), "Cyan");
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("magenta")
    public void magenta(Args args) {
        generate(args);
    }

    public Stream<Args> magenta() {
        return sweep(secondary(1.0, 0.0, 1.0), "Magenta");
    }

    @ParameterizedTest(name = "{arguments}")
    @MethodSource("yellow")
    public void yellow(Args args) {
        generate(args);
    }

    public Stream<Args> yellow() {
        return sweep(secondary(1.0, 1.0, 0.0), "Yellow");
    }

    public Stream<Args> sweep(CIExy color, String name) {
        return windows().boxed().flatMap(
                window -> stimulus().boxed().flatMap(
                        stimulus -> sweep(window, stimulus, color, name)));
    }

    public Stream<Args> sweep(int window, int stimulus,
            CIExy color, String name) {
        return sweep().mapToObj(s -> args(window, stimulus, color, name, s));
    }

    public Args args(int window, int stimulus,
            CIExy color, String name, int saturation) {
        String sti = stimulus < 100 ? format("%02d", stimulus) : "";
        String sat = saturation < 100 ? format("%02d", saturation) : "XX";

        return new Args(
                format("Saturation%s_CalMAN-%s", sti, name), sat,
                format("CalMAN Saturation %d%%", stimulus),
                format("%d%% %s", saturation, name), window,
                getYUV(stimulus, color, saturation));
    }

    public int[] getYUV(int stimulus, CIExy color, int saturation) {
        int[] codes = getRGB(stimulus, color, saturation);
        double[] buf = {codes[0], codes[1], codes[2]};
        matrix.fromRGB(matrix.fromLumaCode(buf, buf), buf);
        return matrix.toCodes(buf, Quantizer::round, codes);
    }

    /**
     * CalMAN rounds RGB values of higher bit depth signals to fill
     * <code>(bitdepth-8)</code> least significant bits with zeros.
     */
    public int[] getRGB(int stimulus, CIExy color, int saturation) {
        int m = 1 << bitdepth - 8;
        int code = round(matrix.toLumaCode(stimulus / 100.0) / m) * m;
        var max = transfer.toLinear(matrix.fromLumaCode(code));
        var xy = saturation(color, saturation / 100.0);
        var rgb = primaries.XYZtoRGB.multiply(xy.CIEXYZ().array());
        multiply(rgb, rgb, max / stream(rgb).max().getAsDouble());
        transfer.fromLinear(rgb, rgb);
        multiply(matrix.toRGBCodes(rgb, rgb), rgb, 1.0 / m);
        var codes = round(rgb);
        return multiply(codes, codes, m);
    }

    private CIExy saturation(CIExy color, double saturation) {
        var white = primaries.white;

        return new CIExy(
                white.x() + (color.x() - white.x()) * saturation,
                white.y() + (color.y() - white.y()) * saturation);
    }

    private CIExy secondary(double... rgb) {
        return new CIEXYZ(primaries.RGBtoXYZ.multiply(rgb)).CIExy();
    }
}
