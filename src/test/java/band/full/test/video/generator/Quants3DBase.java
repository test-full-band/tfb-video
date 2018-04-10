package band.full.test.video.generator;

import static band.full.video.encoder.EncoderY4M.QUICK;
import static java.lang.String.format;
import static java.util.stream.IntStream.concat;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

import band.full.test.video.executor.FrameVerifier;
import band.full.test.video.generator.Quants3DBase.Args;
import band.full.video.buffer.FrameBuffer;
import band.full.video.encoder.DecoderY4M;
import band.full.video.encoder.EncoderParameters;
import band.full.video.encoder.EncoderY4M;
import band.full.video.encoder.MuxerMP4;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Testing color bands separation / dynamic quantization step uniformity.
 *
 * @author Igor Malinin
 */
public class Quants3DBase extends GeneratorBase<Args> {
    public static class Args {
        public final String speed;
        public final int frames;
        public final int lsb;

        public Args(String speed, int frames, int lsb) {
            this.speed = speed;
            this.frames = frames;
            this.lsb = lsb;
        }

        @Override
        public String toString() {
            return format("speed: %s, frames: %d, lsb: %d", speed, frames, lsb);
        }
    }

    @ParameterizedTest
    @MethodSource("params")
    public void quants(Args args) {
        generate(args);
    }

    protected static Stream<Args> params() {
        return IntStream.of(1, 4, 16).boxed().flatMap(lsb -> Stream.of(
                new Args("Fast", 2, lsb), new Args("Slow", 4, lsb)));
    }

    protected Quants3DBase(GeneratorFactory factory,
            EncoderParameters params, String folder) {
        super(factory, params, folder, "Quants3D");
    }

    protected Quants3DBase(GeneratorFactory factory,
            EncoderParameters params, String folder, String pattern) {
        super(factory, params, folder, "Quants3D-" + pattern);
    }

    @Override
    protected String getPattern(Args args) {
        return pattern + '-' + args.speed + args.lsb;
    }

    public IntStream bases(Args args) {
        int start = matrix.YMIN + args.lsb;
        return concat(
                range(0, gop * INTRO_SECONDS / args.frames).map(i -> start),
                rangeClosed(start, matrix.YMAX - args.lsb));
    }

    @Override
    public void encode(MuxerMP4 muxer, File dir, Args args)
            throws IOException, InterruptedException {
        encode(muxer, dir, args, null, 1);
    }

    @Override
    protected void encode(EncoderY4M e, Args args, String phase) {
        int uCode = matrix.ACHROMATIC;
        int vCode = matrix.ACHROMATIC;

        FrameBuffer fb = e.newFrameBuffer();
        // e.parameters.framerate.toFrames(ofMillis(145));

        bases(args).forEach(yCode -> {
            draw(fb, yCode, uCode, vCode, args.lsb);
            e.render(args.frames, () -> fb);
        });
    }

    @Override
    protected void verify(File dir, String mp4, Args args) {
        verify(dir, mp4, null, null, args);
    }

    @Override
    protected void verify(DecoderY4M d, Args args) {
        if (QUICK) return;

        int uCode = matrix.ACHROMATIC;
        int vCode = matrix.ACHROMATIC;

        FrameBuffer expected = d.newFrameBuffer();
        // d.parameters.framerate.toFrames(ofMillis(170));

        bases(args).forEach(yCode -> {
            draw(expected, yCode, uCode, vCode, args.lsb);

            // TODO more precise individual box verification
            d.read(args.frames, fb -> FrameVerifier.verify(
                    expected, fb, 2, 0.0005));
        });
    }

    private FrameBuffer draw(FrameBuffer fb,
            int yCode, int uCode, int vCode, int lsb) {
        int yMid = height >> 1;

        // single square box side truncated to an odd number
        int box = yMid / 9 << 1;
        int half = box / 2; // half of the square box size

        int xMid = width >> 1;

        // left/right space to a side four box pattern
        int margin = (xMid >> 1) - half * 5;

        int x1 = margin, x2 = xMid - half * 3, x3 = width - margin - box * 3;
        int y1 = box, y2 = yMid - half, y3 = height - box * 4;

        fb.fill(yCode, uCode, vCode);

        // top four box row

        fill(fb, x1, y1, box, yCode, uCode, vCode,
                -lsb, lsb, lsb, -lsb, -lsb, -lsb);

        fill(fb, x2, y1, box, yCode, uCode, vCode,
                0, lsb, 0, 0, 0, lsb);

        fill(fb, x3, y1, box, yCode, uCode, vCode,
                lsb, lsb, -lsb, -lsb, lsb, -lsb);

        // bottom four box row

        fill(fb, x1, y3, box, yCode, uCode, vCode,
                -lsb, lsb, 0, lsb, lsb, 0);

        fill(fb, x2, y3, box, yCode, uCode, vCode,
                0, -lsb, lsb, 0, -lsb, -lsb);

        fill(fb, x3, y3, box, yCode, uCode, vCode,
                lsb, 0, -lsb, lsb, 0, lsb);

        // mid left/right boxes
        int m1 = (x1 + x2) / 2 + box, m2 = (x2 + x3) / 2 + box;

        fb.fillRect(m1, y2, box, box, yCode + lsb, uCode, vCode);
        fb.fillRect(m2, y2, box, box, yCode - lsb, uCode, vCode);

        return fb;
    }

    private void fill(FrameBuffer fb, int x, int y, int box,
            int yCode, int uCode, int vCode,
            int yTop, int uTop, int vTop,
            int yLeft, int uLeft, int vLeft) {
        int box2 = box * 2;

        fb.fillRect(x + box, y, box, box, // top
                yCode + yTop, uCode + uTop, vCode + vTop);

        fb.fillRect(x + box, y + box2, box, box, // bottom
                yCode - yTop, uCode - uTop, vCode - vTop);

        fb.fillRect(x, y + box, box, box, // left
                yCode + yLeft, uCode + uLeft, vCode + vLeft);

        fb.fillRect(x + box2, y + box, box, box, // right
                yCode - yLeft, uCode - uLeft, vCode - vLeft);
    }
}
