package band.full.video.itu.h265;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class NALUnitParser {
    private final InputStream in;

    public NALUnitParser(InputStream in) throws IOException {
        this.in = in;
        findUnit(false);
    }

    public NALUnit read() throws IOException {
        int code = in.read();

        try {
            NALUnitType type = NALUnitType.get(code >> 1);
            findUnit(true);
            return new NALUnit(type);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("corrupted stream", e);
        }
    }

    private void findUnit(boolean skip) throws IOException {
        int n = 0;

        while (true) {
            int b = in.read();

            if (b < 0) throw new EOFException();

            if (b == 0) {
                ++n;
                continue;
            }

            if (b == 1 && n >= 2) return;

            if (!skip) throw new IOException("corrupted stream");

            n = 0;
        }
    }
}
