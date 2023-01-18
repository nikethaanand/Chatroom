import java.io.IOException;
import org.junit.jupiter.api.Test;

public class ServerTest {

  @Test
  void test() throws InterruptedException{

    Thread serverTest = new Thread(() ->
    {
      try
      {
        Server.main(new String[]{});
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    });
    serverTest.start();

  }
}
