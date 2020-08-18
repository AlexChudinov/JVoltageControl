import java.util.ArrayList;
import java.util.Arrays;
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

  private static final int MAX_TIME_MS = 3000;

  private static final int WAIT_TIME_STEP_MS = 1;

  private static final int TRANSMISSION_TIME_DELAY_TS = 0;

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

  public static ArduinoCommunication getInstance(String comPortName)
      throws SerialPortException{
    if(!Objects.isNull(communication)){
      if(!communication.port.getPortName().equals(comPortName)){
        communication.closePort();
      } else {
        return communication;
      }
    }
    communication = new ArduinoCommunication(comPortName);
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

  private ArduinoCommunication(String comPortName){
    tryOpenPort(comPortName);
  }

  private boolean testPort()
      throws SerialPortException, InterruptedException {
    LogManager.getRootLogger().info("Test bytes send to port.");
    writeBytes(TEST_BYTES);
    return Arrays.equals(readBytes(TEST_BYTES.length), TEST_BYTES);
  }

  public void writeBytes(final byte[] bytes)
      throws SerialPortException, InterruptedException {
    for(byte b : bytes){
      writeByte(b);
    }
    Thread.sleep(WAIT_TIME_STEP_MS * TRANSMISSION_TIME_DELAY_TS);
    LogManager.getRootLogger().info(
        "Bytes written to port: " + bytesToReadableString(bytes));
    LogManager.getRootLogger().info(
        "Bytes in output buffer: " + port.getOutputBufferBytesCount());
  }

  private void writeByte(byte b)
      throws SerialPortException, InterruptedException {
    port.writeByte(b);
    Thread.sleep(WAIT_TIME_STEP_MS);
    for (int i = 0; i < MAX_TIME_MS && port.getOutputBufferBytesCount() != 0; ++i) {
      Thread.sleep(WAIT_TIME_STEP_MS);
    }
  }

  public byte[] readBytes(int nBytes)
      throws SerialPortException, InterruptedException {
    List<Byte> bytesReadList = new ArrayList<>();
    for(int i = 0; i < MAX_TIME_MS && bytesReadList.size() != nBytes; ++i){
      byte[] bytesRead = port.readBytes();
      if(Objects.isNull(bytesRead)){
        continue;
      }
      for(byte b : bytesRead){
        bytesReadList.add(b);
      }
      Thread.sleep(WAIT_TIME_STEP_MS);
    }
    byte[] bytesRead = new byte[bytesReadList.size()];
    for(int i = 0; i < bytesRead.length; ++i){
      bytesRead[i] = bytesReadList.get(i);
    }
    LogManager.getRootLogger().info(
        "Bytes read from port: " + bytesToReadableString(bytesRead));
    return bytesRead;
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
      stringBuilder.append(Integer.toHexString(b & 0xFF));
      stringBuilder.append(" ");
    }
    return stringBuilder.toString();
  }

  @Override
  public void close() throws Exception {
    if (Objects.isNull(communication)) {
      return;
    }
    communication.closePort();
    communication = null;
    LogManager.getRootLogger().info("Port closed");
  }

  public String portName(){
    return port.getPortName();
  }
}