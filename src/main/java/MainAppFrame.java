import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class MainAppFrame extends JFrame {

  private static final String APP_NAME = "Voltage Control";

  private JToolBar toolBar = new JToolBar();

  private JButton openFile = new JButton();
  private JFileChooser fileChooser = new JFileChooser();

  private AppParams params;

  MainAppFrame() throws Exception {
    super();
    setTitle(APP_NAME);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(toolBar, BorderLayout.PAGE_START);

    params = new AppParams();

    createButtons();
    getContentPane().add(params.getControlPane(), BorderLayout.CENTER);

    pack();
  }

  void createButtons(){
    openFile.addActionListener(this::openFileAction);
    toolBar.add(openFile);
  }

  private void openFileAction(ActionEvent actionEvent) {
    fileChooser.showOpenDialog(this);
  }

}
