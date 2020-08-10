import java.util.List;
import java.util.TreeSet;
import junit.framework.TestCase;

public class VoltageTest extends TestCase {

  public void testSetValue() {
    try (ArduinoCommunication comm = ArduinoCommunication.getInstance()){
      TreeSet<Double> voltageCalibrationValues = new TreeSet<Double>() {{
        add(0.0);
        add(10.0);
      }};

      TreeSet<Integer> voltageByteValues = new TreeSet<Integer>() {{
        add(0x0000);
        add(0xFFFF);
      }};

      Voltage voltage = new Voltage(
          (byte)0x30,
          5.,
          voltageByteValues,
          voltageCalibrationValues
      );

      List<Byte> bytesRead = comm.readBytes(3);
      int byteCounter = 0;
      for(byte b : bytesRead){
        System.out.println("Byte read from port "
            + (byteCounter++) + ":"
            + Integer.toHexString(Byte.toUnsignedInt(b)));
      }
    }
    catch (Exception ex){
      ex.printStackTrace();
    }
  }

}
