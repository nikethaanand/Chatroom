import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

public class ClientTest {

  @Test
  void test() throws IllegalThreadStateException, InterruptedException {
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
    Thread.sleep(1000);
    new Thread(() -> {
    String input = "a\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);
    String[] args = new String[]{"localhost", "8888"};
    try
    {
      Client.main(args);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
   }).start();

    serverTest.interrupt();
  }
}
