package band.full.testing.video.core;

/**
 * @author Igor Malinin
 */
public class CanvasYCbCr {
    public final Plane Y;
    public final Plane Cb;
    public final Plane Cr;

    public CanvasYCbCr(int w, int h) {
        Y = new Plane(w, h);

        int wChroma = w / 2;
        int hChroma = h / 2;

        Cb = new Plane(wChroma, hChroma);
        Cr = new Plane(wChroma, hChroma);
    }

    public CanvasYCbCr(Resolution r) {
        this(r.width, r.height);
    }

    public void fill(short yValue, short cbValue, int crValue) {
        Y.fill(yValue);
        Cb.fill(cbValue);
        Cr.fill(crValue);
    }

    public void fillRect(int x, int y, int w, int h,
            int yValue, int cbValue, int crValue) {
        this.Y.fillRect(x, y, w, h, yValue);

        // FIXME: precise cw, ch
        int cx = x >> 1, cy = y >> 1, cw = (w + 1) >> 1, ch = (h + 1) >> 1;

        this.Cb.fillRect(cx, cy, cw, ch, cbValue);
        this.Cr.fillRect(cx, cy, cw, ch, crValue);
    }
}
