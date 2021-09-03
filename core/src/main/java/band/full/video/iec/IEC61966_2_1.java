package band.full.video.iec;

import static band.full.video.itu.ColorRange.FULL;

import band.full.core.color.Primaries;
import band.full.video.itu.BT709;
import band.full.video.itu.ColorMatrix;
import band.full.video.itu.TransferCharacteristics;
import band.full.video.itu.YCbCr;

/**
 * Multimedia systems and equipment - Colour measurement and management<br>
 * Part 2-1: Colour management - Default RGB colour space - <b>sRGB</b>
 *
 * @author Igor Malinin
 */
public class IEC61966_2_1 {
    /**
     * <strong>IEC 61966-2-1 sRGB</strong> or sYCC IEC 61966-2-4
     *
     * @see <a href="https://www.w3.org/Graphics/Color/srgb">W3C sRGB</a>
     */
    public static final Primaries PRIMARIES = BT709.PRIMARIES;

    /**
     * <strong>IEC 61966-2-1 sRGB</strong> or sYCC IEC 61966-2-4
     *
     * @see <a href="https://www.w3.org/Graphics/Color/srgb">W3C sRGB</a>
     */
    public static final TransferCharacteristics TRANSFER = new Gamma();

    public static final ColorMatrix sRGB =
            new YCbCr(0, TRANSFER, PRIMARIES, 8, FULL);
}
