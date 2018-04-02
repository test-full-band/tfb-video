package band.full.video.itu.h265;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PrintNALU {
    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        try (FileInputStream in = new FileInputStream(
                "target/testing-video/HEVC/FullHD/Quantization/Quants3D1080pHEVC-2.hevc")) {
            NALUnitParser parser = new NALUnitParser(in);
            while (true) {
                NALUnit nalu = parser.read();
                if (nalu == null) {
                    break;
                }

                System.out.println(nalu.getTypeString());
            }
        }
    }
}
