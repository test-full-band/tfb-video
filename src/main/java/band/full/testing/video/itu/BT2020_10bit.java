package band.full.testing.video.itu;

/**
 * Parameter values for 10bit UHD
 * 
 * @author Igor Malinin
 */
public interface BT2020_10bit extends BT2020 {
    short BLACK = 64;
    short YPEAK = 940;

    short ACHROMATIC = 512;

    short CMIN = 64;
    short CMAX = 960;
}
