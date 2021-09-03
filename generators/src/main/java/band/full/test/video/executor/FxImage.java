package band.full.test.video.executor;

import static band.full.core.ArrayMath.multiply;
import static band.full.test.video.executor.FxDisplay.runAndWait;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static javafx.scene.paint.Color.TRANSPARENT;

import band.full.core.Dither;
import band.full.core.Quantizer;
import band.full.video.buffer.FrameBuffer;
import band.full.video.buffer.Plane;
import band.full.video.itu.ColorMatrix;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class FxImage {
    @FunctionalInterface
    public interface Transform {
        public int transform(double[] rgb);
    }

    public static void overlay(Parent parent, FrameBuffer fb) {
        overlay(image -> {
            new Scene(parent, image.getWidth(), image.getHeight());
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(TRANSPARENT);
            parent.snapshot(params, image);
        }, fb);
    }

    public static void overlay(Consumer<WritableImage> consumer,
            FrameBuffer fb) {
        WritableImage image = new WritableImage(fb.Y.width, fb.Y.height);
        runAndWait(() -> consumer.accept(image));
        overlay(image, fb);
    }

    /**
     * Use an image as an overlay. Quality rendering is not considered here as
     * this method purpose is for labels and technical marks. Chroma
     * downsampling just drops samples with odd <em>x</em> and <em>y</em>
     * coordinates. If image pixel has alpha value 0 then original frame buffer
     * value under it is losslessly retained. Otherwise ordinary rounding is
     * used when applying non fully transparent overlay data.
     */
    public static void overlay(Image image, FrameBuffer fb) {
        overlay(image, fb.matrix, Quantizer::round, fb);
    }

    public static void overlay(Image image, ColorMatrix matrix, Quantizer q,
            FrameBuffer fb) {
        var reader = image.getPixelReader();

        Plane Y = fb.Y, U = fb.U, V = fb.V;

        double srcPeak = matrix.transfer.getNominalDisplayPeakLuminance();
        double dstPeak = fb.matrix.transfer.getNominalDisplayPeakLuminance();
        double factor = srcPeak / dstPeak;

        // reusable buffers
        var rgbImg = new double[3];
        var rgbBuf = new double[3];
        var yuv = new double[3];

        for (int y = 0; y < fb.Y.height; y++) {
            boolean hasChromaY = (y & 1) == 0;

            for (int x = 0; x < fb.Y.width; x++) {
                boolean hasChromaX = (x & 1) == 0;

                // TODO PERF: make reading image data more efficient
                int argb = reader.getArgb(x, y);

                int alpha = argb >>> 24;
                if (alpha == 0) {
                    continue; // transparent overlay -> skip math
                }

                fromARGB(argb, rgbImg);
                matrix.transfer.toLinear(rgbImg, rgbImg);
                multiply(rgbImg, rgbImg, factor);
                fb.matrix.fromLinearRGB(rgbImg, yuv);

                double opacity = alpha / 255.0;
                double transparency = 1.0 - opacity;

                if (hasChromaX && hasChromaY) {
                    // for overlay chroma just drop in-between samples, assume
                    // chroma low-pass filtering is applied on the source image,
                    // otherwise color aliasing is possible but for simple
                    // purposes it could be good enough
                    int cx = x >> 1, cy = y >> 1;

                    yuv[0] = fb.matrix.fromLumaCode(Y.get(x, y));
                    yuv[1] = fb.matrix.fromChromaCode(U.get(cx, cy));
                    yuv[2] = fb.matrix.fromChromaCode(V.get(cx, cy));

                    fb.matrix.toLinearRGB(yuv, rgbBuf);

                    for (int i = 0; i < 3; i++) {
                        rgbBuf[i] = rgbBuf[i] * transparency
                                + rgbImg[i] * opacity;
                    }

                    fb.matrix.fromLinearRGB(rgbBuf, yuv);

                    Y.set(x, y, q.quantize(fb.matrix.toLumaCode(yuv[0])));
                    U.set(cx, cy, q.quantize(fb.matrix.toChromaCode(yuv[1])));
                    V.set(cx, cy, q.quantize(fb.matrix.toChromaCode(yuv[2])));
                } else {
                    double imgY = fb.matrix.transfer.toLinear(yuv[0]);

                    double bufY = fb.matrix.transfer.toLinear(
                            fb.matrix.fromLumaCode(Y.get(x, y)));

                    double newY = bufY * transparency + imgY * opacity;

                    Y.set(x, y, q.quantize(fb.matrix.toLumaCode(
                            fb.matrix.transfer.fromLinear(newY))));
                }
            }
        }
    }

    /**
     * Input is packed 32 bit ARGB (8 bit per component).<br>
     * Output is nonlinear YUV.
     */
    public static double[] fromARGB(int argb, double[] rgb) {
        rgb[0] = ((argb >> 16) & 0xff) / 255.0;
        rgb[1] = ((argb >> 8) & 0xff) / 255.0;
        rgb[2] = ((argb) & 0xff) / 255.0;

        return rgb;
    }

    public static void write(FrameBuffer fb, String file)
            throws IOException {
        write(fb, file, FxImage::transform);
    }

    public static void write(FrameBuffer fb, String file, Transform transform)
            throws IOException {
        var matrix = fb.matrix;

        Plane Y = fb.Y, U = fb.U, V = fb.V;

        var image = new BufferedImage(Y.width, Y.height, TYPE_INT_RGB);

        var buf = new double[3];

        for (int y = 0; y < Y.height; y++) {
            for (int x = 0; x < Y.width; x++) {
                buf[0] = matrix.fromLumaCode(Y.get(x, y));
                int x2 = x / 2, y2 = y / 2;
                buf[1] = matrix.fromChromaCode(U.get(x2, y2));
                buf[2] = matrix.fromChromaCode(V.get(x2, y2));

                matrix.toRGB(buf, buf);

                image.setRGB(x, y, transform.transform(buf));
            }
        }

        File dir = new File("target/video-sample/");
        dir.mkdirs();

        ImageIO.write(image, "png", new File(dir, file));
    }

    /**
     * Input is nonlinear RGB.<br>
     * Output is packed 32 bit ARGB with A=0 (opaque). Input buffer is
     * overwritten with converted RGB values;
     */
    public static int transform(double[] rgb) {
        int r = round(clip(rgb[0]) * 255.0);
        int g = round(clip(rgb[1]) * 255.0);
        int b = round(clip(rgb[2]) * 255.0);

        return (r << 16) + (g << 8) + b;
    }

    public static double[] offset(double[] rgb, double value) {
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] += value;
        }

        return rgb;
    }

    public static double[] amplify(double[] rgb, double gain) {
        for (int i = 0; i < rgb.length; i++) {
            rgb[i] *= gain;
        }

        return rgb;
    }

    private static int round(double value) {
        return Quantizer.round(value);
    }

    public static double[] round(double[] rgb, int base) {
        for (int i = 0; i < rgb.length; i++) {
            double q = round(rgb[i] * base);
            rgb[i] = q / base;
        }

        return rgb;
    }

    public static double[] round(double[] rgb, int base, Dither dither) {
        for (int i = 0; i < rgb.length; i++) {
            double q = dither.quantize(rgb[i] * base);
            rgb[i] = q / base;
        }

        return rgb;
    }

    public static double clip(double value) {
        return max(0.0, min(1.0, value));
    }
}
