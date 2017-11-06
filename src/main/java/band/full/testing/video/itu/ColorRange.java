package band.full.testing.video.itu;

import static java.util.Locale.ENGLISH;

public enum ColorRange {
    FULL, LIMITED;

    private final String name;

    private ColorRange() {
        name = name().toLowerCase(ENGLISH);
    }

    @Override
    public String toString() {
        return name;
    }
}
