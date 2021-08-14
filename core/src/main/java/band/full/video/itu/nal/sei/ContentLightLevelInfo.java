package band.full.video.itu.nal.sei;

import band.full.video.itu.nal.NalContext;
import band.full.video.itu.nal.Payload;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

/**
 * D.2.35 Content light level information SEI message syntax
 * <p>
 * <code>content_light_level_info()</code>
 * 
 * @author Igor Malinin
 */
@SuppressWarnings("rawtypes")
public class ContentLightLevelInfo implements Payload {
    public int max_content_light_level; // u(16)
    public int max_pic_average_light_level; // u(16)

    public ContentLightLevelInfo() {
    }

    public ContentLightLevelInfo(NalContext context, RbspReader in, int size) {
        if (size != size(context)) throw new IllegalArgumentException();
        read(context, in);
    }

    @Override
    public int size(NalContext context) {
        return 4;
    }

    @Override
    public void read(NalContext context, RbspReader in) {
        max_content_light_level = in.u16();
        max_pic_average_light_level = in.u16();
    }

    @Override
    public void write(NalContext context, RbspWriter out) {
        out.u16(max_content_light_level);
        out.u16(max_pic_average_light_level);
    }

    @Override
    public void print(NalContext context, RbspPrinter out) {
        out.u16("max_content_light_level", max_content_light_level);
        out.u16("max_pic_average_light_level", max_pic_average_light_level);
    }
}
