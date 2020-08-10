import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class BlockedMainThread {

  private static final Object lock = new Object();

  public static void blockMain(JFrame frame)
      throws InterruptedException {

    frame.setVisible(true);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    Thread t = new Thread(() -> {
      synchronized (lock) {
        while (frame.isVisible()) {
          try {
            lock.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });

    t.start();
    frame.addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent arg0) {
        synchronized (lock) {
          frame.setVisible(false);
          lock.notify();
        }
      }

    });

    t.join();
  }
}
