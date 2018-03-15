package band.full.testing.video.generate.base;

import band.full.testing.video.color.CIELab;

import java.util.List;

/**
 * @see <a href=
 *      "http://xritephoto.com/ph_product_overview.aspx?ID=938&Action=Support&SupportID=5884">
 *      New color specifications for ColorChecker SG and Classic Charts</a>
 */
public class ColorChecker {
    /**
     * ColorChecker24 - November2014 edition and newer.
     * <p>
     * The data in this list is reported in CIE L* a* b* data for illuminant D50
     * and 2 degree observer.
     *
     * @see <a href=
     *      "http://xritephoto.com/documents/literature/EN/ColorChecker24_After_Nov2014.zip">
     *      Reference Data for ColorChecker Classic after November 2014</a>
     */
    public static final List<List<CIELab>> CLASSIC_24 = List.of(
            List.of( // A
                    new CIELab(37.54, 14.37, 14.92),
                    new CIELab(62.73, 35.83, 56.5),
                    new CIELab(28.37, 15.42, -49.8),
                    new CIELab(95.19, -1.03, 2.93)),
            List.of( // B
                    new CIELab(64.66, 19.27, 17.5),
                    new CIELab(39.43, 10.75, -45.17),
                    new CIELab(54.38, -39.72, 32.27),
                    new CIELab(81.29, -0.57, 0.44)),
            List.of( // C
                    new CIELab(49.32, -3.82, -22.54),
                    new CIELab(50.57, 48.64, 16.67),
                    new CIELab(42.43, 51.05, 28.62),
                    new CIELab(66.89, -0.75, -0.06)),
            List.of( // D
                    new CIELab(43.46, -12.74, 22.72),
                    new CIELab(30.1, 22.54, -20.87),
                    new CIELab(81.8, 2.67, 80.41),
                    new CIELab(50.76, -0.13, 0.14)),
            List.of( // E
                    new CIELab(54.94, 9.61, -24.79),
                    new CIELab(71.77, -24.13, 58.19),
                    new CIELab(50.63, 51.28, -14.12),
                    new CIELab(35.63, -0.46, -0.48)),
            List.of( // F
                    new CIELab(70.48, -32.26, -0.37),
                    new CIELab(71.51, 18.24, 67.37),
                    new CIELab(49.57, -29.71, -28.32),
                    new CIELab(20.64, 0.07, -0.46)));

    /**
     * ColorChecker24 - Before November2014 edition.
     * <p>
     * The data in this list is reported in CIE L* a* b* data for illuminant D50
     * and 2 degree observer.
     *
     * @see <a href=
     *      "http://xritephoto.com/documents/literature/EN/ColorChecker24_Before_Nov2014.zip">
     *      Reference Data for ColorChecker Classic before November 2014</a>
     */
    public static final List<List<CIELab>> CLASSIC_24_OLD = List.of(
            List.of( // A
                    new CIELab(37.986, 13.555, 14.059),
                    new CIELab(62.661, 36.067, 57.096),
                    new CIELab(28.778, 14.179, -50.297),
                    new CIELab(96.539, -0.425, 1.186)),
            List.of( // B
                    new CIELab(65.711, 18.13, 17.81),
                    new CIELab(40.02, 10.41, -45.964),
                    new CIELab(55.261, -38.342, 31.37),
                    new CIELab(81.257, -0.638, -0.335)),
            List.of( // C
                    new CIELab(49.927, -4.88, -21.905),
                    new CIELab(51.124, 48.239, 16.248),
                    new CIELab(42.101, 53.378, 28.19),
                    new CIELab(66.766, -0.734, -0.504)),
            List.of( // D
                    new CIELab(43.139, -13.095, 21.905),
                    new CIELab(30.325, 22.976, -21.587),
                    new CIELab(81.733, 4.039, 79.819),
                    new CIELab(50.867, -0.153, -0.27)),
            List.of( // E
                    new CIELab(55.112, 8.844, -25.399),
                    new CIELab(72.532, -23.709, 57.255),
                    new CIELab(51.935, 49.986, -14.574),
                    new CIELab(35.656, -0.421, -1.231)),
            List.of( // F
                    new CIELab(70.719, -33.397, -0.199),
                    new CIELab(71.941, 19.363, 67.857),
                    new CIELab(51.038, -28.631, -28.638),
                    new CIELab(20.461, -0.079, -0.973)));

    /**
     * ColorCheckerSG - November2014 edition and newer.
     * <p>
     * The data in this list is reported in CIE L* a* b* data for illuminant D50
     * and 2 degree observer.
     *
     * @see <a href=
     *      "http://xritephoto.com/documents/literature/EN/ColorCheckerSG_After_Nov2014.zip">
     *      Reference Data for ColorCecker SG after November 2014</a>
     */
    public static final List<List<CIELab>> DIGITAL_SG = List.of(
            List.of( // A
                    new CIELab(96.71, -0.62, 2.06),
                    new CIELab(8.05, 0.17, -0.69),
                    new CIELab(49.76, 0.11, 0.72),
                    new CIELab(96.72, -0.63, 2.06),
                    new CIELab(8.17, 0.15, -0.65),
                    new CIELab(49.68, 0.14, 0.74),
                    new CIELab(96.6, -0.62, 2.11),
                    new CIELab(7.99, 0.21, -0.75),
                    new CIELab(49.67, 0.15, 0.73),
                    new CIELab(96.51, -0.63, 2.11)),
            List.of( // B
                    new CIELab(49.7, 0.12, 0.68),
                    new CIELab(33.02, 52.0, -10.3),
                    new CIELab(61.4, 27.14, -18.42),
                    new CIELab(30.54, 50.39, -41.79),
                    new CIELab(49.56, -13.9, -49.65),
                    new CIELab(60.62, -29.91, -27.54),
                    new CIELab(20.13, -24.81, -7.5),
                    new CIELab(60.32, -40.29, -13.25),
                    new CIELab(19.62, 1.77, 11.99),
                    new CIELab(49.68, 0.15, 0.78)),
            List.of( // C
                    new CIELab(8.13, 0.15, -0.76),
                    new CIELab(19.65, 20.42, -18.82),
                    new CIELab(41.7, 18.9, -37.42),
                    new CIELab(20.25, 0.26, -36.44),
                    new CIELab(60.13, -17.88, -32.08),
                    new CIELab(19.75, -17.79, -22.37),
                    new CIELab(60.43, -5.12, -32.79),
                    new CIELab(50.46, -47.9, -11.56),
                    new CIELab(60.53, -40.75, 19.37),
                    new CIELab(8.09, 0.19, -0.69)),
            List.of( // D
                    new CIELab(96.79, -0.66, 1.99),
                    new CIELab(84.0, -1.7, -8.37),
                    new CIELab(85.48, 15.15, 0.79),
                    new CIELab(84.56, -19.74, -1.13),
                    new CIELab(85.26, 13.37, 7.95),
                    new CIELab(84.38, -11.97, 27.16),
                    new CIELab(62.35, 29.94, 36.89),
                    new CIELab(64.17, 21.34, 19.36),
                    new CIELab(50.48, -53.21, 12.65),
                    new CIELab(96.57, -0.64, 2.0)),
            List.of( // E
                    new CIELab(49.79, 0.13, 0.66),
                    new CIELab(32.77, 19.91, 22.33),
                    new CIELab(62.28, 37.56, 68.87),
                    new CIELab(19.92, 25.07, -61.05),
                    new CIELab(96.78, -0.66, 2.01),
                    new CIELab(8.07, 0.12, -0.93),
                    new CIELab(77.37, 20.28, 24.27),
                    new CIELab(74.01, 29.0, 25.8),
                    new CIELab(20.33, -23.98, 7.2),
                    new CIELab(49.72, 0.14, 0.71)),
            List.of( // F
                    new CIELab(8.09, 0.19, -0.69),
                    new CIELab(63.88, 20.34, 19.93),
                    new CIELab(35.28, 12.93, -51.17),
                    new CIELab(52.75, -44.12, 38.68),
                    new CIELab(79.65, -0.08, 0.62),
                    new CIELab(30.32, -0.1, 0.22),
                    new CIELab(63.46, 13.53, 26.37),
                    new CIELab(64.44, 14.31, 17.63),
                    new CIELab(60.05, -44.0, 7.27),
                    new CIELab(8.08, 0.18, -0.78)),
            List.of( // G
                    new CIELab(96.7, -0.66, 1.97),
                    new CIELab(45.84, -3.74, -25.32),
                    new CIELab(47.6, 53.66, 22.15),
                    new CIELab(36.88, 65.72, 41.63),
                    new CIELab(65.22, -0.27, 0.16),
                    new CIELab(39.55, -0.37, -0.09),
                    new CIELab(44.49, 16.06, 26.79),
                    new CIELab(64.97, 15.89, 16.79),
                    new CIELab(60.77, -30.19, 40.76),
                    new CIELab(96.71, -0.64, 2.01)),
            List.of( // H
                    new CIELab(49.74, 0.14, 0.68),
                    new CIELab(38.29, -17.44, 30.22),
                    new CIELab(20.76, 31.66, -28.04),
                    new CIELab(81.43, 2.41, 88.98),
                    new CIELab(49.71, 0.12, 0.69),
                    new CIELab(60.04, 0.09, 0.05),
                    new CIELab(67.6, 14.47, 17.12),
                    new CIELab(64.75, 17.3, 18.88),
                    new CIELab(51.26, -50.65, 43.8),
                    new CIELab(49.76, 0.14, 0.71)),
            List.of( // I
                    new CIELab(8.1, 0.19, -0.93),
                    new CIELab(51.36, 9.52, -26.98),
                    new CIELab(71.62, -24.77, 64.1),
                    new CIELab(48.75, 57.24, -14.45),
                    new CIELab(34.85, -0.21, 0.73),
                    new CIELab(75.36, 0.35, 0.26),
                    new CIELab(45.14, 26.38, 41.24),
                    new CIELab(36.2, 16.7, 27.06),
                    new CIELab(61.65, -54.33, 46.18),
                    new CIELab(7.97, 0.14, -0.8)),
            List.of( // J
                    new CIELab(96.69, -0.67, 1.95),
                    new CIELab(68.71, -35.41, -1.11),
                    new CIELab(70.39, 19.37, 79.73),
                    new CIELab(47.42, -30.91, -32.27),
                    new CIELab(15.43, -0.24, -0.25),
                    new CIELab(88.85, -0.59, 0.25),
                    new CIELab(64.0, 25.09, 27.14),
                    new CIELab(66.65, 22.21, 28.81),
                    new CIELab(62.05, 16.45, 51.74),
                    new CIELab(96.71, -0.64, 2.02)),
            List.of( // K
                    new CIELab(49.72, 0.12, 0.64),
                    new CIELab(85.68, 10.75, 18.39),
                    new CIELab(89.35, -16.38, 6.41),
                    new CIELab(84.59, 5.21, -5.87),
                    new CIELab(83.63, -12.47, -8.89),
                    new CIELab(70.6, -0.24, 0.07),
                    new CIELab(45.14, -0.04, 0.86),
                    new CIELab(20.33, 0.4, -0.21),
                    new CIELab(62.33, -14.54, 54.58),
                    new CIELab(49.74, 0.14, 0.69)),
            List.of( // L
                    new CIELab(8.08, 0.13, -0.81),
                    new CIELab(23.03, 33.95, 8.88),
                    new CIELab(44.35, 67.94, 50.62),
                    new CIELab(60.91, 36.55, 4.15),
                    new CIELab(62.2, 37.45, 18.18),
                    new CIELab(63.33, 51.3, 81.88),
                    new CIELab(73.74, -11.45, 85.07),
                    new CIELab(62.35, 1.96, 57.52),
                    new CIELab(72.77, -29.09, 71.26),
                    new CIELab(8.13, 0.15, -0.86)),
            List.of( // M
                    new CIELab(49.71, 0.12, 0.62),
                    new CIELab(42.52, 63.55, 11.43),
                    new CIELab(18.09, 32.61, -5.9),
                    new CIELab(40.66, 65.54, 31.98),
                    new CIELab(53.13, 68.44, 49.57),
                    new CIELab(82.08, 23.39, 87.24),
                    new CIELab(82.5, 5.29, 96.68),
                    new CIELab(71.9, -17.32, 77.72),
                    new CIELab(21.95, 13.41, 16.36),
                    new CIELab(49.74, 0.12, 0.69)),
            List.of( // N
                    new CIELab(96.79, -0.67, 1.97),
                    new CIELab(49.78, 0.12, 0.65),
                    new CIELab(8.23, 0.18, -0.82),
                    new CIELab(96.73, -0.67, 1.99),
                    new CIELab(49.8, 0.11, 0.67),
                    new CIELab(8.18, 0.15, -0.84),
                    new CIELab(96.73, -0.65, 2.01),
                    new CIELab(49.75, 0.13, 0.67),
                    new CIELab(8.11, 0.15, -0.9),
                    new CIELab(96.55, -0.64, 2.02)));

    /**
     * ColorCheckerSG - Before November2014 edition.
     * <p>
     * The data in this list is reported in CIE L* a* b* data for illuminant D50
     * and 2 degree observer.
     *
     * @see <a href=
     *      "http://xritephoto.com/documents/literature/EN/ColorCheckerSG_Before_Nov2014.zip">
     *      Reference Data for ColorChecker SG before November 2014</a>
     */
    public static final List<List<CIELab>> DIGITAL_SG_OLD = List.of(
            List.of( // A
                    new CIELab(96.55, -0.91, 0.57),
                    new CIELab(6.43, -0.06, -0.41),
                    new CIELab(49.7, -0.18, 0.03),
                    new CIELab(96.5, -0.89, 0.59),
                    new CIELab(6.5, -0.06, -0.44),
                    new CIELab(49.66, -0.2, 0.01),
                    new CIELab(96.52, -0.91, 0.58),
                    new CIELab(6.49, -0.02, -0.28),
                    new CIELab(49.72, -0.2, 0.04),
                    new CIELab(96.43, -0.91, 0.67)),
            List.of( // B
                    new CIELab(49.72, -0.19, 0.02),
                    new CIELab(32.6, 51.58, -10.85),
                    new CIELab(60.75, 26.22, -18.69),
                    new CIELab(28.69, 48.28, -39),
                    new CIELab(49.38, -15.43, -48.48),
                    new CIELab(60.63, -30.77, -26.23),
                    new CIELab(19.29, -26.37, -6.15),
                    new CIELab(60.15, -41.77, -12.6),
                    new CIELab(21.42, 1.67, 8.79),
                    new CIELab(49.69, -0.2, 0.01)),
            List.of( // C
                    new CIELab(6.5, -0.03, -0.67),
                    new CIELab(21.82, 17.33, -18.35),
                    new CIELab(41.53, 18.48, -37.26),
                    new CIELab(19.99, -0.16, -36.29),
                    new CIELab(60.16, -18.45, -31.42),
                    new CIELab(19.94, -17.92, -20.96),
                    new CIELab(60.68, -6.05, -32.81),
                    new CIELab(50.81, -49.8, -9.63),
                    new CIELab(60.65, -39.77, 20.76),
                    new CIELab(6.53, -0.03, -0.43)),
            List.of( // D
                    new CIELab(96.56, -0.91, 0.59),
                    new CIELab(84.19, -1.95, -8.23),
                    new CIELab(84.75, 14.55, 0.23),
                    new CIELab(84.87, -19.07, -0.82),
                    new CIELab(85.15, 13.48, 6.82),
                    new CIELab(84.17, -10.45, 26.78),
                    new CIELab(61.74, 31.06, 36.42),
                    new CIELab(64.37, 20.82, 18.92),
                    new CIELab(50.4, -53.22, 14.62),
                    new CIELab(96.51, -0.89, 0.65)),
            List.of( // E
                    new CIELab(49.74, -0.19, 0.03),
                    new CIELab(31.91, 18.62, 21.99),
                    new CIELab(60.74, 38.66, 70.97),
                    new CIELab(19.35, 22.23, -58.86),
                    new CIELab(96.52, -0.91, 0.62),
                    new CIELab(6.66, 0.0, -0.3),
                    new CIELab(76.51, 20.81, 22.72),
                    new CIELab(72.79, 29.15, 24.18),
                    new CIELab(22.33, -20.7, 5.75),
                    new CIELab(49.7, -0.19, 0.01)),
            List.of( // F
                    new CIELab(6.53, -0.05, -0.61),
                    new CIELab(63.42, 20.19, 19.22),
                    new CIELab(34.94, 11.64, -50.7),
                    new CIELab(52.03, -44.15, 39.04),
                    new CIELab(79.43, 0.29, -0.17),
                    new CIELab(30.67, -0.14, -0.53),
                    new CIELab(63.6, 14.44, 26.07),
                    new CIELab(64.37, 14.5, 17.05),
                    new CIELab(60.01, -44.33, 8.49),
                    new CIELab(6.63, -0.01, -0.47)),
            List.of( // G
                    new CIELab(96.56, -0.93, 0.59),
                    new CIELab(46.37, -5.09, -24.46),
                    new CIELab(47.08, 52.97, 20.49),
                    new CIELab(36.04, 64.92, 38.51),
                    new CIELab(65.05, 0.0, -0.32),
                    new CIELab(40.14, -0.19, -0.38),
                    new CIELab(43.77, 16.46, 27.12),
                    new CIELab(64.39, 17, 16.59),
                    new CIELab(60.79, -29.74, 41.5),
                    new CIELab(96.48, -0.89, 0.64)),
            List.of( // H
                    new CIELab(49.75, -0.21, 0.01),
                    new CIELab(38.18, -16.99, 30.87),
                    new CIELab(21.31, 29.14, -27.51),
                    new CIELab(80.57, 3.85, 89.61),
                    new CIELab(49.71, -0.2, 0.03),
                    new CIELab(60.27, 0.08, -0.41),
                    new CIELab(67.34, 14.45, 16.9),
                    new CIELab(64.69, 16.95, 18.57),
                    new CIELab(51.12, -49.31, 44.41),
                    new CIELab(49.7, -0.2, 0.02)),
            List.of( // I
                    new CIELab(6.67, -0.05, -0.64),
                    new CIELab(51.56, 9.16, -26.88),
                    new CIELab(70.83, -24.26, 64.77),
                    new CIELab(48.06, 55.33, -15.61),
                    new CIELab(35.26, -0.09, -0.24),
                    new CIELab(75.16, 0.25, -0.2),
                    new CIELab(44.54, 26.27, 38.93),
                    new CIELab(35.91, 16.59, 26.46),
                    new CIELab(61.49, -52.73, 47.3),
                    new CIELab(6.59, -0.05, -0.5)),
            List.of( // J
                    new CIELab(96.58, -0.9, 0.61),
                    new CIELab(68.93, -34.58, -0.34),
                    new CIELab(69.65, 20.09, 78.57),
                    new CIELab(47.79, -33.18, -30.21),
                    new CIELab(15.94, -0.42, -1.2),
                    new CIELab(89.02, -0.36, -0.48),
                    new CIELab(63.43, 25.44, 26.25),
                    new CIELab(65.75, 22.06, 27.82),
                    new CIELab(61.47, 17.1, 50.72),
                    new CIELab(96.53, -0.89, 0.66)),
            List.of( // K
                    new CIELab(49.79, -0.2, 0.03),
                    new CIELab(85.17, 10.89, 17.26),
                    new CIELab(89.74, -16.52, 6.19),
                    new CIELab(84.55, 5.07, -6.12),
                    new CIELab(84.02, -13.87, -8.72),
                    new CIELab(70.76, 0.07, -0.35),
                    new CIELab(45.59, -0.05, 0.23),
                    new CIELab(20.3, 0.07, -0.32),
                    new CIELab(61.79, -13.41, 55.42),
                    new CIELab(49.72, -0.19, 0.02)),
            List.of( // L
                    new CIELab(6.77, -0.05, -0.44),
                    new CIELab(21.85, 34.37, 7.83),
                    new CIELab(42.66, 67.43, 48.42),
                    new CIELab(60.33, 36.56, 3.56),
                    new CIELab(61.22, 36.61, 17.32),
                    new CIELab(62.07, 52.8, 77.14),
                    new CIELab(72.42, -9.82, 89.66),
                    new CIELab(62.03, 3.53, 57.01),
                    new CIELab(71.95, -27.34, 73.69),
                    new CIELab(6.59, -0.04, -0.45)),
            List.of( // M
                    new CIELab(49.77, -0.19, 0.04),
                    new CIELab(41.84, 62.05, 10.01),
                    new CIELab(19.78, 29.16, -7.85),
                    new CIELab(39.56, 65.98, 33.71),
                    new CIELab(52.39, 68.33, 47.84),
                    new CIELab(81.23, 24.12, 87.51),
                    new CIELab(81.8, 6.78, 95.75),
                    new CIELab(71.72, -16.23, 76.28),
                    new CIELab(20.31, 14.45, 16.74),
                    new CIELab(49.68, -0.19, 0.05)),
            List.of( // N
                    new CIELab(96.48, -0.88, 0.68),
                    new CIELab(49.69, -0.18, 0.03),
                    new CIELab(6.39, -0.04, -0.33),
                    new CIELab(96.54, -0.9, 0.67),
                    new CIELab(49.72, -0.18, 0.05),
                    new CIELab(6.49, -0.03, -0.41),
                    new CIELab(96.51, -0.9, 0.69),
                    new CIELab(49.7, -0.19, 0.07),
                    new CIELab(6.47, 0.0, -0.38),
                    new CIELab(96.46, -0.89, 0.71)));
}
