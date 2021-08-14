package band.full.video.itu.nal;

public interface Payload<C extends NalContext> extends Structure<C> {
    /** Number of bytes in the payload */
    int size(C context);

    @SuppressWarnings("rawtypes")
    public static class Bytes implements Payload {
        public byte[] bytes;

        public Bytes() {}

        public Bytes(RbspReader in) {
            read(null, in);
        }

        public Bytes(RbspReader in, int size) {
            bytes = in.readBytes(size);
        }

        @Override
        public int size(NalContext context) {
            return bytes.length;
        }

        @Override
        public void read(NalContext context, RbspReader in) {
            bytes = in.readTrailingBits();
        }

        @Override
        public void write(NalContext context, RbspWriter out) {
            out.writeBytes(bytes);
        }

        @Override
        public void print(NalContext context, RbspPrinter out) {
            out.printH("payload", bytes);
        }
    }
}
