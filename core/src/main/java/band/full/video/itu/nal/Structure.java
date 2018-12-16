package band.full.video.itu.nal;

public interface Structure<C extends NalContext> {
    void read(C context, RbspReader in);

    void write(C context, RbspWriter out);

    void print(C context, RbspPrinter out);
}
