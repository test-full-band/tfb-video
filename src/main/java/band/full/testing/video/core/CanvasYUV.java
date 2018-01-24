package band.full.testing.video.core;

import static band.full.testing.video.core.Quantizer.round;
import static band.full.testing.video.executor.FxDisplay.runAndWait;
import static javafx.scene.paint.Color.TRANSPARENT;

import band.full.testing.video.itu.YCbCr;

import java.util.function.Consumer;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

/**
 * @author Igor Malinin
 */
public class CanvasYUV {
    public final Plane Y;
    public final Plane U;
    public final Plane V;

    public final YCbCr matrix;

    public CanvasYUV(Resolution r, YCbCr matrix) {
        Y = new Plane(r.width, r.height, matrix.YMIN);

        // TODO Other than 4:2:0 subsampling
        int wChroma = r.width / 2;
        int hChroma = r.height / 2;

        U = new Plane(wChroma, hChroma, matrix.ACHROMATIC);
        V = new Plane(wChroma, hChroma, matrix.ACHROMATIC);

        this.matrix = matrix;
    }

    public void fill(int yValue, int uValue, int vValue) {
        Y.fill(yValue);
        U.fill(uValue);
        V.fill(vValue);
    }

    public void fillRect(int x, int y, int w, int h,
            int yValue, int uValue, int vValue) {
        Y.fillRect(x, y, w, h, yValue);

        // FIXME: precise cw, ch
        int cx = x >> 1, cy = y >> 1, cw = (w + 1) >> 1, ch = (h + 1) >> 1;

        U.fillRect(cx, cy, cw, ch, uValue);
        V.fillRect(cx, cy, cw, ch, vValue);
    }

    public void verifyRect(int x, int y, int w, int h,
            int yValue, int uValue, int vValue) {
        Y.verifyRect(x, y, w, h, yValue);

        // FIXME: precise cw, ch
        int cx = x >> 1, cy = y >> 1, cw = (w + 1) >> 1, ch = (h + 1) >> 1;

        U.verifyRect(cx, cy, cw, ch, uValue);
        V.verifyRect(cx, cy, cw, ch, vValue);
    }

    public void overlay(Parent parent) {
        overlay(image -> {
            new Scene(parent, image.getWidth(), image.getHeight());
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(TRANSPARENT);
            parent.snapshot(params, image);
        });
    }

    public void overlay(Consumer<WritableImage> consumer) {
        WritableImage image = new WritableImage(Y.width, Y.height);
        runAndWait(() -> consumer.accept(image));
        overlay(image);
    }

    /**
     * Use an image as an overlay. Quality rendering is not considered here as
     * this method purpose is for labels and technical marks. Chroma
     * downsampling just drops samples with odd <em>x</em> and <em>y</em>
     * coordinates. If image pixel has alpha value 0 then original canvas value
     * under it is losslessly retained. Otherwise ordinary rounding is used when
     * applying non fully transparent overlay data.
     */
    public void overlay(Image image) {
        PixelReader reader = image.getPixelReader();

        for (int y = 0; y < Y.height; y++) {
            boolean hasChromaY = (y & 1) == 0;

            for (int x = 0; x < Y.width; x++) {
                boolean hasChromaX = (x & 1) == 0;

                // TODO PERF: make reading image data more efficient
                int argb = reader.getArgb(x, y);

                int alpha = argb >>> 24;
                if (alpha == 0) {
                    continue; // transparent overlay -> skip math
                }

                double opacity = alpha / 255.0;
                double transparency = 1.0 - opacity;

                double overR = ((argb >> 16) & 0xff) / 255.0;
                double overG = ((argb >> 8) & 0xff) / 255.0;
                double overB = ((argb) & 0xff) / 255.0;

                YCbCr params = matrix;

                double overY = params.getY(overR, overG, overB);
                double oldY = params.fromLumaCode(Y.get(x, y));
                double newY = oldY * transparency + overY * opacity;
                Y.set(x, y, round(params.toLumaCode(newY)));

                if (hasChromaX && hasChromaY) {
                    // for overlay chroma just drop in-between samples (equals
                    // to nearest neighbor) there is no need of higher quality
                    int cx = x >> 1, cy = y >> 1;

                    double overU = params.getCb(overY, overB);
                    double oldU = params.fromChromaCode(U.get(cx, cy));
                    double newU = oldU * transparency + overU * opacity;
                    U.set(cx, cy, round(params.toChromaCode(newU)));

                    double overV = params.getCr(overY, overR);
                    double oldV = params.fromChromaCode(V.get(cx, cy));
                    double newV = oldV * transparency + overV * opacity;
                    V.set(cx, cy, round(params.toChromaCode(newV)));
                }
            }
        }
    }
}
