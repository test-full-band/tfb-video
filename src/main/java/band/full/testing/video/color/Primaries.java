package band.full.testing.video.color;

/**
 * @author Igor Malinin
 * @see <a href=
 *      "http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html">
 *      RGB/XYZ Matrices</a>
 * @see <a href="http://www.poynton.com/PDFs/coloureq.pdf">Colour Space
 *      Conversions</a>
 */
public class Primaries {
    /**
     * @see <a href="https://www.w3.org/Graphics/Color/srgb">W3C sRGB</a>
     */
    public static final Primaries sRGB = new Primaries(
            new CIExy(0.640, 0.330),
            new CIExy(0.300, 0.600),
            new CIExy(0.150, 0.060),
            new CIExy(0.3127, 0.3290));

    public final CIExy red;
    public final CIExy green;
    public final CIExy blue;
    public final CIExy white;

    public final Matrix3x3 rgb2xyz;
    public final Matrix3x3 xyz2rgb;

    public Primaries(CIExy red, CIExy green, CIExy blue, CIExy white) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.white = white;

        rgb2xyz = createRGBtoXYZ();
        xyz2rgb = rgb2xyz.invert();
    }

    public Matrix3x3 getRGBtoXYZ() {
        return rgb2xyz;
    }

    public Matrix3x3 getXYZtoRGB() {
        return xyz2rgb;
    }

    private Matrix3x3 createRGBtoXYZ() {
        Matrix3x3 m = new Matrix3x3(
                red.x / red.y, green.x / green.y, blue.x / blue.y,
                1.0, 1.0, 1.0,
                (1.0 - red.x - red.y) / red.y,
                (1.0 - green.x - green.y) / green.y,
                (1.0 - blue.x - blue.y) / blue.y);

        Matrix3x3 inverse = m.invert();

        double[] w = {white.x / white.y, 1.0,
            (1 - white.x - white.y) / white.y};

        return m.multiplyColumns(inverse.multiply(w));
    }

    @Override
    public String toString() {
        return "Primaries[R" + red + " G" + green + " B" + blue
                + " W" + white + "]";
    }
}
