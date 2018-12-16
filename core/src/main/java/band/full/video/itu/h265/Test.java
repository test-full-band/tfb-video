package band.full.video.itu.h265;

import static band.full.core.ArrayMath.fromHexString;
import static band.full.video.smpte.ST2084.PQ;
import static java.lang.System.out;

import band.full.video.dolby.RpuDataMapping;
import band.full.video.dolby.RpuDataNLQ;
import band.full.video.dolby.RpuHeader;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;

public class Test {
    public static void main(String[] args) {
        RbspPrinter p = new RbspPrinter(out);

        p.ue("ue(0)", 0);
        out.println();

        p.ue("ue(5)", 5); // L1 - ContentRange
        p.printB("L1", 8, 1);
        out.println();

        p.ue("ue(11)", 11); // L2 - TrimPass
        p.printB("L1", 8, 2);
        out.println();

        p.ue("ue(7)", 7); // L5 - ActiveArea
        p.printB("L1", 8, 5);
        out.println();

        p.ue("ue(3)", 3);
        p.ue("ue(9)", 9);
        out.println();

        out.println(PQ.toLinear(7d / 4095) * 10000);
        out.println(PQ.toLinear(3079d / 4095) * 10000);

        byte[] buf = fromHexString(
                "0809004061B6506E700003054573E50040097801FFC00FFF");

        var rpuReader = new RbspReader(buf, 0, buf.length);

        RpuHeader h = new RpuHeader();
        h.read(null, rpuReader);

        out.println("+ RPU Header");
        h.print(null,
                new RbspPrinter(out).enter().enter());

        out.println("  offset: " + (buf.length - (rpuReader.available() >> 3)));
        if (rpuReader.available() != 0) throw new IllegalStateException();

        buf = fromHexString("A8058714A59E840A8BF5682A019D6880"
                + "52F781AB1CF30A81DE6A403CDC4A88FC"
                + "8AA0FDCA76D8F09D4371BA86C713CA18"
                + "2A7BCBC0AA1F7C1B16ECE5F36C02AA26"
                + "4E38D43D1913185CFDF900210BA319FA"
                + "0B5942F33374");

        rpuReader = new RbspReader(buf, 0, buf.length);

        RpuDataMapping mapping = new RpuDataMapping();
        mapping.read(h, rpuReader);

        out.println("+ RPU Data Mapping");
        mapping.print(h, new RbspPrinter(out).enter().enter());

        out.println("  available: " + rpuReader.available());
        out.println("  offset: " + (buf.length - (rpuReader.available() >> 3)));

        RpuDataNLQ nlq = new RpuDataNLQ();
        nlq.read(h, rpuReader);

        out.println("+ RPU Data NLQ");
        nlq.print(h, new RbspPrinter(out).enter().enter());

        out.println("  offset: " + (buf.length - (rpuReader.available() >> 3)));
    }
}
