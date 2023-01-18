import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class SendTextTest {

  private ServerSocket serverName;
  private Socket connectedClient0;
  private Socket connectedClient1;

  SendText user = new SendText();

  @BeforeEach
  void setup() throws IOException, InterruptedException {
    serverName = new ServerSocket(7474);
    Thread.sleep(100);
    connectedClient0 = new Socket("localhost",7474);
    connectedClient1 = new Socket("localhost",7474);
  }

  @Test
  void testSendDirectMessage() throws InterruptedException {
    String messageValue = "@test Hello World!\n";
    InputStream inputBuffer = new ByteArrayInputStream(messageValue.getBytes());
    System.setIn(inputBuffer);
    SendText sendValue = new SendText(connectedClient0, "x");
    new Thread(sendValue).start();
    Thread.sleep(100);
  }

  @Test
  void testSendBroadcastMessage() throws InterruptedException {
    String value = "@all How are you\n";
    InputStream inputBufferValue = new ByteArrayInputStream(value.getBytes());
    System.setIn(inputBufferValue);
    SendText sendValue = new SendText(connectedClient0, "x");
    new Thread(sendValue).start();
    Thread.sleep(100);
  }


  @Test
  void testSendInsultMessage() throws InterruptedException {
    String messageValue = "!test\n";
    InputStream inputBuffer = new ByteArrayInputStream(messageValue.getBytes());
    System.setIn(inputBuffer);
    SendText sendValue = new SendText(connectedClient0, "x");
    new Thread(sendValue).start();
    Thread.sleep(100);
  }


  @Test
  void testUserOptionMenu() throws InterruptedException {
    String value = "?\n";
    InputStream inputBuffer = new ByteArrayInputStream(value.getBytes());
    System.setIn(inputBuffer);
    SendText sendValue = new SendText(connectedClient0, "x");
    new Thread(sendValue).start();
    Thread.sleep(100);
  }

  @Test
  void testAskQuery() throws InterruptedException {
    String query = "who\n";
    InputStream inputBuffer = new ByteArrayInputStream(query.getBytes());
    System.setIn(inputBuffer);
    SendText value = new SendText(connectedClient0, "x");
    new Thread(value).start();
    Thread.sleep(100);
  }

  @Test
  void testToString() {
    SendText userInputTest = new SendText();
    Assertions.assertNotEquals(user, userInputTest);
    Assertions.assertEquals(userInputTest.toString(), user.toString());
  }

  @Test
  void testToHashcode(){
    SendText userInputTest = new SendText();
    Assertions.assertNotEquals(userInputTest.hashCode(), user.hashCode());
  }

  @Test
  void testEquals(){
    SendText userInputTest = new SendText();
    Assertions.assertNotEquals(userInputTest, user);
  }

  @AfterEach
  void close() throws IOException {
    connectedClient0.close();
    connectedClient1.close();
    serverName.close();
  }
}
