import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class DoublePropertiesCollection {

  private Map<String, DoubleSpinner> spinners = new HashMap<>();

  private List<String> order = new ArrayList<>();

  public static void main(String[] args) {
    DoublePropertiesCollection collection = new DoublePropertiesCollection();
    collection.addVoltageControl("volts1", new DoubleSpinner(0.0, -100., 100., 0.1));
    collection.addVoltageControl("volts2", new DoubleSpinner(0.0, -100., 100., 0.1));
    JFrame frame = new JFrame();
    for(DoubleSpinner spinner : collection.getOrderedControls()){
      frame.add(spinner);
    }
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public DoublePropertiesCollection() {

  }

  public boolean addVoltageControl(String name, DoubleSpinner spinner)
      throws NullPointerException {
    if (Objects.isNull(spinner)) {
      throw new NullPointerException();
    }
    if(!spinners.containsKey(name)){
      spinners.put(name, spinner);
      order.add(name);
      return true;
    }
    return false;
  }

  public List<DoubleSpinner> getOrderedControls(){
    List<DoubleSpinner> controls = new ArrayList<>();
    for(String name : order){
      controls.add(spinners.get(name));
    }
    return controls;
  }

  public DoubleSpinner getSpinner(String name){
    return spinners.get(name);
  }

  public List<String> getOrder() {
    return order;
  }
}
