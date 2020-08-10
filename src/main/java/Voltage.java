import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import jssc.SerialPortException;

public class Voltage {

  private static final int MAX_BYTE = 0xFFFF;
  private static final int MIN_BYTE = 0x0000;
  private static final double MIN_VOLTS = 0.0;
  private static final int COMMUNICATION_BYTES_SIZE = 3;

  private double value;

  private TreeMap<Double, Integer> calibrationTable;

  private ArduinoCommunication communication;
  private byte address;

  public class VoltageException extends Exception {

    public VoltageException(String msg) {
      super(msg);
    }
  }

  public Voltage(
      byte address,
      double value,
      TreeSet<Integer> calTabBytes,
      TreeSet<Double> calTabValues)
      throws Exception {
    communication = ArduinoCommunication.getInstance();
    this.address = address;
    calibrationTable = new TreeMap<>();
    setCalibrationTable(calTabValues, calTabBytes);
    setValue(value);
  }

  private void checkTableEntries(
      TreeSet<Integer> calTabBytes,
      TreeSet<Double> calTabValues)
      throws VoltageException {
    if (calTabBytes.size() != calTabValues.size()) {
      throw new VoltageException("Calibration values mismatch");
    }
    if (calTabBytes.first() < MIN_BYTE || calTabBytes.last() > MAX_BYTE) {
      throw new VoltageException("Calibration bytes out of bounds");
    }
    if (calTabValues.first() < MIN_VOLTS) {
      throw new VoltageException("Calibration volts out of bounds");
    }
  }

  private void setCalibrationTable(
      TreeSet<Double> calTabValues,
      TreeSet<Integer> calTabBytes)
      throws VoltageException {
    checkTableEntries(calTabBytes, calTabValues);
    calibrationTable.clear();
    Iterator<Double> valueIterator = calTabValues.iterator();
    Iterator<Integer> byteIterator = calTabBytes.iterator();
    while (valueIterator.hasNext()) {
      calibrationTable.put(valueIterator.next(), byteIterator.next());
    }
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) throws SerialPortException {
    this.value = value;
    communication.writeBytes(valueToBytes());
  }

  private byte[] valueToBytes() {
    byte[] bytes = new byte[COMMUNICATION_BYTES_SIZE];

    double minValue = calibrationTable.floorKey(value);
    double maxValue = calibrationTable.ceilingKey(value);
    long intValue;

    if(maxValue == minValue){
      intValue = calibrationTable.get(value);
    } else {
      int minByte = calibrationTable.get(minValue);
      int maxByte = calibrationTable.get(maxValue);

      intValue = Math.round(
          (maxByte - minByte) * value / (maxValue - minValue) + minByte);
    }


    ByteBuffer.wrap(bytes).put(address);
    ByteBuffer
        .wrap(bytes)
        .order(ByteOrder.BIG_ENDIAN)
        .putShort(1, (short) (intValue));

    return bytes;
  }


}
