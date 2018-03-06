package band.full.testing.video.core;

import band.full.testing.video.itu.ColorMatrix;

/**
 * @author Igor Malinin
 */
public class FrameBuffer {
    public final Plane Y;
    public final Plane U;
    public final Plane V;

    public final ColorMatrix matrix;

    public FrameBuffer(Resolution r, ColorMatrix matrix) {
        Y = new Plane(r.width, r.height, matrix.YMIN);

        // TODO Other than 4:2:0 subsampling
        int wChroma = r.width / 2;
        int hChroma = r.height / 2;

        U = new Plane(wChroma, hChroma, matrix.ACHROMATIC);
        V = new Plane(wChroma, hChroma, matrix.ACHROMATIC);

        this.matrix = matrix;
    }

    public void fill(int[] yuv) {
        fill(yuv[0], yuv[1], yuv[2]);
    }

    public void fill(int yValue, int uValue, int vValue) {
        Y.fill(yValue);
        U.fill(uValue);
        V.fill(vValue);
    }

    public void fillRect(int x, int y, int w, int h, int[] yuv) {
        fillRect(x, y, w, h, yuv[0], yuv[1], yuv[2]);
    }

    public void fillRect(int x, int y, int w, int h,
            int yValue, int uValue, int vValue) {
        Y.fillRect(x, y, w, h, yValue);

        // FIXME: precise cw, ch
        int cx = x >> 1, cy = y >> 1, cw = (w + 1) >> 1, ch = (h + 1) >> 1;

        U.fillRect(cx, cy, cw, ch, uValue);
        V.fillRect(cx, cy, cw, ch, vValue);
    }

    public void verify(FrameBuffer expected) {
        Y.verify(expected.Y);
        U.verify(expected.U);
        V.verify(expected.V);
    }

    public void verify(FrameBuffer expected, int deviation, double maxMisses) {
        Y.verify(expected.Y, deviation, maxMisses);
        U.verify(expected.U, deviation, maxMisses);
        V.verify(expected.V, deviation, maxMisses);
    }

    public void verify(FrameBuffer expected, int deviation, int maxMisses) {
        Y.verify(expected.Y, deviation, maxMisses);
        U.verify(expected.U, deviation, maxMisses);
        V.verify(expected.V, deviation, maxMisses);
    }

    public void verifyRect(int x, int y, int w, int h,
            int yValue, int uValue, int vValue) {
        Y.verifyRect(x, y, w, h, yValue);

        // FIXME: precise cw, ch
        int cx = x >> 1, cy = y >> 1, cw = (w + 1) >> 1, ch = (h + 1) >> 1;

        U.verifyRect(cx, cy, cw, ch, uValue);
        V.verifyRect(cx, cy, cw, ch, vValue);
    }
}
