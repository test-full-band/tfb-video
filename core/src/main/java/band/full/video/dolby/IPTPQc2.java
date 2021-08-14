package band.full.video.dolby;

import static band.full.video.dolby.VdrDmDataPayload.IPTPQ_YCCtoRGB_coef;
import static band.full.video.dolby.VdrDmDataPayload.IPTPQc2_RGBtoLMS_coef;
import static band.full.video.dolby.VdrDmDataPayload.getRGBtoLMS;
import static band.full.video.dolby.VdrDmDataPayload.getYCCtoRGB;
import static band.full.video.itu.ColorRange.FULL;
import static band.full.video.itu.ICtCp.LMStoRGB;
import static band.full.video.itu.ICtCp.RGBtoLMS;

import band.full.core.color.Matrix3x3;
import band.full.core.color.Primaries;
import band.full.video.itu.BT2020;
import band.full.video.itu.ColorMatrix;
import band.full.video.smpte.ST2084;

/**
 * Encoding/decoding of <em>RGB</em> values to/from Dolby Vision profile 5
 * <em>IPTPQc2</em> color space.
 * <p>
 * This color matrix has reshaping state so is not thread safe and cannot be
 * shared. Create an instance for each encode.
 *
 * @author Igor Malinin
 */
public final class IPTPQc2 extends ColorMatrix {
    public static final ST2084 PQ = new ST2084(2, "DVp5");

    public static final Primaries PRIMARIES =
            new Primaries(2, BT2020.PRIMARIES);

    public static final IPTPQc2 PQ10IPTc2 = new IPTPQc2(10);
    public static final IPTPQc2 PQ12IPTc2 = new IPTPQc2(12);

    private static final double[] RESHAPING = {1.0, 2.0, 2.0};

    /**
     * <pre>
     * [1.0,  0.09753,  0.20520]
     * [1.0, -0.11389,  0.13318]
     * [1.0,  0.03259, -0.67688]
     * </pre>
     */
    public static final Matrix3x3 IPTtoPQLMS = getYCCtoRGB(IPTPQ_YCCtoRGB_coef);

    private static final Matrix3x3 IPTtoPQLMS_RESHAPED =
            IPTtoPQLMS.multiplyColumns(RESHAPING);

    /**
     * <pre>
     * [0.40013,  0.39989,  0.19998]
     * [4.45534, -4.85146,  0.39612]
     * [0.80567,  0.35718, -1.16285]
     * </pre>
     */
    public static final Matrix3x3 PQLMStoIPT = IPTtoPQLMS.invert();

    private static final Matrix3x3 PQLMStoIPT_RESHAPED =
            IPTtoPQLMS_RESHAPED.invert();

    /**
     * <pre>
     * [ 1.04254, -0.02130, -0.02130]
     * [-0.02124,  1.04254, -0.02130]
     * [-0.02130, -0.02130,  1.04254]
     * </pre>
     */
    public static final Matrix3x3 CROSS_TALK_INVERSE =
            getRGBtoLMS(IPTPQc2_RGBtoLMS_coef);

    /**
     * <pre>
     * [0.9600, 0.0200, 0.0200]
     * [0.0200, 0.9600, 0.0200]
     * [0.0200, 0.0200, 0.9600]
     * </pre>
     */
    public static final Matrix3x3 CROSS_TALK = CROSS_TALK_INVERSE.invert();

    // TODO enable pivots and full polynomials
    // currently only first order polynomial scaling supported
    public final double[] reshaping;

    public IPTPQc2(int bitdepth) {
        this(bitdepth, 1.0, 2.0, 2.0);
    }

    public IPTPQc2(int bitdepth, double... reshaping) {
        super(2, PQ, PRIMARIES, bitdepth, FULL);
        this.reshaping = reshaping;
    }

    @Override
    public double[] fromRGB(double[] rgb, double[] ipt) {
        return fromLinearRGB(transfer.toLinear(rgb, ipt), ipt);
    }

    @Override
    public double[] fromLinearRGB(double[] rgb, double[] ipt) {
        RGBtoLMS.multiply(rgb, ipt); // TODO combine with cross talk
        CROSS_TALK.multiply(ipt, ipt);
        transfer.fromLinear(ipt, ipt);
        PQLMStoIPT_RESHAPED.multiply(ipt, ipt);
        return ipt;
    }

    @Override
    public double[] toRGB(double[] ipt, double[] rgb) {
        return transfer.fromLinear(toLinearRGB(ipt, rgb), rgb);
    }

    @Override
    public double[] toLinearRGB(double[] ipt, double[] rgb) {
        IPTtoPQLMS_RESHAPED.multiply(ipt, rgb);
        transfer.toLinear(rgb, rgb);
        CROSS_TALK_INVERSE.multiply(rgb, rgb);
        return LMStoRGB.multiply(rgb, rgb); // TODO combine with cross talk
    }

    public static void main(String[] args) {
        System.out.println(CROSS_TALK.toString());
    }
}
