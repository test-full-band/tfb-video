package band.full.video.smpte;

import static band.full.core.ArrayMath.fromHexString;
import static band.full.video.itu.h265.SEI.PREFIX_SEI;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import band.full.video.itu.h265.H265WriterAnnexB;
import band.full.video.itu.h265.SEI;
import band.full.video.itu.nal.sei.UserDataRegisteredT35;
import band.full.video.scte.ATSC1;
import band.full.video.smpte.st2094.ST2094_10;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Igor Malinin
 */
public class TestST2094 {
    @Test
    public void testST2094_10() throws IOException {
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

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        H265WriterAnnexB writer = new H265WriterAnnexB(out);
        writer.write(sei);
        writer.close();

        assertArrayEquals(fromHexString("00000001_4E01_041E"
                + "_B500314741393409"
                + "_5B300800400200001805001001001001001001FFF0"
                + "_FF80"), out.toByteArray());
    }
}
