import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.WindowConstants;

public class VoltageControl extends JFrame {

  public final static String APP_TITLE = "Voltage Control";

  private JPanel panel;
  private JTable table;

  public VoltageControl() throws IOException {
    $$$setupUI$$$();
    table = new VoltageTableParams();
    panel.add(table, new GridConstraints(0, 0, 1, 1,
        GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_BOTH,
        GridConstraints.SIZEPOLICY_WANT_GROW,
        GridConstraints.SIZEPOLICY_WANT_GROW, null,
        new Dimension(150, 50), null, 0, false));
    setTitle(APP_TITLE);
    setSize(200, 100);
    setContentPane(panel);
    setVisible(true);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR
   * call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    panel = new JPanel();
    panel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return panel;
  }

}
