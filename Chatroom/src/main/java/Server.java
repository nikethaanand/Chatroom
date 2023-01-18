/**
 * @author divyadharshinimuruganandham  nikethaanand
 */

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

/**
 * Server class is the class which has the server port information, accepts the client request and starts the Chat Channel.
 */
public class Server extends Thread {


  /**
   * main method for declaring ports, receiving inputs and validating args from the command line
   * @param args which is provided in the command line
   * @throws IOException IOException
   */

  public static void main(String[] args) throws IOException {
      int connectingPortNumber = 6464;
      ServerSocket socket = new ServerSocket(connectingPortNumber);
      System.out.println("Port Number "+socket.getLocalPort()+" Inet Address "+socket.getInetAddress());
      while (true) {
        Socket newClient = socket.accept();
        System.out.println("Request received from the Client: " + newClient.getInetAddress().getHostName());
        System.out.println("Creating a channel for the recent Client.");
        GenerateChat clientChannel = new GenerateChat(newClient);
        GenerateChat.chatList.add(clientChannel);
        new Thread(clientChannel).start();
        System.out.println("There are " + GenerateChat.chatList.size() + " users in total.");
      }
  }
}
