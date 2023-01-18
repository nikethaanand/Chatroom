import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;


public class GenerateChatTest {

  private static Socket receiverName;
  private static ServerSocket serverName;
  private static DataOutputStream outputStream;
  private static DataInputStream inputStream;
  private static GenerateChat generateChat;
  private static Socket connectedClient;

  GenerateChat user = new GenerateChat();


  @BeforeAll
  static void setUp() throws IOException {
    serverName = new ServerSocket(6565);
    connectedClient = new Socket("localhost", 6565);
    receiverName = serverName.accept();
    outputStream = new DataOutputStream(connectedClient.getOutputStream());
    inputStream = new DataInputStream(connectedClient.getInputStream());
    byte[] usernameBytes = "usernameTest".getBytes();
    outputStream.writeInt(MessageIdentifier.CONNECT_MESSAGE);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.flush();
    generateChat = new GenerateChat(receiverName);
    GenerateChat.chatList.add(generateChat);
    new Thread(generateChat).start();
  }

  @Test
  void testGenerateConnectionDisconnect() throws IOException, InterruptedException {
    byte[] usernameBytes = "usernameTest".getBytes();
    outputStream.writeInt(MessageIdentifier.DISCONNECT_MESSAGE);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.flush();
    Thread.sleep(100);
  }

  @Test
  void testGenerateConnectionConnect() throws IOException, InterruptedException {
    byte[] usernameBytes = "usernameTest".getBytes();
    outputStream.writeInt(MessageIdentifier.CONNECT_RESPONSE);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.flush();
    Thread.sleep(100);
  }

  @Test
  void testGenerateDirectMessage() throws IOException {

    byte[] usernameBytes = "usernameTest".getBytes();
    outputStream.writeInt(MessageIdentifier.DIRECT_MESSAGE);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.flush();
  }


  @Test
  void testGenerateBroadcastMessage() throws IOException {

    byte[] usernameBytes = "usernameTest".getBytes();
    outputStream.writeInt(MessageIdentifier.BROADCAST_MESSAGE);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.flush();
  }

  @Test
  void testGenerateFailureMessage() throws IOException{

    byte[] usernameBytes = "usernameTest".getBytes();
    outputStream.writeInt(MessageIdentifier.FAILED_MESSAGE);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.flush();
  }


  @Test
  void testGenerateQueryMessage() throws IOException, InterruptedException {

    byte[] usernameBytes = "usernameTest".getBytes();
    outputStream.writeInt(MessageIdentifier.QUERY_CONNECTED_USERS);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.flush();
    Thread.sleep(100);
  }

  @Test
  void testGenerateInsultMessage() throws IOException {

    byte[] usernameBytes = "usernameTest".getBytes();
    outputStream.writeInt(MessageIdentifier.SEND_INSULT);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.writeInt(usernameBytes.length);
    outputStream.write(usernameBytes);
    outputStream.flush();
  }

  @Test
  void testToString() {
    GenerateChat userInputTest = new GenerateChat();
    Assertions.assertNotEquals(user, userInputTest);
    Assertions.assertEquals(userInputTest.toString(), user.toString());
  }

  @Test
  void testToHashcode(){
    GenerateChat userInputTest = new GenerateChat();
    Assertions.assertNotEquals(userInputTest.hashCode(), user.hashCode());
  }

  @Test
  void testEquals(){
    GenerateChat userInputTest = new GenerateChat();
    Assertions.assertNotEquals(userInputTest, user);
  }


  @AfterAll
  static void shutdown() throws IOException {
    connectedClient.close();
    serverName.close();
    receiverName.close();
    outputStream.close();
    inputStream.close();
  }
}
