package band.full.core.color;

import static band.full.core.color.CIEXYZ.ILLUMINANT_D50;
import static band.full.core.color.CIEXYZ.ILLUMINANT_D65;

public class ChromaticAdaptation {
    public static final Matrix3x3 BRADFORD_MA = new Matrix3x3(
            0.8951, 0.2664, -0.1614,
            -0.7502, 1.7135, 0.0367,
            0.0389, -0.0685, 1.0296);

    public static final Matrix3x3 BRADFORD_MA_I = BRADFORD_MA.invert();

    public static final Matrix3x3 BRADFORD_D50_D65 =
            bradford(ILLUMINANT_D50, ILLUMINANT_D65);

    public static Matrix3x3 bradford(CIEXYZ src, CIEXYZ dst) {
        double[] s = BRADFORD_MA.multiply(src.array());
        double[] d = BRADFORD_MA.multiply(dst.array());

        Matrix3x3 m = new Matrix3x3(
                d[0] / s[0], 0.0, 0.0,
                0.0, d[1] / s[1], 0.0,
                0.0, 0.0, d[2] / s[2]);

        return BRADFORD_MA_I.multiply(m).multiply(BRADFORD_MA);
    }
}
