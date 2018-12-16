package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.PREFIX_SEI_NUT;
import static band.full.video.itu.h265.SEI.PREFIX_SEI;
import static band.full.video.itu.h265.SEI.PayloadType.user_data_registered_itu_t_t35;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.sei.UserDataRegisteredT35;
import band.full.video.scte.ATSC1;
import band.full.video.smpte.st2094.ST2094_10;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class InjectDV {
    private static final String IN_FILE =
            "target/video-main/H.265-HEVC/UHD4K/DV10/Basic/BT2111U4K_DV10.hevc";

    private static final String OUT_FILE =
            "target/video-main/H.265-HEVC/UHD4K/DV10/Basic/BT2111U4K_DV10p5.hevc";

    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        long time = currentTimeMillis();
        try (var reader = new H265ReaderAnnexB(new FileInputStream(IN_FILE));
             var writer = new H265WriterAnnexB(
                     new FileOutputStream(OUT_FILE))) {
            NALUnit nalu = reader.read();
            while (nalu != null) {
                if (nalu.type == PREFIX_SEI_NUT) {
                    SEI ps = (SEI) nalu;
                    if (ps.messages.get(0).payloadType == 1) {
                        SEI sei = SEI_ST2094_10(0);
                        writer.write(reader.context, sei);
                        print(reader.context, sei);
                    }
                }

                writer.write(reader.context, nalu);
                print(reader.context, nalu);

                nalu = reader.read();
            }
        }

        System.out.println("Time: " + (currentTimeMillis() - time));
    }

    private static SEI SEI_ST2094_10(int i) {
        var cr = new ST2094_10.ContentRange();
        {
            cr.min_PQ = 0;
            cr.max_PQ = 4095;
            cr.avg_PQ = 1024;
        }

        var trim = new ST2094_10.TrimPass();
        {
            trim.target_max_PQ = 2048;
            // trim.trim_power = (short) (4096 / 24 * i);
        }

        var msg = new SEI.Message(
                user_data_registered_itu_t_t35,
                new UserDataRegisteredT35(new ATSC1(
                        new ST2094_10(true, cr, trim))));

        return PREFIX_SEI(msg);
    }

    private static void print(H265Context context, NALUnit nalu) {
        out.print(nalu.zero_byte ? "* " : "- ");
        out.println(nalu.getTypeString() +
                " - " + nalu.getHeaderParamsString());
        nalu.print(context, new RbspPrinter(out).enter().enter());
        out.println();
    }
}
