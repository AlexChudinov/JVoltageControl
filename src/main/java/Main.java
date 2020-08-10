import javax.swing.JOptionPane;
import org.apache.logging.log4j.LogManager;

public class Main {

  public static final String APP_NAME = "Voltage Control";

  public static void main(String[] args) {
    try {
      VoltageSpinner.main(args);
      /*JFrame frame = new JFrame();
      frame.setSize(300, 200);
      frame.setResizable(false);
      frame.setTitle(APP_NAME);
      frame.add(new VoltageTableParams());
      frame.setVisible(true);
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);*/
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
