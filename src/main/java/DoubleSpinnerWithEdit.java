import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.util.Objects;
import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import org.apache.logging.log4j.LogManager;

public class DoubleSpinnerWithEdit extends JSpinner {

  private double value;

  private JDialog contextDialog;

  public DoubleSpinnerWithEdit(
      double cur, double min, double max, double step) {
    value = cur;

    setModel(new SpinnerNumberModel(cur, min, max, step));

    ((JSpinner.DefaultEditor) getEditor())
        .getTextField()
        .addFocusListener(new TextFocusListener());

    JTextField textField = ((JSpinner.DefaultEditor) getEditor()).getTextField();
    textField.setHorizontalAlignment(JTextField.CENTER);
    textField.setFont(textField.getFont().deriveFont(18.0f));

    ((JSpinner.DefaultEditor) getEditor())
        .getTextField()
        .addMouseListener(new TextFieldMouseListener());
  }

  public void setContextDialog(JDialog contextDialog) {
    this.contextDialog = contextDialog;
  }

  private class TextFocusListener implements FocusListener {

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
      try {
        String str = ((JTextField) e.getComponent()).getText();
        ((DefaultEditor) getEditor()).commitEdit();
        value = (double) getValue();
      } catch (ParseException parseException) {
        LogManager.getRootLogger().info(parseException.getMessage());
        setValue(value);
      }
    }
  }

  private class TextFieldMouseListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
      switch (e.getButton()) {
        case MouseEvent.BUTTON3:
          if (!Objects.isNull(contextDialog)) {
            contextDialog.setVisible(true);
          }
          break;
        case MouseEvent.BUTTON2:
          setValue(e);
          break;
        default:
          //setValue(e);
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      //setValue(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      //setValue(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      //setValue(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
      //setValue(e);
    }

    private void setValue(MouseEvent e) {
      JSpinner spinner = ((DefaultEditor) (e.getComponent().getParent())).getSpinner();
      ((JTextField) e.getComponent()).setText(Double.toString(
          (Double) (spinner.getModel().getValue())
      ));
    }
  }
}
