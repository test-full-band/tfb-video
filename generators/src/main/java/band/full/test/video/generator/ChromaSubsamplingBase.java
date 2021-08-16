package band.full.test.video.generator;

import static band.full.core.Quantizer.round;
import static band.full.core.Resolution.STD_2160p;
import static band.full.video.smpte.ST2084.PQ;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.util.Arrays.fill;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;
import band.full.test.video.generator.ChromaSubsamplingBase.Args;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Testing quality of chroma upsampling.
 *
 * @author Igor Malinin
 */
@TestInstance(PER_CLASS)
public abstract class ChromaSubsamplingBase extends GeneratorBase<Args> {
    private static final int CENTER_X = STD_2160p.width() / 2;
    private static final int CENTER_Y = STD_2160p.height() / 2;
    private static final double MAX_DISTANCE = CENTER_Y;
    private static final double RANGE = 32; // Fmax / Fmin

    public static class Args {
        public final String suffix;
        public final Consumer<EncoderY4M> generator;

        public Args(String suffix, Consumer<EncoderY4M> generator) {
            this.suffix = suffix;
            this.generator = generator;
        }

        @Override
        public String toString() {
            return suffix;
        }
    }

    public ChromaSubsamplingBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String group) {
        super(factory, params, folder, "ConcentricLogSine", group);
    }

    public ChromaSubsamplingBase(GeneratorFactory factory,
            EncoderParameters params, NalUnitPostProcessor<Args> processor,
            MuxerFactory muxer, String folder, String group) {
        super(factory, params, processor, muxer,
                folder, "ConcentricLogSine", group);
    }

    @Override
    @ParameterizedTest(name = "{arguments}")
    @MethodSource("args")
    public void generate(Args args) {
        super.generate(args);
    }

    public Stream<Args> args() {
        return Stream.of(
                new Args("BlackWhiteE",
                        this::concentricBlackWhiteSineE),
                new Args("BlackWhiteO",
                        this::concentricBlackWhiteSineO),
                new Args("RedBlueE",
                        this::concentricRedBlueSineE));
    }

    @Override
    protected void encode(EncoderY4M e, Args args, String phase) {
        args.generator.accept(e);
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        // TODO
    }

    @Override
    protected String getPattern(Args args) {
        return super.getPattern(args) + '-' + args.suffix;
    }

    /**
     * Black & White concentric sine circles
     */
    public void concentricBlackWhiteSineE(EncoderY4M e) {
        var fb = e.newFrameBuffer();
        int grayY = round(matrix.toLumaCode(0.25));

        fb.Y.calculate((x, y) -> {
            double radius = r(x, y);

            return (radius > MAX_DISTANCE) ? grayY
                    : round(matrix.toLumaCode(
                            0.25 * (1.0 - cosineSweep(radius))));
        });

        short c0 = (short) matrix.ACHROMATIC;

        fill(fb.U.pixels, c0);
        fill(fb.V.pixels, c0);

        e.render(gop, () -> fb);
    }

    public void concentricBlackWhiteSineO(EncoderY4M e) {
        var fb = e.newFrameBuffer();
        int grayY = round(matrix.toLumaCode(0.25));

        double amp = PQ.toLinear(0.5) / 2.0;

        fb.Y.calculate((x, y) -> {
            double radius = r(x, y);

            return (radius > MAX_DISTANCE) ? grayY
                    : round(matrix.toLumaCode(
                            PQ.fromLinear(amp * (1.0 - cosineSweep(radius)))));
        });

        short c0 = (short) matrix.ACHROMATIC;

        fill(fb.U.pixels, c0);
        fill(fb.V.pixels, c0);

        e.render(gop, () -> fb);
    }

    /**
     * Concentric circles of varying width alternating Red and Blue with half
     * the resolution of the Y channel.
     */
    // TODO Find correct amplitudes according to BT.709 and DCI-P3 primaries
    public void concentricRedBlueSineE(EncoderY4M e) {
        var fb = e.newFrameBuffer();
        int grayY = round(matrix.toLumaCode(0.25));
        int c0 = matrix.ACHROMATIC;

        // reusable buffers
        var rgb = new double[3];
        var yuv = new double[3];

        for (int y = 0; y < fb.Y.height; y++) {
            boolean hasChromaY = (y & 1) == 0;

            for (int x = 0; x < fb.Y.width; x++) {
                boolean hasChromaX = (x & 1) == 0;

                double radius = r(x, y);

                if (radius > MAX_DISTANCE) {
                    fb.Y.set(x, y, grayY);

                    if (hasChromaX && hasChromaY) {
                        int cx = x >> 1, cy = y >> 1;

                        fb.U.set(cx, cy, c0);
                        fb.V.set(cx, cy, c0);
                    }
                } else {
                    double sin = 0.25 * sineSweepHalf(radius);

                    rgb[0] = 0.25 + sin;
                    rgb[1] = 0.0;
                    rgb[2] = 0.25 - sin;

                    matrix.fromRGB(rgb, yuv);

                    fb.Y.set(x, y, round(matrix.toLumaCode(yuv[0])));

                    if (hasChromaX && hasChromaY) {
                        int cx = x >> 1, cy = y >> 1;

                        fb.U.set(cx, cy, round(matrix.toChromaCode(yuv[1])));
                        fb.V.set(cx, cy, round(matrix.toChromaCode(yuv[2])));
                    }
                }
            }
        }

        e.render(gop, () -> fb);
    }

    /** luma log sine sweep */
    private double cosineSweep(double r) {
        double w1 = PI * MAX_DISTANCE / RANGE; // double frequency
        double L = 1.0 / log(RANGE);
        return cos(w1 * L * (exp(r / MAX_DISTANCE / L) - 1.0));
    }

    /** chroma half frequency log sine sweep */
    private double sineSweepHalf(double r) {
        double w1 = PI * MAX_DISTANCE / RANGE / 2.0;
        double L = 1.0 / log(RANGE);
        return sin(w1 * L * (exp(r / MAX_DISTANCE / L) - 1.0));
    }

    /** radius from screen center */
    private static double r(int x, int y) {
        int dX = CENTER_X - x;
        int dY = CENTER_Y - y;
        return sqrt(dX * dX + dY * dY);
    }
}
