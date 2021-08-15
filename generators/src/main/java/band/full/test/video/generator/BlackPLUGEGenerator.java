package band.full.test.video.generator;

import static band.full.video.itu.ColorRange.NARROW;
import static java.lang.Math.max;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.text.Font.font;

import band.full.core.Quantizer;
import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;
import band.full.test.video.executor.FrameVerifier;
import band.full.test.video.executor.FxImage;
import band.full.video.buffer.FrameBuffer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Patterns for setting up black level brightness.
 *
 * @author Igor Malinin
 */
public class BlackPLUGEGenerator extends GeneratorBase<Void> {
    private final double fine;

    public BlackPLUGEGenerator(GeneratorFactory factory,
            EncoderParameters params, NalUnitPostProcessor<Void> processor,
            MuxerFactory muxer, String folder, String group) {
        super(factory, params, processor, muxer, folder, "BlackPLUGE", group);

        fine = matrix.fromLumaCode(matrix.YMIN + max(1, 1 << bitdepth - 8));
    }

    private int[] yuv(double y) {
        double[] buf = {y, y, y};

        int[] codes = matrix.toCodes(matrix.fromRGB(buf, buf),
                Quantizer::round, new int[3]);
        System.out.println(Arrays.toString(codes));
        return codes;
    }

    @Override
    public List<String> encode(File dir, Void args)
            throws IOException, InterruptedException {
        var all = new ArrayList<String>(PATTERN_SECONDS);
        all.addAll(encode(dir, args, INTRO, INTRO_SECONDS));
        all.addAll(encode(dir, args, BODY, BODY_SECONDS));
        return all;
    }

    @Override
    protected void encode(EncoderY4M e, Void args, String phase) {
        var fb = e.newFrameBuffer();

        draw(fb);

        if (phase == INTRO) {
            FxImage.overlay(overlay(params), fb);
        }

        e.render(gop, () -> fb);
    }

    /**
     * Fills passed Y plane so that each vertical column has individual luma
     * code value starting from <code>yMin</code> and increment of 1 for every
     * next column.
     */
    public FrameBuffer draw(FrameBuffer fb) {
        fillVerticalBars(fb);

        double fine = matrix.fromLumaCode(
                matrix.YMIN + max(1, 1 << bitdepth - 8));

        fillLeftBars(fb, fine);

        if (matrix.range == NARROW) {
            fillRightBars(fb, fine);
        }

        return fb;
    }

    private void fillVerticalBars(FrameBuffer fb) {
        if (matrix.range == NARROW) {
            fillVerticalBar(fb, 0, yuv(-0.04));
            fillVerticalBar(fb, 1, yuv(-0.02));
        }

        fillVerticalBar(fb, 2, yuv(0.02));
        fillVerticalBar(fb, 3, yuv(0.04));
    }

    private void fillVerticalBar(FrameBuffer fb, int n, int[] yuv) {
        fb.fillRect(width / 24 * (n * 4 + 5), 0, width / 12, height, yuv);
    }

    private void fillLeftBars(FrameBuffer fb, double fine) {
        int w = width / 24 * 5;
        int h = height / 27, h5 = h * 5;
        fb.fillRect(0, 0, w, h5, yuv(fine * 2));
        fb.fillRect(0, h5 * 2, w, h5 * 2, yuv(fine));
        fb.fillRect(0, h5 * 5, w, height - h5 * 5, yuv(fine * 3));
    }

    private void fillRightBars(FrameBuffer fb, double fine) {
        int x = width / 24 * 19, w = width - x;
        int h = height / 27, h5 = h * 5;

        fb.fillRect(x, 0, w, h5, yuv(fine * -2));
        fb.fillRect(x, h5 * 2, w, h5 * 2, yuv(fine * -1));
        fb.fillRect(x, h5 * 5, w, height - h5 * 5, yuv(fine * -3));
    }

    @Override
    protected void verify(File dir, String mp4, Void args) {
        verify(dir, mp4, INTRO_SECONDS - 1, 2, args);
    }

    @Override
    protected void verify(DecoderY4M d, Void args) {
        var expected = draw(d.newFrameBuffer());

        d.read(fb -> {
            // cover overlay before verification
            int x = width / 24 * 19, w = width - x;
            int h5 = height / 27 * 5;

            fb.fillRect(x, h5 * 5, w, height - h5 * 5, yuv(fine * -3));

            FrameVerifier.verify(expected, fb, 4, 0.002);
        });
    }

    protected Parent overlay(EncoderParameters params) {
        double peak = transfer.getNominalDisplayPeakLuminance();

        int x = width / 24 * 19, w = width - x;
        int h = height / 27, h5 = h * 5;

        Pane grid = new Pane(text(x, h5 * 5, w, height - h5 * 5,
                Color.gray(transfer.fromLinear(2.0 / peak)),
                font(height / 30), "test.full.band"));

        grid.setBackground(EMPTY);
        return grid;
    }
}
