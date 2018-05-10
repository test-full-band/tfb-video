package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.TRAIL_R;
import static band.full.video.itu.h265.SEI.PREFIX_SEI;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

import band.full.video.itu.nal.sei.UserDataRegisteredT35;
import band.full.video.scte.ATSC1;
import band.full.video.smpte.st2094.ST2094_10;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class InjectDV {
    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        long time = currentTimeMillis();
        try (var parser = new H265ReaderAnnexB(
                new FileInputStream("../BT2111HDR10.h265"));
             var writer = new H265WriterAnnexB(
                     new FileOutputStream("../BT2111DV10.h265"))) {
            for (int i = 0; i < 100; i++) {
                NALUnit nalu = parser.read();
                if (nalu == null) {
                    break;
                }

                writer.write(nalu);
                print(nalu);

                if (nalu.type == TRAIL_R) {
                    var cr = new ST2094_10.ContentRange();
                    cr.min_PQ = 0;
                    cr.max_PQ = 2048;
                    cr.avg_PQ = 1024;

                    ST2094_10.TrimPass trim = new ST2094_10.TrimPass();
                    trim.target_max_PQ = 2048;

                    ST2094_10.DisplayManagementBlock[] blocks = {cr, trim};

                    ST2094_10 dm = new ST2094_10();
                    dm.metadata_refresh = true;
                    dm.ext_blocks = blocks;

                    ATSC1 atsc1 = new ATSC1();
                    atsc1.user_data_type_code = 0x09;
                    atsc1.user_data_type_structure = dm;

                    UserDataRegisteredT35 t35 = new UserDataRegisteredT35();
                    t35.country_code = ATSC1.COUNTRY_CODE;
                    t35.provider_code = ATSC1.PROVIDER_CODE;
                    t35.user_identifier = ATSC1.USER_IDENTIFIER;
                    t35.user_structure = atsc1;

                    SEI.Message msg = new SEI.Message();
                    msg.payloadType =
                            SEI.PayloadType.user_data_registered_itu_t_t35
                                    .ordinal();
                    msg.payload = t35;

                    SEI sei = PREFIX_SEI();
                    sei.zero_byte = true;
                    sei.messages = List.of(msg);

                    writer.write(nalu);
                    print(sei);
                }
            }
        }

        System.out.println("Time: " + (currentTimeMillis() - time));
    }

    private static void print(NALUnit nalu) {
        out.print(nalu.zero_byte ? "* " : "- ");
        out.println(nalu.getTypeString() +
                " - " + nalu.getHeaderParamsString());
        nalu.print(out);
        out.println();
    }
}
