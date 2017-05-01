package band.full.testing.video.itu;

/**
 * Parameter values for the HDTV standards for production and international
 * programme exchange
 * 
 * @author Igor Malinin
 * @see <a href=
 *      "http://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.709-6-201506-I!!PDF-E.pdf">
 *      Recommendation ITU-R BT.709-6 (06/2015)</a>
 */
public interface BT709 {
    // FIXME wrong values!!!
    static double getY(double r, double g, double b) {
        return 0.2627 * r + 0.6780 * g + 0.0593 * b;
    }

    static double getCb(double y, double b) {
        return (b - y) / 1.8814;
    }

    static double getCr(double y, double r) {
        return (r - y) / 1.4746;
    }
}
