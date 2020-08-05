import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import org.apache.logging.log4j.LogManager;

public class Main {

  public static final String APP_NAME = "Voltage Control";

  public static void main(String[] args) {
    try {
      JFrame frame = new JFrame();
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
    }
  }
}