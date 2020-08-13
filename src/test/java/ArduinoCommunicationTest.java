import java.util.Arrays;
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
      byte[] bytesOut = communication.readBytes(bytesIn.length);
      assertTrue(Arrays.equals(bytesOut, bytesIn));
      System.out.println("bytesOut = " + Arrays.toString(bytesOut));
    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
