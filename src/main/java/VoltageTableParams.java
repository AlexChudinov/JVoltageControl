import java.awt.Component;
import java.awt.Font;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class VoltageTableParams extends VoltageSpinnersPane {

  private static final String DEF_PROPERTIES_FILE_NAME =
      "data/config.properties";

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
    loadProperties();
  }

  private void loadProperties() throws IOException {
    Properties props = new Properties();
    InputStream in = new FileInputStream(DEF_PROPERTIES_FILE_NAME);
    props.load(in);

    for (int i = 0; i < ELECTRODES.size(); ++i) {
      String propName = PROPERTIES_NAMES.get(i);
      String electrodeName = ELECTRODE_NAMES.get(i);
      addVoltageControl(electrodeName,
          new VoltageSpinner(
              Double.parseDouble((String) props.get(propName)),
              Double.parseDouble((String)props.get(propName + "_min")),
              Double.parseDouble((String)props.get(propName + "_max")),
              Double.parseDouble((String)props.get(propName + "_step"))));
    }
  }
}
