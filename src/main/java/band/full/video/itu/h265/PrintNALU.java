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
    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        long time = currentTimeMillis();
        try (var parser = new H265ReaderAnnexB(new FileInputStream(
                // "target/video-main/H.265-HEVC/FullHD/HLG10/Basic/Checkerboard-FHD_HLG10-1090.hevc"))
                // {
                // "../LG_DolbyTrailer.h265")) {
                "../DV5.h265"));
             var writer = new H265WriterAnnexB(
                     new FileOutputStream("../out.h265"))) {
            for (int i = 0; i < 2000000; i++) {
                NALUnit nalu = parser.read();
                if (nalu == null) {
                    break;
                }
                writer.write(nalu);

                out.print(nalu.zero_byte ? "* " : "- ");
                out.println(nalu.getTypeString() +
                        " - " + nalu.getHeaderParamsString());
                nalu.print(out);

                if (nalu.type == UNSPEC63) {
                    NALU n = (NALU) nalu;
                    NALUnit dv = NALUnit
                            .create(new RbspReader(n.bytes, 0, n.bytes.length));

                    out.print("+ ");
                    out.println(dv.getTypeString() +
                            " - " + dv.getHeaderParamsString());
                    dv.print(out);
                }

                out.println();
            }
        }

        System.out.println("Time: " + (currentTimeMillis() - time));
    }
}
