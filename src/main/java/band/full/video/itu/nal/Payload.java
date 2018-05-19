package band.full.video.itu.nal;

import static band.full.core.ArrayMath.toHexString;

import java.io.PrintStream;

public interface Payload<C extends NalContext> extends Structure<C> {
    int size(C context);

    @SuppressWarnings("rawtypes")
    public static class Bytes implements Payload {
        public byte[] bytes;

        public Bytes() {}

        public Bytes(RbspReader reader) {
            read(null, reader);
        }

        public Bytes(RbspReader reader, int size) {
            bytes = reader.readBytes(size);
        }

        @Override
        public int size(NalContext context) {
            return bytes.length;
        }

        @Override
        public void read(NalContext context, RbspReader reader) {
            bytes = reader.readTrailingBits();
        }

        @Override
        public void write(NalContext context, RbspWriter writer) {
            writer.writeBytes(bytes);
        }

        @Override
        public void print(NalContext context, PrintStream ps) {
            ps.print("      payload: 0x");
            ps.println(toHexString(bytes));
        }
    }
}
