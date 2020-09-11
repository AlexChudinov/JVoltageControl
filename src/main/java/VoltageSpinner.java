import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.nio.ByteBuffer;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import org.apache.logging.log4j.LogManager;

public class VoltageSpinner extends DoubleSpinnerWithEdit {

  private final float FONT_SIZE = 18.0f;

  private Voltage value;

  public VoltageSpinner(
      Voltage cur,
      double min,
      double max,
      double step) {
    super(cur.getValue(), min, max, step);

    value = cur;

    addChangeListener(this::changeListener);

    addMouseWheelListener(this::mouseWheelListener);

    setContextDialog(new PropertiesDlg(null, this));
  }

  private void mouseWheelListener(MouseWheelEvent mouseWheelEvent) {
    if (mouseWheelEvent.getWheelRotation() == 0) {
      setValue(value.getValue());
    }
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
    } catch (Exception ex) {
      String msg = "Serial port communication error";
      LogManager.getRootLogger().warn(msg);
      JOptionPane.showMessageDialog(this,
          "Serial port communication error");
    }
  }

  public class PropertiesDlg extends JDialog {

    private static final String MAX_VOLTAGE_LABEL = "Max voltage:";

    private static final String STEP_LABEL = "Step:";

    private final VoltageSpinner spinner;

    private JSpinner byteSpinner;

    private JSpinner spinnerMaxValueControl;

    private JSpinner spinnerStepValueControl;

    private DoubleSpinnerWithEdit voltageSpinner;

    public PropertiesDlg(JFrame frame, VoltageSpinner spinner) {
      super(frame, true);
      setTitle("Voltage control properties");
      this.spinner = spinner;
      JTabbedPane tabbedPane = new JTabbedPane();
      tabbedPane.addTab("Spinner properties", createSpinnerPanel());
      tabbedPane.addTab("Voltage properties", createVoltagePanel());
      setContentPane(tabbedPane);
      pack();
    }

    private JPanel createSpinnerPanel() {
      JPanel panel = new JPanel(new GridBagLayout());

      JLabel maxLabel = new JLabel("Max:");

      panel.add(maxLabel);

      spinnerMaxValueControl = new DoubleSpinnerWithEdit(
          (Double) ((SpinnerNumberModel) spinner.getModel()).getMaximum(),
          0.0,
          10000.,
          0.1
      );
      panel.add(spinnerMaxValueControl);

      JLabel stepLabel = new JLabel("Step:");

      panel.add(stepLabel);

      spinnerStepValueControl = new DoubleSpinnerWithEdit(
          (double) ((SpinnerNumberModel) spinner.getModel()).getStepSize(),
          0.0,
          1000.,
          0.1
      );
      panel.add(spinnerStepValueControl);

      JButton apply = new JButton("Apply");
      apply.addActionListener(this::applyVoltageSpinnerParameters);

      panel.add(apply);

      return panel;
    }

    private void applyVoltageSpinnerParameters(ActionEvent actionEvent) {
      ((SpinnerNumberModel) spinner.getModel())
          .setStepSize(
              (Double) spinnerStepValueControl.getModel().getValue());
      ((SpinnerNumberModel) spinner.getModel())
          .setMaximum(
              (Double) spinnerMaxValueControl.getModel().getValue());
      dispose();
    }

    private JPanel createVoltagePanel() {
      JPanel panel = new JPanel(new GridBagLayout());

      addAddressInfo(panel);

      addVoltageValue(panel);

      addByteValue(panel);

      addButtons(panel);

      addCalTab(panel);

      return panel;
    }

    private void addCalTab(JPanel panel) {
      GridBagConstraints tableConstraints = new GridBagConstraints();
      tableConstraints.gridwidth = 6;
      tableConstraints.gridx = 0;
      tableConstraints.gridy = GridBagConstraints.RELATIVE;
      panel.add(showCalTable(), tableConstraints);
    }

    private void addButtons(JPanel panel) {
      JButton testButton = new JButton("Test");
      testButton.addActionListener(this::testButtonCallback);
      GridBagConstraints testButtonConstraints = new GridBagConstraints();
      testButtonConstraints.gridx = 0;
      testButtonConstraints.gridy = GridBagConstraints.RELATIVE;
      testButtonConstraints.gridwidth = 6;
      testButtonConstraints.weightx = 0.5;
      testButtonConstraints.weighty = 0.5;
      testButtonConstraints.anchor = GridBagConstraints.CENTER;
      panel.add(testButton, testButtonConstraints);
    }

    private void addByteValue(JPanel panel) {
      JLabel label = new JLabel("Bytes:");
      GridBagConstraints labelConstraints = new GridBagConstraints();
      labelConstraints.gridx = 0;
      labelConstraints.gridwidth = 3;
      labelConstraints.gridy = GridBagConstraints.RELATIVE;
      labelConstraints.weightx = 0.5;
      labelConstraints.weighty = 0.5;
      labelConstraints.anchor = GridBagConstraints.CENTER;
      panel.add(label, labelConstraints);

      byteSpinner = new JSpinner();
      byteSpinner.setModel(
          new SpinnerNumberModel(0, 0, 0xFFFF, 1));

      JTextField textField = ((JSpinner.DefaultEditor) byteSpinner.getEditor())
          .getTextField();
      textField.setHorizontalAlignment(JTextField.CENTER);
      textField.setFont(textField.getFont().deriveFont(FONT_SIZE));

      GridBagConstraints valueConstraints = new GridBagConstraints();
      valueConstraints.gridx = 3;
      valueConstraints.gridy = GridBagConstraints.RELATIVE;
      valueConstraints.gridwidth = 3;
      valueConstraints.weightx = 0.5;
      valueConstraints.weighty = 0.5;
      valueConstraints.anchor = GridBagConstraints.CENTER;
      panel.add(byteSpinner, valueConstraints);
    }

    private void addVoltageValue(JPanel panel) {
      JLabel label = new JLabel("Volts:");
      GridBagConstraints labelConstraints = new GridBagConstraints();
      labelConstraints.gridx = 0;
      labelConstraints.gridy = GridBagConstraints.RELATIVE;
      labelConstraints.gridwidth = 3;
      labelConstraints.weightx = 0.5;
      labelConstraints.weighty = 0.5;
      labelConstraints.anchor = GridBagConstraints.CENTER;
      panel.add(label, labelConstraints);

      voltageSpinner = new DoubleSpinnerWithEdit(
          spinner.value.getValue(),
          0.0,
          10000.,
          10.);
      GridBagConstraints valueConstraints = new GridBagConstraints();
      valueConstraints.gridx = 3;
      valueConstraints.gridy = GridBagConstraints.RELATIVE;
      valueConstraints.gridwidth = 3;
      valueConstraints.weightx = 0.5;
      valueConstraints.weighty = 0.5;
      valueConstraints.anchor = GridBagConstraints.CENTER;
      panel.add(voltageSpinner, valueConstraints);
    }

    private void addAddressInfo(JPanel panel) {
      JLabel label = new JLabel("Address:");
      GridBagConstraints labelConstraints = new GridBagConstraints();
      labelConstraints.gridx = 0;
      labelConstraints.gridy = GridBagConstraints.RELATIVE;
      labelConstraints.gridwidth = 3;
      labelConstraints.weightx = 0.5;
      labelConstraints.weighty = 0.5;
      labelConstraints.anchor = GridBagConstraints.CENTER;
      panel.add(label, labelConstraints);

      JLabel value = new JLabel("0x0" + Integer.toHexString(
          spinner.value.getAddress() & 0xF));
      value.setFont(value.getFont().deriveFont(FONT_SIZE));
      GridBagConstraints valueConstraints = new GridBagConstraints();
      valueConstraints.gridx = 3;
      valueConstraints.gridy = GridBagConstraints.RELATIVE;
      valueConstraints.gridwidth = 3;
      valueConstraints.weightx = 0.5;
      valueConstraints.weighty = 0.5;
      valueConstraints.anchor = GridBagConstraints.CENTER;
      panel.add(value, valueConstraints);
    }

    private void testButtonCallback(ActionEvent e) {
      try {
        int byteValue = (int) byteSpinner.getValue();
        byte address = spinner.value.getAddress();
        byte[] bytesToWrite = new byte[Voltage.COMMUNICATION_BYTES_SIZE];
        ByteBuffer.wrap(bytesToWrite).put(address);
        ByteBuffer.wrap(bytesToWrite).putShort(1, (short) byteValue);
        ArduinoCommunication.getInstance().writeBytes(bytesToWrite);
      } catch (Exception ex) {
        String msg = "Serial port communication error";
        LogManager.getRootLogger().warn(msg);
        JOptionPane.showMessageDialog(this,
            "Serial port communication error");
      }
    }

    private JTable showCalTable() {
      Object[] columnsHeader = new String[]{"Byte:", "Volts:"};
      Map<Double, Integer> calibrationTable = value.getCalibrationTable();
      Object[][] tableContent = new Object[20][2];
      int i = 0;
      for (Map.Entry<Double, Integer> entry : calibrationTable.entrySet()) {
        tableContent[i][0] = entry.getValue();
        tableContent[i][1] = entry.getKey();
        i++;
      }
      JTable table = new JTable(tableContent, columnsHeader);
      table.setFont(table.getFont().deriveFont(FONT_SIZE));
      return table;
    }
  }
}
