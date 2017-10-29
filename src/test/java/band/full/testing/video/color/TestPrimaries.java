package band.full.testing.video.color;

import static band.full.testing.video.color.Primaries.sRGB;
import static org.junit.jupiter.api.Assertions.assertEquals;

import band.full.testing.video.itu.BT2020;

import org.junit.jupiter.api.Test;

/**
 * @author Igor Malinin
 */
public class TestPrimaries {
    // The expected values in XYZ/sRGB conversion matrices does not fully match
    // any of the published pre-computed matrices, but close to less than 1%, a
    // reason for the difference is unknown, matrices at different sources also
    // tend to differ

    @Test
    public void checkRGBtoXYZ() {
        Matrix3x3 RGBtoXYZ = sRGB.getRGBtoXYZ();

        assertEquals(0.4124, RGBtoXYZ.get(0, 0), 1e-4);
        assertEquals(0.3576, RGBtoXYZ.get(0, 1), 1e-4);
        assertEquals(0.1805, RGBtoXYZ.get(0, 2), 1e-4);

        assertEquals(0.2126, RGBtoXYZ.get(1, 0), 1e-4);
        assertEquals(0.7152, RGBtoXYZ.get(1, 1), 1e-4);
        assertEquals(0.0722, RGBtoXYZ.get(1, 2), 1e-4);

        assertEquals(0.0193, RGBtoXYZ.get(2, 0), 1e-4);
        assertEquals(0.1192, RGBtoXYZ.get(2, 1), 1e-4);
        assertEquals(0.9505, RGBtoXYZ.get(2, 2), 1e-4);
    }

    @Test
    public void checkXYZtoRGB() {
        Matrix3x3 XYZtoRGB = sRGB.getXYZtoRGB();

        assertEquals(3.241, XYZtoRGB.get(0, 0), 1e-4);
        assertEquals(-1.54, XYZtoRGB.get(0, 1), 1e-2);
        assertEquals(-0.4986, XYZtoRGB.get(0, 2), 1e-4);

        assertEquals(-0.969, XYZtoRGB.get(1, 0), 1e-3);
        assertEquals(1.876, XYZtoRGB.get(1, 1), 1e-4);
        assertEquals(0.0415, XYZtoRGB.get(1, 2), 1e-4);

        assertEquals(0.0557, XYZtoRGB.get(2, 0), 1e-4);
        assertEquals(-0.2040, XYZtoRGB.get(2, 1), 1e-4);
        assertEquals(1.057, XYZtoRGB.get(2, 2), 1e-4);
    }

    @Test
    public void checkBT2020toXYZ() {
        Matrix3x3 RGBtoXYZ = BT2020.PRIMARIES.getRGBtoXYZ();

        assertEquals(0.2627, RGBtoXYZ.get(1, 0), 1e-4);
        assertEquals(0.6780, RGBtoXYZ.get(1, 1), 1e-4);
        assertEquals(0.0593, RGBtoXYZ.get(1, 2), 1e-4);
    }
}
