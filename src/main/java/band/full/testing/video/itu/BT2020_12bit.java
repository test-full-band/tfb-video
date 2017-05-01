package band.full.testing.video.itu;

/**
 * Parameter values for 12bit UHD
 * 
 * @author Igor Malinin
 * @see <a href=
 *      "https://www.itu.int/dms_pubrec/itu-r/rec/bt/R-REC-BT.2020-2-201510-I!!PDF-E.pdf">
 *      Rec. ITU-R BT.2020-2 (10/2015)</a>
 */
public interface BT2020_12bit extends BT2020 {
    short BLACK = 256;
    short YPEAK = 3760;

    short ACHROMATIC = 2048;

    short CMIN = 256;
    short CMAX = 3840;
}
