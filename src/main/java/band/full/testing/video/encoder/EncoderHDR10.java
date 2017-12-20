package band.full.testing.video.encoder;

import static band.full.testing.video.encoder.DecoderY4M.decode;
import static band.full.testing.video.encoder.EncoderParameters.HDR10;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public class EncoderHDR10 extends EncoderHEVC {
    private EncoderHDR10(String name, EncoderParameters parameters)
            throws IOException {
        super(name, parameters);
    }

    public static void encode(String name, Consumer<EncoderY4M> consumer) {
        encode(name, HDR10, consumer);
    }

    public static void encode(String name,
            Consumer<EncoderY4M> ec, Consumer<DecoderY4M> dc) {
        encode(name, HDR10, ec, dc);
    }

    public static void encode(String name, EncoderParameters parameters,
            Consumer<EncoderY4M> consumer) {
        try (EncoderHDR10 encoder = new EncoderHDR10(name, parameters)) {
            consumer.accept(encoder);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void encode(String name, EncoderParameters parameters,
            Consumer<EncoderY4M> ec, Consumer<DecoderY4M> dc) {
        encode(name, parameters, ec);
        decode(name, parameters, dc);
    }
}
