package band.full.video.itu.h265;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

import band.full.video.dolby.RPU;
import band.full.video.itu.nal.RbspPrinter;
import band.full.video.itu.nal.RbspReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class PrintNALU {
    // private static final String IN_FILE =
    // "target/video-main/H.265-HEVC/FullHD/HLG10/Basic/Checkerboard-FHD_HLG10-1090.hevc";

    // private static final String IN_FILE = "../DV5.h265";
    // private static final String IN_FILE = "../../DV5/dv_el_out_1.h265";
    private static final String IN_FILE = "../../iOS_P5_track1.dvh1";

    // private static final String IN_FILE = "../generators/target/"
    // + "video-main/H.265-HEVC/UHD4K/DVp5/Basic/BT2111~U4K_DVp5.hevc";

    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        long time = currentTimeMillis();
        try (var reader = new H265ReaderAVCC(new FileInputStream(IN_FILE));
             var writer = new H265WriterAnnexB(
                     new FileOutputStream("../out.h265"))) {
            final H265Context elContext = new H265Context();

            for (int i = 0; i < 3000; i++) {
                NALUnit nalu = reader.read();
                if (nalu == null) {
                    break;
                }

                writer.write(reader.context, nalu);

                out.print(nalu.zero_byte ? "* " : "- ");
                out.println(nalu.getTypeString() +
                        " - " + nalu.getHeaderParamsString());

                nalu.print(reader.context,
                        new RbspPrinter(out).enter().enter());

                switch (nalu.type) {
                    case UNSPEC62: {
                        // TODO check RPU NALU indication (0x7C01)
                        NALU n = (NALU) nalu;

                        RPU rpu = new RPU(n.bytes);
                        rpu.print(null, new RbspPrinter(out).enter().enter());

                        byte[] bytes = rpu.toBytes(256);

                        // String binary = toBinaryString(n.bytes);
                        // for (int x = 0; x < binary.length(); x += 64) {
                        // out.println(binary.substring(x,
                        // min(binary.length(), x + 64)));
                        // }
                        //
                        // out.println();
                        // binary = toBinaryString(bytes);
                        // for (int x = 0; x < binary.length(); x += 64) {
                        // out.println(binary.substring(x,
                        // min(binary.length(), x + 64)));
                        // }

                        if (!Arrays.equals(n.bytes, bytes))
                            throw new IllegalStateException();

                        // String binary = toBinaryString(n.bytes);
                        // for (int x = 0; x < binary.length(); x += 64) {
                        // out.println(binary.substring(x,
                        // min(binary.length(), x + 64)));
                        // }
                        //
                        // binary = toHexString(n.bytes);
                        // for (int x = 0; x < binary.length(); x += 16) {
                        // out.println(binary.substring(x,
                        // min(binary.length(), x + 16)));
                        // }
                        //
                        // out.println("+ RPU Header");
                        // out.println("+ RPU Data Mapping");
                        // out.println("+ RPU Data NLQ");
                        // out.println("+ VDR Display Management Data");
                        break;
                    }

                    case UNSPEC63: {
                        NALU n = (NALU) nalu;
                        NALUnit dv = NALUnit.create(elContext,
                                new RbspReader(n.bytes, 0, n.bytes.length));

                        out.print("+ ");
                        out.println(dv.getTypeString() +
                                " - " + dv.getHeaderParamsString());

                        dv.print(reader.context,
                                new RbspPrinter(out).enter().enter());
                        break;
                    }

                    default:
                }

                out.println();
            }
        }

        out.println("Time: " + (currentTimeMillis() - time));
    }
}
