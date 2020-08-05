import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class VoltageSpinnerCollection {

  private Map<String, VoltageSpinner> spinners = new HashMap<>();

  private List<String> order = new ArrayList<>();

  public static void main(String[] args) {
    VoltageSpinnerCollection collection = new VoltageSpinnerCollection();
    collection.addVoltageControl("volts1", new VoltageSpinner(0.0, -100., 100., 0.1));
    collection.addVoltageControl("volts2", new VoltageSpinner(0.0, -100., 100., 0.1));
    JFrame frame = new JFrame();
    for(VoltageSpinner spinner : collection.getOrderedControls()){
      frame.add(spinner);
    }
    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public VoltageSpinnerCollection() {

  }

  public boolean addVoltageControl(String name, VoltageSpinner spinner)
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

  public List<VoltageSpinner> getOrderedControls(){
    List<VoltageSpinner> controls = new ArrayList<>();
    for(String name : order){
      controls.add(spinners.get(name));
    }
    return controls;
  }

  public VoltageSpinner getSpinner(String name){
    return spinners.get(name);
  }

  public List<String> getOrder() {
    return order;
  }
}
