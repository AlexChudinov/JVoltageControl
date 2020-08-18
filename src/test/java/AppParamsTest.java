import junit.framework.TestCase;

public class AppParamsTest extends TestCase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  public void testConstructor(){
    try {
      AppParams params = new AppParams();
      System.out.println(params);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }
}
