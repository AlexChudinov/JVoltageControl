import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VoltagePropertiesCollection {

  private Map<String, VoltageSpinner> spinners = new HashMap<>();

  private List<String> order = new ArrayList<>();

  public VoltagePropertiesCollection() {

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
