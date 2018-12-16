package band.full.video.itu.nal;

public abstract class NalUnit<C extends NalContext> implements Structure<C> {
    /** true if there is additional 0x00 before start_code_prefix_one_3bytes */
    public boolean zero_byte;

    public abstract boolean isZeroByteRequired();

    public abstract String getTypeString();

    public abstract String getHeaderParamsString();
}
