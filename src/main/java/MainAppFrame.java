import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.apache.logging.log4j.LogManager;

public class MainAppFrame extends JFrame {

  private static final int SWITCH_TIME_DELAY = 20;
  private static final String APP_NAME = "Voltage Control";
  private static final int TOOLBAR_ICON_SIZE = 32;

  private static final String FILE_OPEN_ICON = "icons/open.png";
  private static final String FILE_SAVE_ICON = "icons/save.png";
  private static final String START_ICON = "icons/start.jpg";
  private static final String STOP_ICON = "icons/stop.jpg";

  private static final ArrayList<String> FIG_FILES =
      new ArrayList<String>() {{
        add(FILE_OPEN_ICON);
        add(FILE_SAVE_ICON);
        add(START_ICON);
        add(STOP_ICON);
      }};

  private static Image openImage;
  private static Image saveImage;
  private static Image startImage;
  private static Image stopImage;

  static {
    try {
      openImage = loadToolBarImage(FILE_OPEN_ICON);

      saveImage = loadToolBarImage(FILE_SAVE_ICON);

      startImage = loadToolBarImage(START_ICON);

      stopImage = loadToolBarImage(STOP_ICON);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Image loadToolBarImage(String fileName)
      throws IOException {
    return ImageIO
        .read(new File(fileName))
        .getScaledInstance(
            TOOLBAR_ICON_SIZE,
            TOOLBAR_ICON_SIZE,
            Image.SCALE_SMOOTH);
  }

  private ImageIcon fileOpenIcon;
  private ImageIcon fileSaveIcon;

  private ImageIcon startIcon;
  private ImageIcon stopIcon;

  private JToolBar toolBar = new JToolBar();

  private JButton openFile = new JButton();
  private JButton saveFile = new JButton();
  private JFileChooser fileChooser = new JFileChooser();

  private JButton startStopVoltages = new JButton();
  private boolean isVoltagesRunning = false;

  private AppParams params;

  MainAppFrame() throws Exception {
    super();
    setTitle(APP_NAME);

    setIconImage(ImageIO.read(new File("icons/app_icon.jpg")));

    params = new AppParams();
    createButtons();

    setInfoContent();
  }

  private void setControlContent() {
    setContent(params.getControlPane());
  }

  private void setInfoContent() {
    setContent(params.getInfoPane());
  }

  private void setContent(JPanel panel) {
    getContentPane().removeAll();
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(toolBar, BorderLayout.PAGE_START);
    getContentPane().add(panel, BorderLayout.CENTER);
    pack();
  }

  void createButtons() {
    loadAndScaleIcons();
    openFile.setIcon(fileOpenIcon);
    openFile.addActionListener(this::openFileAction);
    toolBar.add(openFile);

    saveFile.setIcon(fileSaveIcon);
    saveFile.addActionListener(this::saveFileAction);
    toolBar.add(saveFile);

    startStopVoltages.setIcon(startIcon);
    startStopVoltages.addActionListener(this::startStopVoltages);
    toolBar.add(startStopVoltages);
  }

  private void loadAndScaleIcons() {
    fileOpenIcon = new ImageIcon(openImage);

    fileSaveIcon = new ImageIcon(saveImage);

    startIcon = new ImageIcon(startImage);

    stopIcon = new ImageIcon(stopImage);
  }

  private void openFileAction(ActionEvent actionEvent) {
    try {
      int approve = fileChooser.showOpenDialog(this);
      if (approve == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        LogManager.getRootLogger().info(
            "Voltage params file load: "
                + file.getAbsolutePath()
        );
        if (isVoltagesRunning) {
          stop();
        }
        params.load(file.getAbsolutePath());
        setInfoContent();
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage());
    }
  }

  private void saveFileAction(ActionEvent actionEvent) {
    try {
      int approve = fileChooser.showSaveDialog(this);
      if (approve == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        LogManager.getRootLogger().info(
            "Voltage params file save: "
                + file.getAbsolutePath()
        );
        params.save(file.getAbsolutePath());
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage());
    }
  }

  private void startStopVoltages(ActionEvent actionEvent) {
    try {
      if (!isVoltagesRunning) {
        start();
      } else {
        stop();
      }
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage());
    }
  }

  private void start() throws Exception {
    startStopVoltages.setIcon(stopIcon);
    params.initVoltages(SWITCH_TIME_DELAY);
    setControlContent();
    isVoltagesRunning = true;
  }

  private void stop() throws Exception {
    startStopVoltages.setIcon(startIcon);
    params.stopVoltages(SWITCH_TIME_DELAY);
    setInfoContent();
    isVoltagesRunning = false;
  }
}
