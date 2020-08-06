import jdk.javadoc.internal.doclets.toolkit.util.Utils.Pair;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class VoltageCalibration {

    private static final String CALIBRATION_TABLE_FILENAME = "data/calib.dat";

    Map<Double, Pair<Byte, Byte>> calibrationTable = new HashMap<>();

    public VoltageCalibration() throws IOException {
        File file = new File(CALIBRATION_TABLE_FILENAME);
        if (file.exists()) {
            loadCalibrationTable(file);
        }
    }

    private void loadCalibrationTable(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file));
        String line;
        while(!Objects.isNull(line = in.readLine())){
            String[] tokens = line.split("\\s+");

        }

    }

    private static Pair<Byte, Byte> next(Pair<Byte, Byte> pair) {
        if (pair.second == (byte) 0xFF) {
            return new Pair<>((byte)(pair.first + 1), (byte)0x00);
        } else {
            return new Pair<>(pair.first, (byte)(pair.second + 1));
        }
    }

    private static int distance(Pair<Byte, Byte> start, Pair<Byte, Byte> finish){
        Pair<Byte, Byte> cur = start;
        int counter = 1;
        while(cur != finish){
            cur = next(cur);
            counter++;
        }
        return counter;
    }
}
