import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseWheelEvent;
import java.text.ParseException;
import java.util.TreeSet;
import java.util.function.DoubleConsumer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import jssc.SerialPortException;
import org.apache.logging.log4j.LogManager;

public class VoltageSpinner extends JSpinner {

  private Voltage value;

  public static void main(String[] args) throws Exception {
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
        (byte)0x30,
        5.,
        voltageByteValues,
        voltageCalibrationValues
    );

    panel.add(new VoltageSpinner(voltage, 0.0, 10.0, 0.1));
    frame.add(panel);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public VoltageSpinner(
      Voltage cur,
      double min,
      double max,
      double step) {
    value = cur;

    setModel(new SpinnerNumberModel(cur.getValue(), min, max, step));

    addChangeListener(this::changeListener);

    addMouseWheelListener(this::mouseWheelListener);

    ((JSpinner.DefaultEditor) getEditor())
        .getTextField()
        .addFocusListener(new EditorFocusListener());

    ((JSpinner.DefaultEditor) getEditor())
        .getTextField()
        .setHorizontalAlignment(JTextField.CENTER);
  }

  private void mouseWheelListener(MouseWheelEvent mouseWheelEvent) {
    Double val = (Double) getModel().getValue()
        + mouseWheelEvent.getWheelRotation()
        * (Double) ((SpinnerNumberModel) getModel()).getStepSize();
    Double max = (Double) ((SpinnerNumberModel) getModel()).getMaximum();
    Double min = (Double) ((SpinnerNumberModel) getModel()).getMinimum();
    if (val > max) {
      val = max;
    } else if (val < min) {
      val = min;
    }
    setValue(val);
  }

  private void changeListener(ChangeEvent changeEvent) {
    try {
      if (!getValue().equals(value.getValue())) {
        LogManager.getRootLogger().info("Voltage value was changed from "
            + value.getValue() + " to " + getValue());
        value.setValue((double) getValue());
      }
    } catch (SerialPortException ex) {
      String msg = "Serial port communication error";
      LogManager.getRootLogger().warn(msg);
      JOptionPane.showMessageDialog(this,
          "Serial port communication error");
    }
  }

  private class EditorFocusListener implements FocusListener {

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
      try {
        String str = ((JTextField) e.getComponent()).getText();
        ((DefaultEditor) getEditor()).commitEdit();
      } catch (ParseException parseException) {
        LogManager.getRootLogger().info(parseException.getMessage());
      }
    }
  }
}