import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.TableView.TableRow;

public class VoltageTableParams extends JTable {

  private static final String DEF_PROPERTIES_FILE_NAME =
      "data/config.properties";

  private static final Object[] COLUMN_NAMES = new String[]{
      "Electrode", "Value"
  };

  private static final List<String> ELECTRODE_NAMES
      = new ArrayList<String>() {{
    add("Reflector Grid");
    add("Reflector plate");
    add("Pulse (-)");
    add("Pulse (+)");
    add("Focusing");
    add("Accel.");
  }};

  private static final List<String> PROPERTIES_NAMES
      = new ArrayList<String>() {{
    for (String str : ELECTRODE_NAMES) {
      add(String.join("_", str.split("\\s")));
    }
  }};

  private static final Map<String, Byte> ELECTRODES
      = new HashMap<String, Byte>() {{
    put("Reflector Grid", (byte) 0x00);
    put("Pulse (-)", (byte) 0x01);
    put("Reflector Plate", (byte) 0x02);
    put("Pulse (+)", (byte) 0x03);
    put("Focusing", (byte) 0x05);
    put("Accel.", (byte) 0x06);
  }};

  public VoltageTableParams() throws IOException {
    Properties props = new Properties();
    InputStream in = new FileInputStream(DEF_PROPERTIES_FILE_NAME);
    props.load(in);
    List<Double> values = new ArrayList<>();
    for (String name : PROPERTIES_NAMES) {
      values.add(Double.valueOf((String)props.get(name)));
    }
    Object[][] data = new Object[ELECTRODE_NAMES.size()][2];

    for (int i = 0; i < PROPERTIES_NAMES.size(); ++i) {
      data[i][0] = ELECTRODE_NAMES.get(i);
      data[i][1] = values.get(i);
    }

    setModel(new DefaultTableModel(data, COLUMN_NAMES));

    getColumnModel().getColumn(0).setCellEditor(SET_NOTEDITABLE);

    getColumnModel().getColumn(1).setCellEditor(new SpinnerEditor());

    setFont(new Font("Times", Font.BOLD, 30));

    updateRowHeights();
  }

  private void updateRowHeights()
  {
    for (int row = 0; row < getRowCount(); row++)
    {
      int rowHeight = getRowHeight();

      for (int column = 0; column < getColumnCount(); column++)
      {
        Component comp = prepareRenderer(getCellRenderer(row, column), row, column);
        rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
      }

      setRowHeight(row, rowHeight);
    }
  }

  private static final TableCellEditor SET_NOTEDITABLE
      = new TableCellEditor() {
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
        int row, int column) {
      return null;
    }

    @Override
    public Object getCellEditorValue() {
      return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
      return false;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
      return false;
    }

    @Override
    public boolean stopCellEditing() {
      return false;
    }

    @Override
    public void cancelCellEditing() {

    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {

    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {

    }
  };

  public static class SpinnerEditor extends DefaultCellEditor
  {
    JSpinner spinner;
    JSpinner.DefaultEditor editor;
    JTextField textField;
    boolean valueSet;

    public SpinnerEditor() {
      super(new JTextField());
      spinner = new JSpinner();
      spinner.setModel(new SpinnerNumberModel(0., -1000., 1000., .1));
      editor = ((JSpinner.DefaultEditor)spinner.getEditor());
      textField = editor.getTextField();
      textField.addFocusListener( new FocusListener() {
        public void focusGained( FocusEvent fe ) {
          SwingUtilities.invokeLater(() -> {
            if ( valueSet ) {
              textField.setCaretPosition(1);
            }
          });
        }
        public void focusLost( FocusEvent fe ) {
        }
      });
      textField.addActionListener(ae -> stopCellEditing());
      spinner.addChangeListener(System.err::println);
    }

    // Prepares the spinner component and returns it.
    public Component getTableCellEditorComponent(
        JTable table, Object value, boolean isSelected, int row, int column
    ) {
      if ( !valueSet ) {
        spinner.setValue(value);
      }
      SwingUtilities.invokeLater(textField::requestFocus);
      return spinner;
    }

    public boolean isCellEditable( EventObject eo ) {
      if ( eo instanceof KeyEvent) {
        KeyEvent ke = (KeyEvent)eo;
        textField.setText(String.valueOf(ke.getKeyChar()));
        valueSet = true;
      } else {
        valueSet = false;
      }
      return true;
    }

    public Object getCellEditorValue() {
      return spinner.getValue();
    }

    public boolean stopCellEditing() {
      try {
        editor.commitEdit();
        spinner.commitEdit();
      } catch ( java.text.ParseException e ) {
        JOptionPane.showMessageDialog(null,
            "Invalid value, discarding.");
      }
      return super.stopCellEditing();
    }
  }
}
