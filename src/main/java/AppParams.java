import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class AppParams {

  public static class AppParamsException extends Exception {

    public AppParamsException(String msg) {
      super(msg);
    }
  }

  private static final String DEFAULT_PROPERTIES_FILE_NAME =
      "data/config.json";

  Map<String, Voltage> voltages = new HashMap<>();

  Map<String, VoltageSpinner> spinners = new HashMap<>();

  public AppParams() throws Exception {
    loadProperties();
  }

  public void loadProperties(String fileName)
      throws Exception {
    JSONParser parser = new JSONParser();
    JSONObject object = (JSONObject) parser.parse(
        new FileReader(fileName));
    JSONArray voltages = (JSONArray) object.get("voltages");
    for (Object voltageObj : voltages) {
      JSONObject voltage = (JSONObject) voltageObj;
      String name = (String) voltage.get("name");
      if (this.voltages.containsKey(name)) {
        throw new AppParamsException("Not an unique voltage name value: " + name);
      }
      Voltage v = loadVoltage(voltage);
      this.voltages.put(name, v);
      double max = (double) voltage.get("max");
      double min = (double) voltage.get("min");
      double step = (double) voltage.get("step");
      this.spinners.put(name, new VoltageSpinner(v, min, max, step));
    }
  }

  private void loadProperties()
      throws Exception {
    loadProperties(DEFAULT_PROPERTIES_FILE_NAME);
  }

  private static Voltage loadVoltage(JSONObject voltage)
      throws Exception {
    long address = (long) voltage.get("address");
    double value = (double) voltage.get("value");

    TreeSet<Integer> calibrationBytes = new TreeSet<>();
    TreeSet<Double> calibrationVoltages = new TreeSet<>();

    JSONObject calibration = (JSONObject) voltage.get("calibration");
    JSONArray bytes = (JSONArray) calibration.get("bytes");
    JSONArray voltages = (JSONArray) calibration.get("voltages");

    if (!bytes.isEmpty() && bytes.size() != voltages.size()) {
      throw new AppParamsException(
          "Wrong calibration values array size in configuration file");
    }

    Integer firstByte = (int) ((long) bytes.get(0));
    Double firstVoltage = (Double) voltages.get(0);

    if (firstVoltage < 0.0 || !checkByte(firstByte)) {
      throw new AppParamsException(
          "Wrong first calibration values in configuration file");
    }

    for (int i = 1; i < bytes.size(); ++i) {
      calibrationVoltages.add(firstVoltage);
      calibrationBytes.add(firstByte);
      int nextByte = (int) ((long) bytes.get(i));
      Double nextVoltage = (Double) voltages.get(i);
      if (nextByte <= firstByte || nextVoltage <= firstVoltage) {
        throw new AppParamsException(
            "Not ordered calibration values in configuration file");
      }
      firstByte = nextByte;
      firstVoltage = nextVoltage;
    }
    calibrationVoltages.add(firstVoltage);
    calibrationBytes.add(firstByte);

    return new Voltage((byte) address, value, calibrationBytes, calibrationVoltages);
  }

  private static boolean checkByte(Integer firstByte) {
    final int MIN_BYTE = 0x00;
    final int MAX_BYTE = 0xFFFF;
    return firstByte >= MIN_BYTE && firstByte <= MAX_BYTE;
  }
}
