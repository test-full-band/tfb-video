package band.full.video.itu.h265;

import static band.full.video.itu.h265.NALUnitType.UNSPEC63;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

import band.full.video.itu.nal.RbspReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PrintNALU {
    // private static final String IN_FILE =
    // "target/video-main/H.265-HEVC/UHD4K/DV10/Basic/BT2111U4K_DV10p5_15.hevc";

    // private static final String IN_FILE =
    // "target/video-main/H.265-HEVC/UHD4K/DV10/Basic/BT2111U4K_DV10p5.hevc";

    // private static final String IN_FILE =
    // "target/video-main/H.265-HEVC/FullHD/HLG10/Basic/Checkerboard-FHD_HLG10-1090.hevc";

    private static final String IN_FILE = "../../LG_DolbyTrailer.h265";
    // private static final String IN_FILE = "../DV5.h265";

    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        long time = currentTimeMillis();
        try (var reader = new H265ReaderAnnexB(new FileInputStream(IN_FILE));
             var writer = new H265WriterAnnexB(
                     new FileOutputStream("../out.h265"))) {
            final H265Context elContext = new H265Context();

            for (int i = 0; i < 100; i++) {
                NALUnit nalu = reader.read();
                if (nalu == null) {
                    break;
                }
                writer.write(reader.context, nalu);

                out.print(nalu.zero_byte ? "* " : "- ");
                out.println(nalu.getTypeString() +
                        " - " + nalu.getHeaderParamsString());
                nalu.print(reader.context, out);

                if (nalu.type == UNSPEC63) {
                    NALU n = (NALU) nalu;
                    NALUnit dv = NALUnit.create(elContext,
                            new RbspReader(n.bytes, 0, n.bytes.length));

                    out.print("+ ");
                    out.println(dv.getTypeString() +
                            " - " + dv.getHeaderParamsString());
                    dv.print(reader.context, out);
                }

                out.println();
            }
        }

        System.out.println("Time: " + (currentTimeMillis() - time));
    }
}
