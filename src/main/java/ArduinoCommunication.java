import java.util.Arrays;
import java.util.Objects;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.logging.log4j.LogManager;

public class ArduinoCommunication {

  private SerialPort port;

  private static final byte[] TEST_BYTES = new byte[]{
      0x30, 0x00, (byte) 0xFF
  };

  private static final int MAX_TIME = 3000;

  private static final int WAIT_TIME_STEP = 1;

  public static void main(String[] args) {
    try {
      ArduinoCommunication arduinoCommunication = new ArduinoCommunication();
      arduinoCommunication.closePort();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public ArduinoCommunication()
      throws SerialPortException, InterruptedException {
    for (String s : SerialPortList.getPortNames()) {
      if (tryOpenPort(s) && testPort()) {
        break;
      } else {
        if (port.isOpened()) {
          closePort();
        }
      }
    }
  }

  private boolean testPort()
      throws SerialPortException, InterruptedException {
    LogManager.getRootLogger().info("Test bytes send to port.");
    writeBytes(TEST_BYTES);
    byte[] readBytes = readBytes();
    return Arrays.equals(readBytes, TEST_BYTES);
  }

  private void writeBytes(final byte[] bytes) throws SerialPortException {
    port.writeBytes(bytes);
    LogManager.getRootLogger().info(
        "Bytes written to port: " + bytesToReadableString(bytes));
  }

  private byte[] readBytes()
      throws SerialPortException, InterruptedException {
    byte[] readBytes = null;
    for (int i = 0; i < MAX_TIME && Objects.isNull(readBytes); ++i) {
      Thread.sleep(WAIT_TIME_STEP);
      readBytes = port.readBytes();
    }
    LogManager.getRootLogger().info(
        "Bytes read from port: " + bytesToReadableString(readBytes));
    return readBytes;
  }

  private boolean tryOpenPort(String name) {
    try {
      LogManager.getRootLogger().info(
          "Try to open serial port: " + name
      );
      port = new SerialPort(name);
      port.openPort();
      port.setParams(
          SerialPort.BAUDRATE_115200,
          SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1,
          SerialPort.PARITY_NONE);
      LogManager.getRootLogger().info(
          "Attempt to open serial port " + name + " succeeded."
      );
      return true;
    } catch (SerialPortException ex) {
      LogManager.getRootLogger().info(
          "Attempt to open serial port " + name + " failed."
      );
      return false;
    }
  }

  public void closePort() throws SerialPortException {
    if (port.isOpened()) {
      port.closePort();
    }
  }

  private String bytesToReadableString(final byte[] bytes) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b : bytes) {
      stringBuilder.append(b);
      stringBuilder.append(" ");
    }
    return stringBuilder.toString();
  }
}