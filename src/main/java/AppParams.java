import java.awt.GridLayout;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import jssc.SerialPortException;
import org.apache.logging.log4j.LogManager;
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

  private String portName;

  public AppParams() throws Exception {
    load();
  }

  public void initVoltages(int timeDelaySec)
      throws SerialPortException, InterruptedException {
    ArduinoCommunication communication = ArduinoCommunication.getInstance(portName);
    this.voltages.values().forEach(a -> a.setCommunication(communication));
    Map<String, Double> curValues = new HashMap<>();
    for (Map.Entry<String, Voltage> voltage : voltages.entrySet()) {
      curValues.put(voltage.getKey(), voltage.getValue().getValue());
      voltage.getValue().setValue(0.0);
    }
    int timeStepMs = timeDelaySec * 10;
    for (int i = 1; i <= 100; ++i) {
      for (Map.Entry<String, Voltage> voltage : voltages.entrySet()) {
        double val = (curValues.get(voltage.getKey()) * i) / 100;
        LogManager.getRootLogger().info("Voltage "
            + voltage.getKey() + " initialised with value: " + val);
        voltage.getValue().setValue(val);
      }
      Thread.sleep(timeStepMs);
    }
  }

  public void stopVoltages(int timeDelaySec)
      throws Exception {
    Map<String, Double> curValues = new HashMap<>();
    for (Map.Entry<String, Voltage> voltage : voltages.entrySet()) {
      curValues.put(voltage.getKey(), voltage.getValue().getValue());
    }
    int timeStepMs = timeDelaySec * 10;
    for (int i = 100; i >= 0; --i) {
      for (Map.Entry<String, Voltage> voltage : voltages.entrySet()) {
        double val = (curValues.get(voltage.getKey()) * i) / 100;
        voltage.getValue().setValue(val);
      }
      Thread.sleep(timeStepMs);
    }
    ArduinoCommunication.getInstance().close();
  }

  public JPanel getControlPane() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 2));
    for (Map.Entry<String, VoltageSpinner> spinner : spinners.entrySet()) {
      JLabel voltageNameLabel = new JLabel(spinner.getKey());
      voltageNameLabel.setHorizontalAlignment(JTextField.CENTER);
      voltageNameLabel.setFont(voltageNameLabel.getFont().deriveFont(20.0f));
      panel.add(voltageNameLabel);
      panel.add(spinner.getValue());
    }
    resetSpinnerValues();
    return panel;
  }

  public JPanel getInfoPane() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1));
    for (Map.Entry<String, VoltageSpinner> spinner : spinners.entrySet()) {
      JLabel label = new JLabel(spinner.getKey()
          + ": " + spinner.getValue().getValue());
      label.setFont(label.getFont().deriveFont(20.0f));
      resetSpinnerValues();
      panel.add(label);
    }
    return panel;
  }

  public void load(String fileName)
      throws Exception {
    JSONParser parser = new JSONParser();
    JSONObject object = (JSONObject) parser.parse(
        new FileReader(fileName));
    JSONArray voltages = (JSONArray) object.get("voltages");
    this.voltages.clear();
    spinners.clear();
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
    portName = (String) object.get("com");
  }

  private void load()
      throws Exception {
    load(DEFAULT_PROPERTIES_FILE_NAME);
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

    int firstByte = (int) ((long) bytes.get(0));
    Double firstVoltage = (Double) voltages.get(0);

    if (firstVoltage < 0.0 || !checkByte(firstByte)) {
      throw new AppParamsException(
          "Wrong first calibration value in configuration file."
              + "It should be not lesser than zero");
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

  public void save() throws IOException {
    save(DEFAULT_PROPERTIES_FILE_NAME);
  }

  public void save(String fileName) throws IOException {
    JSONObject obj = new JSONObject();
    obj.put("com", portName);
    JSONArray voltagesJson = new JSONArray();
    for (Map.Entry<String, VoltageSpinner> voltage : spinners.entrySet()) {
      JSONObject o = new JSONObject();
      VoltageSpinner v = voltage.getValue();
      o.put("name", voltage.getKey());
      o.putAll(voltages.get(voltage.getKey()).toJson());
      o.put("min", ((SpinnerNumberModel) v.getModel()).getMinimum());
      o.put("max", ((SpinnerNumberModel) v.getModel()).getMaximum());
      o.put("step", ((SpinnerNumberModel) v.getModel()).getStepSize());
      voltagesJson.add(o);
    }
    obj.put("voltages", voltagesJson);
    Writer out = new FileWriter(fileName);
    obj.writeJSONString(out);
    out.flush();
    out.close();
  }

  private void resetSpinnerValues() {
    for (Map.Entry<String, VoltageSpinner> e : spinners.entrySet()){
      Voltage v = voltages.get(e.getKey());
      e.getValue().setValue(v.getValue());
    }
  }
}
