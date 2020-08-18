import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import junit.framework.TestCase;

public class AppParamsTest extends TestCase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  public void testGetControlPanel() {
    try {
      AppParams params = new AppParams();
      JFrame frame = new JFrame();
      frame.add(params.getControlPane());
      frame.pack();
      BlockedMainThread.blockMain(frame);
      ArduinoCommunication.getInstance().close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void testInitVoltages(){
    try {
      AppParams params = new AppParams();
      params.initVoltages(10);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void testConstructor() {
    try {
      AppParams params = new AppParams();
      System.out.println(params);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }
}
