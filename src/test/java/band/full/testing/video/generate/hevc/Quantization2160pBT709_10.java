package band.full.testing.video.generate.hevc;

import static band.full.testing.video.core.Framerate.FPS_23_976;
import static band.full.testing.video.core.Resolution.STD_2160p;
import static band.full.testing.video.itu.BT709.BT709_10bit;
import static band.full.testing.video.itu.BT709.TRANSFER;
import static java.lang.String.format;

import band.full.testing.video.encoder.EncoderHEVC;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.QuantizationBase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quantization2160pBT709_10 extends QuantizationBase {
    private static final EncoderParameters UHD4K_BT709b10 =
            new EncoderParameters(STD_2160p, TRANSFER, BT709_10bit, FPS_23_976);

    @ParameterizedTest
    @ValueSource(ints = {64, 96})
    public void quantsNearBlack(int yCode) {
        quants("NearBlack", yCode); // 32
    }

    @ParameterizedTest
    @ValueSource(ints = {128, 160})
    public void quantsDarkGray(int yCode) {
        quants("DarkGray", yCode); // 32
    }

    @ParameterizedTest
    @ValueSource(ints = {192, 256})
    public void quantsGray(int yCode) {
        quants("Gray", yCode); // 64
    }

    @ParameterizedTest
    @ValueSource(ints = {320, 384})
    public void quantsLightGray(int yCode) {
        quants("LightGray", yCode); // 64
    }

    @ParameterizedTest
    @ValueSource(ints = {448, 512})
    public void quantsNearWhite(int yCode) {
        quants("NearWhite", yCode); // 64
    }

    @ParameterizedTest
    @ValueSource(ints = {576, 640})
    public void quantsBright(int yCode) {
        quants("Bright", yCode); // 64
    }

    @ParameterizedTest
    @ValueSource(ints = {704, 768})
    public void quantsBrighter(int yCode) {
        quants("Brighter", yCode); // 64
    }

    @ParameterizedTest
    @ValueSource(ints = {832, 876})
    public void quantsBrightest(int yCode) {
        quants("Brightest", yCode); // 44
    }

    private void quants(String name, int yCode) {
        String prefix = format(
                "HEVC/UHD4K/BT709_10/Quantization/QuantsBT709_10-Y%03d", yCode);

        EncoderHEVC.encode(prefix + "Cb-" + name, UHD4K_BT709b10,
                e -> quants(e, yCode, false),
                d -> verify(d, yCode, false));

        EncoderHEVC.encode(prefix + "Cr-" + name, UHD4K_BT709b10,
                e -> quants(e, yCode, true),
                d -> verify(d, yCode, true));
    }
}
