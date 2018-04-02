package band.full.core.color;

/**
 * Parameter values for the HDTV standards for production and international
 * programme exchange
 *
 * @author Igor Malinin
 * @see SMPTE EG 432-1:2010
 * @see SMPTE RP 431-2:2011
 * @see <a href= "https://en.wikipedia.org/wiki/DCI-P3">Wikipedia</a>
 */
public class DCI_P3 {
    public static final CIExy CIE_R = new CIExy(0.680, 0.320);
    public static final CIExy CIE_G = new CIExy(0.265, 0.690);
    public static final CIExy CIE_B = new CIExy(0.150, 0.060);
    /** Theater */
    public static final CIExy CIE_W = new CIExy(0.314, 0.351);
    public static final CIExy CIE_D65 = new CIExy(0.3127, 0.3290);

    /**
     * <p>
     * ITU-T colour_primaries = 11
     * <ul>
     * <li>SMPTE RP 431-2 (2011)
     * </ul>
     */
    public static Primaries PRIMARIES_THEATER =
            new Primaries(11, CIE_R, CIE_G, CIE_B, CIE_W);

    /**
     * <p>
     * ITU-T colour_primaries = 12
     * <ul>
     * <li>SMPTE EG 432-1 (2010)
     * </ul>
     */
    public static Primaries PRIMARIES_D65 =
            new Primaries(12, CIE_R, CIE_G, CIE_B, CIE_D65);
}
