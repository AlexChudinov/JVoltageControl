import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class DoublePropertyPane extends JPanel {

  private DoublePropertiesCollection collection = new DoublePropertiesCollection();

  public static void main(String[] args) {
    DoublePropertyPane pane = new DoublePropertyPane();
    pane.addVoltageControl("volts1",
        new DoubleSpinner(0.0, -10., 10, 0.1));
    pane.addVoltageControl("volts2",
        new DoubleSpinner(0.0, -10., 10., 0.1));
    JFrame frame = new JFrame();
    frame.add(pane);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public DoublePropertyPane() {
    setLayout(new GridBagLayout());
  }

  public void addVoltageControl(String name, DoubleSpinner spinner) {
    if (collection.addVoltageControl(name, spinner)) {
      GridBagConstraints labelConstraints
          = new GridBagConstraints();
      labelConstraints.gridx = 0;
      labelConstraints.gridy = GridBagConstraints.RELATIVE;

      JLabel label = new JLabel(name);
      ((GridBagLayout) getLayout())
          .setConstraints(label, labelConstraints);
      add(label);
      label.addMouseListener(new LabelMouseListener());

      GridBagConstraints spinnerConstraints
          = new GridBagConstraints();
      spinnerConstraints.gridx = 1;
      spinnerConstraints.gridy = GridBagConstraints.RELATIVE;
      spinnerConstraints.ipadx = 40;

      ((GridBagLayout) getLayout())
          .setConstraints(spinner, spinnerConstraints);
      add(spinner);
    }
  }

  private class LabelMouseListener implements MouseListener{

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
      if(e.getButton() == MouseEvent.BUTTON3){
        System.out.println(((JLabel)e.getComponent()).getText());
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
  }
}