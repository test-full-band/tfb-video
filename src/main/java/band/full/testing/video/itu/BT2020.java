package band.full.testing.video.itu;

/**
 * Parameter values for ultra-high definition television systems for production
 * and international programme exchange
 * 
 * @author Igor Malinin
 * @see <a href=
 *      "https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.2020-2-201510-I!!PDF-E.pdf">
 *      Rec. ITU-R BT.2020-2 (10/2015)</a>
 */
public interface BT2020 {
    /** Values are in 0..1 range */
    static double getY(double r, double g, double b) {
        return 0.2627 * r + 0.6780 * g + 0.0593 * b;
    }

    /** YB values are in 0..1 range, Cb value is in -0.5..+0.5 range */
    static double getCb(double y, double b) {
        return (b - y) / 1.8814;
    }

    /** YR values are in 0..1 range, Cr value is in -0.5..+0.5 range */
    static double getCr(double y, double r) {
        return (r - y) / 1.4746;
    }
}
