import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import junit.framework.TestCase;

public class VoltageSpinnerTest extends TestCase {

  public void testMain() {
    try {
      JFrame frame = new JFrame();
      JPanel panel = new JPanel();
      TreeSet<Double> voltageCalibrationValues = new TreeSet<Double>() {{
        add(0.0);
        add(10.0);
      }};

      TreeSet<Integer> voltageByteValues = new TreeSet<Integer>() {{
        add(0x0000);
        add(0xFFFF);
      }};

      Voltage voltage = new Voltage(
          (byte) 0x30,
          5.,
          voltageByteValues,
          voltageCalibrationValues
      );

      panel.add(new VoltageSpinner(voltage, 0.0, 10.0, 0.1));
      frame.add(panel);

      BlockedMainThread.blockMain(frame);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
