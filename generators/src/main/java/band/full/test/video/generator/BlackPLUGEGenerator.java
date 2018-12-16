package band.full.test.video.generator;

import static band.full.video.itu.ColorRange.NARROW;
import static java.lang.Math.max;
import static javafx.scene.layout.Background.EMPTY;
import static javafx.scene.text.Font.font;

import band.full.test.video.encoder.DecoderY4M;
import band.full.test.video.encoder.EncoderParameters;
import band.full.test.video.encoder.EncoderY4M;
import band.full.test.video.executor.FrameVerifier;
import band.full.test.video.executor.FxImage;
import band.full.video.buffer.FrameBuffer;
import band.full.video.buffer.Plane;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    public final int step1;
    public final int step2;
    public final int step4;

    public BlackPLUGEGenerator(GeneratorFactory factory,
            EncoderParameters params, NalUnitPostProcessor<Void> processor,
            MuxerFactory muxer, String folder, String group) {
        super(factory, params, processor, muxer, folder, "BlackPLUGE", group);

        if (matrix.range == NARROW) {
            step1 = (matrix.YMAX - matrix.YMIN) / 100;
            step2 = (matrix.YMAX - matrix.YMIN) / 50;
            step4 = (matrix.YMAX - matrix.YMIN) / 25;
        } else {
            step1 = step2 = step4 = matrix.YMIN;
        }
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

        if (phase != null) {
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
        fillVerticalBars(fb.Y);
        fillLeftBars(fb.Y);

        if (matrix.range == NARROW) {
            fillRightBars(fb.Y);
        }

        return fb;
    }

    private void fillVerticalBars(Plane luma) {
        if (matrix.range == NARROW) {
            fillVerticalBar(luma, 0, matrix.YMIN - step2);
            fillVerticalBar(luma, 1, matrix.YMIN - step1);
        }

        fillVerticalBar(luma, 2, matrix.YMIN + step1);
        fillVerticalBar(luma, 3, matrix.YMIN + step2);
    }

    private void fillVerticalBar(Plane luma, int n, int code) {
        luma.fillRect(width / 24 * (n * 4 + 5), 0, width / 12, height, code);
    }

    private void fillLeftBars(Plane luma) {
        int fine = max(1, 1 << bitdepth - 9);
        int w = width / 24 * 5;
        int h = height / 27, h5 = h * 5;
        luma.fillRect(0, 0, w, h5, matrix.YMIN + fine * 2);
        luma.fillRect(0, h5 * 2, w, h5 * 2, matrix.YMIN + fine);
        luma.fillRect(0, h5 * 5, w, height - h5 * 5, matrix.YMIN + fine * 3);
    }

    private void fillRightBars(Plane luma) {
        int x = width / 24 * 19, w = width - x;
        int h = height / 27, h5 = h * 5;
        luma.fillRect(x, 0, w, h5, matrix.YMIN - step2);
        luma.fillRect(x, h5 * 2, w, h5 * 2, matrix.VMIN);
        luma.fillRect(x, h5 * 5, w, height - h5 * 5, matrix.YMIN - step4);
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

            fb.Y.fillRect(x, h5 * 5, w, height - h5 * 5,
                    matrix.YMIN - step4);

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
