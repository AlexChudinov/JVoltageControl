import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class VoltageCalibration {

  public static void main(String[] args) throws VoltageCalibrationException {
    TreeSet<ByteRepresentation> bytes = new TreeSet<>();
    bytes.add(ByteRepresentation.zero());
    bytes.add(ByteRepresentation.max());
    TreeSet<Double> doubles = new TreeSet<>();
    doubles.add(0.0);
    doubles.add(10.);
    VoltageCalibration calibration
        = new VoltageCalibration(bytes, doubles);
    ByteRepresentation byteRepresentation1
        = calibration.voltageToBytes(5.0);
    ByteRepresentation byteRepresentation2
        = calibration.voltageToBytes(5.1);
  }

  private static final Double MIN_DOUBLE_VALUE = 0.0;

  private static class VoltageCalibrationException extends Exception {

    public VoltageCalibrationException(String msg) {
      super(msg);
    }
  }

  private final TreeMap<Double, ByteRepresentation> calibrationTable
      = new TreeMap<>();

  public VoltageCalibration(
      TreeSet<ByteRepresentation> calibrationBytes,
      TreeSet<Double> calibrationValues
  ) throws VoltageCalibrationException {
    checkConstructorParams(calibrationBytes, calibrationValues);

    if (!calibrationBytes.first().equals(ByteRepresentation.zero())) {
      calibrationValues.add(MIN_DOUBLE_VALUE);
      calibrationBytes.add(ByteRepresentation.zero());
    }

    Iterator<Double> valueIterator = calibrationValues.iterator();
    Iterator<ByteRepresentation> byteIterator = calibrationBytes.iterator();

    ByteRepresentation firstByte = byteIterator.next();
    Double firstValue = valueIterator.next();

    double step = 0.0;
    while (byteIterator.hasNext()) {
      ByteRepresentation lastByte = byteIterator.next();
      Double lastValue = valueIterator.next();
      int nSteps = lastByte.compareTo(firstByte);
      step = (lastValue - firstValue) / nSteps;
      for (int i = 0; i < nSteps; ++i) {
        calibrationTable.put(firstValue, firstByte);
        firstValue += step;
        firstByte = firstByte.next();
      }
      firstByte = lastByte;
      firstValue = lastValue;
    }
    if (!firstByte.equals(ByteRepresentation.max())) {
      int nSteps = ByteRepresentation.max().compareTo(firstByte);
      for (int i = 0; i < nSteps; ++i) {
        calibrationTable.put(firstValue += step, firstByte);
        firstByte = firstByte.next();
      }
    }
  }

  private void checkConstructorParams(
      TreeSet<ByteRepresentation> calibrationBytes,
      TreeSet<Double> calibrationValues
  ) throws VoltageCalibrationException {
    if (calibrationBytes.size() != calibrationValues.size()) {
      throw new VoltageCalibrationException(
          "Different number of calibration bytes and values");
    }
    if (calibrationBytes.size() < 2) {
      throw new VoltageCalibrationException(
          "Smaller than two values for calibration");
    }
    if (calibrationValues.first() < MIN_DOUBLE_VALUE) {
      throw new VoltageCalibrationException(
          "Double value for calibration can not be smaller than "
              + MIN_DOUBLE_VALUE);
    }
    if (calibrationBytes.last().compareTo(ByteRepresentation.max()) > 0) {
      throw new VoltageCalibrationException(
          "Byte value "
              + calibrationBytes.last()
              + " is not a possible value"
      );
    }
  }

  ByteRepresentation voltageToBytes(double val) {
    double minVal = calibrationTable.floorKey(val);
    double maxVal = calibrationTable.ceilingKey(val);
    if (val - minVal > maxVal - val) {
      return calibrationTable.get(maxVal);
    } else {
      return calibrationTable.get(minVal);
    }
  }

  public static class ByteRepresentation implements
      Comparable<ByteRepresentation>,
      Iterator<ByteRepresentation> {

    private static final int SIZE = 2;

    private static final int SHORT_MASK = 0xFFFF;

    private static ByteRepresentation maxBytes;

    private static ByteRepresentation zeroBytes;

    static {
      try {
        maxBytes = new ByteRepresentation(new byte[]{
            (byte) 0xFF, (byte) 0xFF
        });
        zeroBytes = new ByteRepresentation(new byte[]{
            (byte) 0x00, (byte) 0x00
        });
      } catch (VoltageCalibrationException e) {
        e.printStackTrace();
      }
    }

    private final byte[] bytes;

    public ByteRepresentation(byte[] bytes)
        throws VoltageCalibrationException {
      if (bytes.length != SIZE) {
        throw new VoltageCalibrationException("Bad byte representation");
      }
      this.bytes = bytes;
    }

    static public ByteRepresentation zero() {
        return zeroBytes;
    }

    static public ByteRepresentation max() {
        return maxBytes;
    }

    @Override
    public int compareTo(ByteRepresentation other) {
      ByteBuffer thisBuff = ByteBuffer.wrap(bytes);
      ByteBuffer otherBuff = ByteBuffer.wrap(other.bytes);
      return (thisBuff.getShort() & SHORT_MASK)
          - (otherBuff.getShort() & SHORT_MASK);
    }

    @Override
    public boolean hasNext() {
      return (ByteBuffer.wrap(bytes).getShort() & SHORT_MASK) < SHORT_MASK;
    }

    @Override
    public ByteRepresentation next() {
      if (!hasNext()) {
        throw new ArrayIndexOutOfBoundsException();
      }
      ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
      int nextVal = (int) byteBuffer.getShort() + 1;
      byte[] newBytes = new byte[SIZE];
      ByteBuffer.wrap(newBytes).putShort((short) (nextVal & SHORT_MASK));

      try {
        return new ByteRepresentation(newBytes);
      } catch (VoltageCalibrationException e) {
        return null;
      }
    }

    public boolean equals(ByteRepresentation other) {
      return Arrays.equals(bytes, other.bytes);
    }
  }
}
