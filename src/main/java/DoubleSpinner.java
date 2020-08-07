import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseWheelEvent;
import java.text.ParseException;
import java.util.Objects;
import java.util.function.DoubleConsumer;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import org.apache.logging.log4j.LogManager;

public class DoubleSpinner extends JSpinner {

  private Double value;

  private DoubleConsumer valueListener;

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    DoubleSpinner spinner =
        new DoubleSpinner(0.0, -100., 100., 0.1);
    spinner.addValueListener(
        a -> LogManager.getRootLogger().info("Test value accepted " + a));
    frame.add(spinner);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public DoubleSpinner(double cur, double min, double max, double step) {
    value = cur;

    setModel(new SpinnerNumberModel(cur, min, max, step));

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
    if (!getValue().equals(value)) {
      LogManager.getRootLogger().info("Voltage value was changed from "
          + value + " to " + getValue());
      value = (Double) getValue();
      if (!Objects.isNull(valueListener)) {
        valueListener.accept(value);
      }
    }
  }

  public void addValueListener(DoubleConsumer op) {
    valueListener = op;
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
