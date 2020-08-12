import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.apache.logging.log4j.LogManager;

public class Main {

  public static final String APP_NAME = "Voltage Control";

  public static void main(String[] args) {
    try {
      JFrame frame = new JFrame();
      JPanel panel = new JPanel();
      TreeSet<Double> voltageCalibrationValues = new TreeSet<Double>() {{
        add(0.0);
        add(3000.0);
      }};

      TreeSet<Integer> voltageByteValues = new TreeSet<Integer>() {{
        add(0x0000);
        add(0xFFFF);
      }};

      Voltage voltage = new Voltage(
          (byte) 0x30,
          0.,
          voltageByteValues,
          voltageCalibrationValues
      );
      ArduinoCommunication communication = ArduinoCommunication.getInstance();
      voltage.setCommunication(communication);
      panel.setLayout(new GridLayout(0,1));
      panel.add(new VoltageSpinner(voltage, 0.0, 3000.0, 0.1));
      frame.add(panel);
      frame.pack();
      frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          super.windowClosing(e);
          try {
            communication.close();
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      });
      frame.setVisible(true);
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
    /*try {
      JFrame frame = new JFrame();
      frame.setSize(300, 200);
      frame.setResizable(false);
      frame.setTitle(APP_NAME);
      frame.add(new VoltageTableParams());
      frame.setVisible(true);
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          null,
          "Fatal error in "
              + APP_NAME
              + ": " + ex.getMessage());
      LogManager.getRootLogger().fatal(ex.getMessage());
      ex.printStackTrace();
    }*/
  }
}
