package band.full.video.dolby;

import static band.full.video.dolby.IPTPQc2.IPTtoPQLMS;
import static band.full.video.dolby.IPTPQc2.PQ12IPTc2;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestIPTPQc2 {
    private final IPTPQc2 cm = PQ12IPTc2;

    @Test
    public void valuesITPtoPQLMS() {
        assertAll(
                () -> assertEquals(1.0, IPTtoPQLMS.get(0, 0)),
                () -> assertEquals(0.0976, IPTtoPQLMS.get(0, 1), 1e-4),
                () -> assertEquals(0.2052, IPTtoPQLMS.get(0, 2), 5e-5),
                () -> assertEquals(1.0, IPTtoPQLMS.get(1, 0)),
                () -> assertEquals(-0.1139, IPTtoPQLMS.get(1, 1), 5e-5),
                () -> assertEquals(0.1332, IPTtoPQLMS.get(1, 2), 5e-5),
                () -> assertEquals(1.0, IPTtoPQLMS.get(2, 0)),
                () -> assertEquals(0.0326, IPTtoPQLMS.get(2, 1), 5e-5),
                () -> assertEquals(-0.6769, IPTtoPQLMS.get(2, 2), 5e-5));
    }

    @Test
    public void valuesFull() {
        assertAll(
                () -> assertEquals(0, cm.VMIN),
                () -> assertEquals(4095, cm.VMAX),
                () -> assertEquals(0, cm.YMIN),
                () -> assertEquals(4095, cm.YMAX),
                () -> assertEquals(1, cm.CMIN),
                () -> assertEquals(4095, cm.CMAX),
                () -> assertEquals(2048, cm.ACHROMATIC));
    }

    @Test
    public void fromLumaCode() {
        assertAll(
                () -> assertEquals(0.0, cm.fromLumaCode(cm.YMIN)),
                () -> assertEquals(1.0, cm.fromLumaCode(cm.YMAX)));
    }

    @Test
    public void fromChromaCode() {
        assertAll(
                () -> assertEquals(-0.5, cm.fromChromaCode(cm.CMIN)),
                () -> assertEquals(0.5, cm.fromChromaCode(cm.CMAX)),
                () -> assertEquals(0.0, cm.fromChromaCode(cm.ACHROMATIC)));
    }

    @Test
    public void toLumaCode() {
        assertAll(
                () -> assertEquals(cm.YMIN, cm.toLumaCode(0.0)),
                () -> assertEquals(cm.YMAX, cm.toLumaCode(1.0)));
    }

    @Test
    public void toChromaCode() {
        assertAll(
                () -> assertEquals(cm.CMIN, cm.toChromaCode(-0.5)),
                () -> assertEquals(cm.CMAX, cm.toChromaCode(0.5)),
                () -> assertEquals(cm.ACHROMATIC, cm.toChromaCode(0.0)));
    }

    @Test
    public void black() {
        double[] expected = {0.0, 0.0, 0.0};
        double[] black = {0.0, 0.0, 0.0};

        assertAll(
                () -> assertArrayEquals(expected, cm.toRGB(black, black)),
                () -> assertArrayEquals(expected, cm.toLinearRGB(black, black)),
                () -> assertArrayEquals(expected, cm.fromRGB(black, black)),
                () -> assertArrayEquals(expected,
                        cm.fromLinearRGB(black, black)));
    }

    @Test
    public void gray() {
        double[] tmp = new double[3];
        double[] rgb = {0.5, 0.5, 0.5};
        double[] itp = {0.5, 0.0, 0.0};

        assertAll(
                () -> assertColorEquals(0.5, 0.5, 0.5, cm.toRGB(itp, tmp)),
                () -> assertColorEquals(0.5, 0.0, 0.0, cm.fromRGB(rgb, tmp)));
    }

    @Test
    public void white() {
        double[] tmp = new double[3];
        double[] rgb = {1.0, 1.0, 1.0};
        double[] itp = {1.0, 0.0, 0.0};

        assertAll(
                () -> assertColorEquals(1.0, 1.0, 1.0, cm.toRGB(itp, tmp)),
                () -> assertColorEquals(1.0, 1.0, 1.0,
                        cm.toLinearRGB(itp, tmp)),
                () -> assertColorEquals(1.0, 0.0, 0.0, cm.fromRGB(rgb, tmp)),
                () -> assertColorEquals(1.0, 0.0, 0.0,
                        cm.fromLinearRGB(rgb, tmp)));
    }

    private void assertColorEquals(double c0, double c1, double c2,
            double[] actual) {
        assertAll(
                () -> assertEquals(c0, actual[0], 7e-5),
                () -> assertEquals(c1, actual[1], 7e-5),
                () -> assertEquals(c2, actual[2], 7e-5));
    }

    @Test
    public void red() {
        assertAll(
                () -> symmetry(0.5, 0.0, 0.0),
                () -> symmetry(1.0, 0.0, 0.0));
    }

    @Test
    public void green() {
        assertAll(
                () -> symmetry(0.0, 0.5, 0.0),
                () -> symmetry(0.0, 1.0, 0.0));
    }

    @Test
    public void blue() {
        assertAll(
                () -> symmetry(0.0, 0.0, 0.5),
                () -> symmetry(0.0, 0.0, 1.0));
    }

    private void symmetry(double r, double g, double b) {
        assertAll(
                () -> symmetryE(r, g, b),
                () -> symmetryO(r, g, b));
    }

    private void symmetryE(double r, double g, double b) {
        double[] rgb = {r, g, b};

        cm.fromRGB(rgb, rgb);
        cm.toRGB(rgb, rgb);

        assertAll(
                () -> assertEquals(r, rgb[0], 6e-6),
                () -> assertEquals(g, rgb[1], 6e-6),
                () -> assertEquals(b, rgb[2], 6e-6));
    }

    private void symmetryO(double r, double g, double b) {
        double[] rgb = {r, g, b};

        cm.fromLinearRGB(rgb, rgb);
        cm.toLinearRGB(rgb, rgb);

        assertAll(
                () -> assertEquals(r, rgb[0], 5e-13),
                () -> assertEquals(g, rgb[1], 5e-13),
                () -> assertEquals(b, rgb[2], 5e-13));
    }
}
