package band.full.core;

import static band.full.core.Quantizer.round;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

import java.util.Random;

/**
 * @author Igor Malinin
 */
public abstract class Dither implements Quantizer {
    protected final Random PRNG = new Random();
    protected final int min, max;

    private Dither() {
        this(MIN_VALUE, MAX_VALUE);
    }

    private Dither(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static class RPDF extends Dither {
        public RPDF() {}

        public RPDF(int min, int max) {
            super(min, max);
        }

        @Override
        public int quantize(double value) {
            return round(value + PRNG.nextFloat() - 0.5);
        }
    }

    public static class TPDF extends Dither {
        private float state = PRNG.nextFloat();

        public TPDF() {}

        public TPDF(int min, int max) {
            super(min, max);
        }

        @Override
        public int quantize(double value) {
            var r = state;
            state = PRNG.nextFloat();
            return round(value + r - state);
        }
    }
}
