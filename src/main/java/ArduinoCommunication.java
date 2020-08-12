import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.apache.logging.log4j.LogManager;

public class ArduinoCommunication implements AutoCloseable {

  private SerialPort port;

  private static ArduinoCommunication communication = null;

  private static final byte[] TEST_BYTES = new byte[]{
      0x30, 0x00, 0x00
  };

  private static final int MAX_TIME = 3000;

  private static final int WAIT_TIME_STEP = 1;

  public static ArduinoCommunication getInstance()
      throws SerialPortException, InterruptedException {
    if (Objects.isNull(communication)) {
      communication = new ArduinoCommunication();
      if (Objects.isNull(communication.port)
          || !communication.port.isOpened()) {
        communication = null;
      }
    }
    return communication;
  }

  private ArduinoCommunication()
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
    List<Byte> bytesRead = readBytes(TEST_BYTES.length);
    if (bytesRead.size() != TEST_BYTES.length) {
      return false;
    } else {
      boolean result = true;
      for (int i = 0; i < bytesRead.size(); ++i) {
        result &= (bytesRead.get(i) == TEST_BYTES[i]);
      }
      return result;
    }
  }

  public void writeBytes(final byte[] bytes) throws SerialPortException {
    port.writeBytes(bytes);
    System.out.println("bytes=" + port.getOutputBufferBytesCount());
    LogManager.getRootLogger().info(
        "Bytes written to port: " + bytesToReadableString(bytes));
  }

  public List<Byte> readBytes(int nBytes)
      throws SerialPortException, InterruptedException {
    List<Byte> byteList = new LinkedList<>();
    for (int i = 0; i < MAX_TIME && byteList.size() != nBytes; ++i) {
      Thread.sleep(WAIT_TIME_STEP);
      byte[] readBytes = port.readBytes();
      if (Objects.isNull(readBytes)) {
        continue;
      }
      for (byte b : readBytes) {
        byteList.add(b);
      }
    }
    LogManager.getRootLogger().info(
        "Bytes read from port: " + bytesToReadableString(byteList));
    return byteList;
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

  private void closePort() throws SerialPortException {
    if (port.isOpened()) {
      port.closePort();
    }
  }

  private String bytesToReadableString(final List<Byte> bytes) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b : bytes) {
      stringBuilder.append(b);
      stringBuilder.append(" ");
    }
    return stringBuilder.toString();
  }

  private String bytesToReadableString(final byte[] bytes) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b : bytes) {
      stringBuilder.append(b);
      stringBuilder.append(" ");
    }
    return stringBuilder.toString();
  }

  @Override
  public void close() throws Exception {
    if(Objects.isNull(communication)){
      return;
    }
    communication.closePort();
    communication = null;
    LogManager.getRootLogger().info("Port closed");
  }
}