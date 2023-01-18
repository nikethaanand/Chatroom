/**
 * @author divyadharshinimuruganandham  nikethaanand
 */
import java.io.IOException;
import java.util.Scanner;
import java.net.Socket;

/** Client class which has to be run after running Server
 * Class Client which ask for the username and starts receiveText and sendText files
 **/

public class Client {

  private static int serverPortNumber;
  private static String hostName;
  private  static String clientName;


  /**
   * main method produces the entry of the application with input args and calling the method for send text and receive text
   * @param args given input
   * @throws IOException throws IO exception
   */
  public static void main(String[] args) throws IllegalThreadStateException, IOException {
    try
    {
  hostName = args[0];
  serverPortNumber = Integer.parseInt(args[1]);
  System.out.println("Host " + hostName + " port Number " + serverPortNumber);
  System.out.println("Enter your Name: ");
  }
    catch (ArrayIndexOutOfBoundsException e)
    {
      System.err.println("Please pass hostname and Port Number via Args");
      System.exit(0);
    }

    Scanner scan = new Scanner(System.in);
    clientName = scan.nextLine();

    if(clientName.equals(null)||(clientName.equals("")))
    {
      System.out.println("ClientName is not Provided!");
      return;
    }

    Socket clientSocket=new Socket(hostName,serverPortNumber);

    Thread messageReceive = new Thread(new ReceiveText(clientSocket,clientName));
    Thread messageSend = new Thread(new SendText(clientSocket, clientName));
    messageReceive.start();
    messageSend.start();
  }
}
