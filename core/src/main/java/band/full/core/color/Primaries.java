package band.full.core.color;

import static java.lang.String.format;

/**
 * Color Primaries
 *
 * @author Igor Malinin
 * @see <a href=
 *      "http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html">
 *      RGB/XYZ Matrices</a>
 * @see <a href="http://www.poynton.com/PDFs/coloureq.pdf">Colour Space
 *      Conversions</a>
 */
public class Primaries {
    /**
     * <ul>
     * ITU-T colour_primaries = 1
     * <li>Rec. ITU-R BT.709-6
     * <li>Rec. ITU-R BT.1361-0 conventional colour gamut system <br>
     * and extended colour gamut system (historical)
     * <li><strong>IEC 61966-2-1 sRGB</strong> or sYCC IEC 61966-2-4
     * <li>Annex B of SMPTE RP 177 (1993)
     * </ul>
     *
     * @see <a href="https://www.w3.org/Graphics/Color/srgb">W3C sRGB</a>
     */
    public static final Primaries sRGB = new Primaries(1,
            new CIExy(0.640, 0.330),
            new CIExy(0.300, 0.600),
            new CIExy(0.150, 0.060),
            new CIExy(0.3127, 0.3290));

    public final int code;
    public final CIExy red;
    public final CIExy green;
    public final CIExy blue;
    public final CIExy white;

    public final Matrix3x3 RGBtoXYZ;
    public final Matrix3x3 XYZtoRGB;

    public Primaries(int code, Primaries p) {
        this(code, p.red, p.green, p.blue, p.white);
    }

    public Primaries(int code,
            CIExy red, CIExy green, CIExy blue, CIExy white) {
        this.code = code;

        this.red = red;
        this.green = green;
        this.blue = blue;
        this.white = white;

        RGBtoXYZ = createRGBtoXYZ();
        XYZtoRGB = RGBtoXYZ.invert();
    }

    private Matrix3x3 createRGBtoXYZ() {
        Matrix3x3 m = new Matrix3x3(
                red.x() / red.y(), green.x() / green.y(), blue.x() / blue.y(),
                1.0, 1.0, 1.0,
                (1.0 - red.x() - red.y()) / red.y(),
                (1.0 - green.x() - green.y()) / green.y(),
                (1.0 - blue.x() - blue.y()) / blue.y());

        Matrix3x3 inverse = m.invert();

        double[] w = {white.x() / white.y(), 1.0,
            (1.0 - white.x() - white.y()) / white.y()};

        return m.multiplyColumns(inverse.multiply(w));
    }

    @Override
    public String toString() {
        return format("Primaries[R%s G%s B%s W%s]", red, green, blue, white);
    }
}
