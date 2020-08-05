import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class VoltageSpinnersPane extends JPanel {

  private VoltageSpinnerCollection collection = new VoltageSpinnerCollection();

  private List<String> names = new ArrayList<>();

  public static void main(String[] args) {
    VoltageSpinnersPane pane = new VoltageSpinnersPane();
    pane.addVoltageControl("volts1",
        new VoltageSpinner(0.0, -10., 10, 0.1));
    pane.addVoltageControl("volts2",
        new VoltageSpinner(0.0, -10., 10., 0.1));
    JFrame frame = new JFrame();
    frame.add(pane);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public VoltageSpinnersPane() {
    setLayout(new GridBagLayout());
  }

  public void addVoltageControl(String name, VoltageSpinner spinner) {
    if (collection.addVoltageControl(name, spinner)) {
      GridBagConstraints labelConstraints
          = new GridBagConstraints();
      labelConstraints.gridx = 0;
      labelConstraints.gridy = GridBagConstraints.RELATIVE;

      JLabel label = new JLabel(name);
      ((GridBagLayout) getLayout())
          .setConstraints(label, labelConstraints);
      add(label);

      GridBagConstraints spinnerConstraints
          = new GridBagConstraints();
      spinnerConstraints.gridx = 1;
      spinnerConstraints.gridy = GridBagConstraints.RELATIVE;

      ((GridBagLayout) getLayout())
          .setConstraints(spinner, spinnerConstraints);
      add(spinner);
    }
  }
}
