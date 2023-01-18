import ExceptionHandling.ChatTypeException;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

/**
 * class SendText handles all the supported message as per the response
 */
public class SendText extends Thread{

  private boolean active;
  BufferedReader br;
  DataOutputStream outputStream;

  private int five=5;
  private byte[] userNameBytes;
  private String user;
  private String signIn="signin";

  /**SendText constructor
   * Sets active to true and calls sendMessage
   * @param socket socket
   * @param username username of user who has logged in
   */
  public SendText(Socket socket,String username)  {
    try {
      br = new BufferedReader (new InputStreamReader(System.in));
      outputStream=new DataOutputStream(socket.getOutputStream());
      this.user =username;
      active=true;
      userNameBytes =username.getBytes();
      sendMessage(signIn);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    catch (ChatTypeException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Defined constructor to test the override methods of the class
   */
  public SendText(){}


  /**run method gets user input from user via readUserOption and calls sendMessage()
   *
   */
  public void run(){
    while(active)
    {
      try {
        Thread.sleep(100);
        System.out.println("Enter a message option");
        String userOption= br.readLine();
        sendMessage(userOption);
      }
      catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
      catch (ChatTypeException e) {
        throw new RuntimeException(e);
      }

    }
  }


  /**
   * sendMessage checks the user input entered and calls respective functions
   * @param userOption user entered command
   * @throws IOException IOException
   * @throws ChatTypeException ChatroomException
   */
  public void sendMessage(String userOption) throws IOException,ChatTypeException {
    userOption=userOption.trim();
    if(userOption.equals("?"))
    {
      showOptions();
    }
    else if(userOption.equalsIgnoreCase("signIn"))
    {
      signIn(userOption);
    }
    else if(userOption.equalsIgnoreCase("logoff"))
    {
      logoff(userOption);
    }
    else if(userOption.equalsIgnoreCase("who"))
    {
      connectedUser(userOption);
    }
    else if(userOption.startsWith("@all"))
    {
      sendBroadcast(userOption);
    }
    else if(userOption.startsWith("@"))
    {
      sendToClient(userOption);
    }
    else if(userOption.startsWith("!"))
    {
      sendInsultGrammarText(userOption);
    }

    else {
      System.out.println("Unknown Command");
    }
  }

  /**
   * showOptions prints the messages that can be entered by the user
   */
  public void showOptions()
  {
    System.out.println("• logoff: sends a DISCONNECT_MESSAGE to the server"+ '\n'+
                           "• who: sends a QUERY_CONNECTED_USERS to the server"+'\n'+
                           "• @user: sends a DIRECT_MESSAGE to the specified user to the server"+'\n'+
                           "• @all: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected"+'\n'+
                           "• !user: sends a SEND_INSULT message to the server, to be sent to the specified user");
  }

  /**
   * signIn writes user details to the DataOutputStream and handles error
   * @param input message
   * @throws ChatTypeException ChatroomException
   */
  public void signIn(String input)throws ChatTypeException
  {
    int t=MessageIdentifier.CONNECT_MESSAGE;
    try
    {
      outputStream.writeInt(t);
      int userNameLength = userNameBytes.length;
      outputStream.writeInt(userNameLength);
      outputStream.write(userNameBytes);
      outputStream.flush();
    }
    catch (Exception e)
    {
      active=false;
      shutdown();
      String error= e.getMessage();
      throw new ChatTypeException(error);
    }
  }
  /**
   * logoff writes userDetails to the DataOutputStream and handles exception
   * @param input message
   * @throws ChatTypeException ChatroomException
   */
  public void logoff(String input) throws ChatTypeException
  {
    int t=MessageIdentifier.DISCONNECT_MESSAGE;
    try
    {
      outputStream.writeInt(t);
      int userNameLength = userNameBytes.length;
      outputStream.writeInt(userNameLength);
      outputStream.write(userNameBytes);
      outputStream.flush();
      active=false;
    }catch (Exception e)
    {
      active=false;
      shutdown();
      String error= e.getMessage();
      throw new ChatTypeException(error);
    }

  }

  /**
   * connectedUser writes userdetails to the DataOutputStream and handles exception
   * @param input message
   * @throws ChatTypeException ChatroomException
   */
  public void connectedUser(String input) throws ChatTypeException {
    int t=MessageIdentifier.QUERY_CONNECTED_USERS;
    try {
      outputStream.writeInt(t);
      int userNameLength = userNameBytes.length;
      outputStream.writeInt(userNameLength);
      outputStream.write(userNameBytes);
      outputStream.flush();
    }catch ( Exception e)
    {
      active=false;
      shutdown();
      String error= e.getMessage();
      throw new ChatTypeException(error);
    }


  }
  /**
   * sendBroadcast writes sender and message details to the DataOutputStream and handles exception
   * @param input message
   * @throws ChatTypeException ChatroomException
   */
  public void sendBroadcast(String input) throws ChatTypeException {
    try{
    int type = MessageIdentifier.BROADCAST_MESSAGE;
    int index=input.indexOf("@all")+five;
    String message=input.substring(index).trim();
    byte[] messageBytes = message.getBytes();

    int nameLength = userNameBytes.length;
    int messageLength = messageBytes.length;
    outputStream.writeInt(type);
    outputStream.writeInt(nameLength);
    outputStream.write(userNameBytes);
    outputStream.writeInt(messageLength);
    outputStream.write(messageBytes);
    outputStream.flush();

    System.out.println("\""+message+"\"" +" sent to all connected clients.");
    }  catch (Exception e)
    {
      active=false;
      shutdown();
      String error= "Enter a message";
      throw new ChatTypeException(error);
    }
  }
  /**
   * sendToClient writes sender,receiver and message details to the DataOutputStream and handles exception
   * @param input message
   * @throws ChatTypeException ChatroomException
   */
  public void sendToClient(String input) throws ChatTypeException{
    int type = MessageIdentifier.DIRECT_MESSAGE;
    int space = input.indexOf(" ");
    String userName = input.substring(input.indexOf("@")+1,space);
    String message = input.substring(space).trim();
    byte[] nameBytes = userName.getBytes();
    byte[] messageBytes = message.getBytes();
    int receiverUserLength = nameBytes.length;
    int senderUserNameSize = userNameBytes.length;
    int messageSize = messageBytes.length;
    try{
    outputStream.writeInt(type);
    outputStream.writeInt(senderUserNameSize);
    outputStream.write(userNameBytes);
    outputStream.writeInt(receiverUserLength);
    outputStream.write(nameBytes);
    outputStream.writeInt(messageSize);
    outputStream.write(messageBytes);
    outputStream.flush();
    System.out.println("\""+message+"\" "+"is sent to user "+"\""+user+"\"");

    }
    catch (Exception e)
    {
      active=false;
      shutdown();
      String error= "Enter a message along with username";

      throw new ChatTypeException(error);
    }
  }

  /**
   * sendInsultGrammarText is called and message,receiver details are written to the DataOutputStream
   * @param val message
   * @throws ChatTypeException ChatroomException
   */

  public void sendInsultGrammarText(String val) throws ChatTypeException{
    try {
      int type = MessageIdentifier.SEND_INSULT;
      String to = val.substring(val.indexOf("!")+1).trim();
      if(to==null || to.length()==0)
      {
        System.out.println("Enter a username");
      }
      else {
        byte[] receiverUserName = to.getBytes();
        int receiverUserNameLength = receiverUserName.length;
        int senderUserNameLength = userNameBytes.length;
        outputStream.writeInt(type);
        outputStream.writeInt(senderUserNameLength);
        outputStream.write(userNameBytes);
        outputStream.writeInt(receiverUserNameLength);
        outputStream.write(receiverUserName);
        outputStream.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
      active = false;
      shutdown();
      throw new ChatTypeException("Incorrect format");
    }
  }

  /**
   * close method called in case of exception,it closes BufferReader and DataOutputStream
   */
  public void shutdown(){
    try {
      outputStream.close();
      br.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * toString method
   * @return String
   */
  @Override
  public String toString() {
    return "SendTextMessage{}";
  }


  /**
   * override hashcode method
   * @return hashcode method
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * override equals method
   * @param obj equals object
   * @return returns equals boolean value
   */
  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

}
