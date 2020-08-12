import java.util.List;
import junit.framework.TestCase;

public class ArduinoCommunicationTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testGetInstance() {
    try (
        ArduinoCommunication communication = ArduinoCommunication.getInstance()
    ) {
      byte[] bytesIn = new byte[]{0x30, 0x00, (byte) 0xff};
      communication.writeBytes(bytesIn);
      List<Byte> bytesOut = communication.readBytes(bytesIn.length);
      int byteCounter = 0;
      for (byte b : bytesOut) {
        System.out.println("Byte #" + byteCounter + ": " + b);
        assertEquals(b, bytesIn[byteCounter]);
        byteCounter++;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
