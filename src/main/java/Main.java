import javax.swing.WindowConstants;

public class Main {



  public static void main(String[] args) {
    try {
      MainAppFrame frame = new MainAppFrame();
      frame.setVisible(true);
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
