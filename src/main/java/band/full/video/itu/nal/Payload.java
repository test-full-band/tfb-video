package band.full.video.itu.nal;

import static band.full.core.ArrayMath.toHexString;

import java.io.PrintStream;

public interface Payload extends Structure {
    int size();

    public static class Bytes implements Payload {
        public byte[] bytes;

        public Bytes() {}

        public Bytes(RbspReader reader) {
            read(reader);
        }

        public Bytes(RbspReader reader, int size) {
            bytes = reader.readBytes(size);
        }

        @Override
        public int size() {
            return bytes.length;
        }

        @Override
        public void read(RbspReader reader) {
            bytes = reader.readTrailingBits();
        }

        @Override
        public void write(RbspWriter writer) {
            writer.writeBytes(bytes);
        }

        @Override
        public void print(PrintStream ps) {
            ps.print("      payload: 0x");
            ps.println(toHexString(bytes));
        }
    }
}
