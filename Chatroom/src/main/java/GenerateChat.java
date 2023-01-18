/**
 * @author divyadharshinimuruganandham  nikethaanand
 */
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import Exceptions.UndefinedInputException;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.simple.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.*;
import org.json.simple.parser.ParseException;

/**
 * class GenerateChat is the chat handler between SendText and ReceiverText
 */
public class GenerateChat extends Thread{

  private boolean isActive;
  private String userName;

  DataOutputStream outputStream;
  DataInputStream inputStream;
  private Socket client;
  /**
   * chatList is a public List which stores chat information
   */
  public static List<GenerateChat> chatList = new CopyOnWriteArrayList<>();

  /**GenerateChat constructor
   * Sets to active once the GenerateChat is called
   * @param client client
   */
  public GenerateChat(Socket client) {
    try {
      isActive = true;
      this.client = client;
      outputStream = new DataOutputStream(client.getOutputStream());
      inputStream = new DataInputStream(client.getInputStream());
      chatResponse();
    } catch (IOException e) {
      close();
      isActive = false;
      throw new RuntimeException(e);
    }
  }

  /**
   * run method which calls the chatResponse function
   */
  @Override
  public void run(){
    while(isActive){
      chatResponse();
    }
  }

  /**
   * Defined constructor to test the override methods of the class
   */
  public GenerateChat(){}

  /** chatResponse method calls respective functions based on messageType
   *
   */
  private  void chatResponse() {
    try {
      int messageType=inputStream.readInt();
      System.out.println(messageType);
      if(messageType== MessageIdentifier.CONNECT_MESSAGE)
      {
        connect();
      }
      else if(messageType== MessageIdentifier.DISCONNECT_MESSAGE)
      {
        disconnect();
        System.out.println("The user "+userName+" is disconnected.");
        chatList.remove(userName);
        isActive=false;
      }
      else if(messageType== MessageIdentifier.QUERY_CONNECTED_USERS)
      {
        connectedUsers();
      }
      else if(messageType== MessageIdentifier.BROADCAST_MESSAGE)
      {
        broadcastDirect(false);
      }
      else if(messageType== MessageIdentifier.DIRECT_MESSAGE)
      {
        broadcastDirect(true);
      }
      else if(messageType== MessageIdentifier.SEND_INSULT)
      {
        insult();
      }
      else {
        fail("Fail message");
      }
    } catch (UndefinedInputException | ParseException| IOException e)
    {
      throw new RuntimeException(e);
    }
  }


  /**Function connect called when connected
   *
   */
  public synchronized void connect()
  {
    try {
      String user = getValue(inputStream,inputStream.readInt());
      userName=user;
      outputStream.writeInt(MessageIdentifier.CONNECT_RESPONSE);
      int numberOfClients = GenerateChat.chatList.size();
      String message;
      if(numberOfClients>10)
      {
        message="maximum users count limit reached";
        outputStream.writeBoolean(false);
        isActive=false;
      }
      else if(GenerateChat.chatList.contains(userName))
      {
        message="The given user is already present";
        outputStream.writeBoolean(false);
        isActive=false;
      }
      else {
        outputStream.writeBoolean(true);
        message="There are "+numberOfClients+" connected clients";
        isActive=true;
      }
      byte[] messageBytes = message.getBytes();
      int messageLength = messageBytes.length;
      outputStream.writeInt(messageLength);
      outputStream.write(messageBytes);

      outputStream.flush();
      System.out.println(message);
    }
      catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Function called when disconnected
   * @throws IOException IOException
   */
  public synchronized void disconnect() throws IOException
  {
    try {
      String user = getValue(inputStream, inputStream.readInt());
      userName=user;
      outputStream.writeInt(MessageIdentifier.DISCONNECT_MESSAGE);
      String message = "You are no longer connected " + user;
      byte[] messageBytes = message.getBytes();
      int messageLength = messageBytes.length;
      outputStream.writeInt(messageLength);
      outputStream.write(messageBytes);

      outputStream.flush();
      System.out.println(message);

    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**Function connectedUsers holds chat list information
   *
   * @throws IOException IOException
   */
  public synchronized void connectedUsers () throws IOException{
    try {
      String user = getValue(inputStream, inputStream.readInt());
      userName = user;
      outputStream.writeInt(MessageIdentifier.QUERY_USER_RESPONSE);
      outputStream.writeInt(chatList.size());

      for (GenerateChat i : chatList) {
        byte[] usernameBytes = i.userName.getBytes();
        int usernameLength = usernameBytes.length;
        outputStream.writeInt(usernameLength);
        outputStream.write(usernameBytes);
      }
      outputStream.flush();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Function broadcastDirect is called hen message type is Broadcast or Direct message
   *
   * @param value value
   * @throws IOException IOException
   */
  private synchronized void broadcastDirect(boolean value) throws IOException
  {
    String sender = getValue(inputStream,inputStream.readInt());
    if (value) {
      String receiver = getValue(inputStream, inputStream.readInt());
      String message = getValue(inputStream, inputStream.readInt());
      sendDirect(message, sender, receiver);
    }
    else
    {
      String msg = getValue(inputStream,inputStream.readInt());
      for (GenerateChat other : chatList) {
        if (other == this) {
          continue;
        }
        else {
          other.sendBroadcast(msg,sender);
        }
      }
    }}

  /**
   * sendDirect method generates direct message to the current user
   * @param messageContent is the message to be sent
   * @param user user is the current sender
   * @param receiver is the one who receives the text
   * @throws IOException throws IOException
   */
  private synchronized void sendDirect(String messageContent, String user, String receiver) throws IOException {
    byte[] senderBytes = user.getBytes();
    byte[] chatMessage = messageContent.getBytes();
    byte[] receiverBytes = receiver.getBytes();
    for(GenerateChat chat: GenerateChat.chatList){
      if(chat.userName.equals(receiver)){
        chat.outputStream.writeInt(MessageIdentifier.DIRECT_MESSAGE);
        chat.outputStream.writeInt(senderBytes.length);
        chat.outputStream.write(senderBytes);
        chat.outputStream.writeInt(receiverBytes.length);
        chat.outputStream.write(receiverBytes);
        chat.outputStream.writeInt(chatMessage.length);
        chat.outputStream.write(chatMessage);
        chat.outputStream.flush();
        return;
      }
    }
  }


  /**
   * sendBroadcast method sends the broadcast message to all the users
   * @param inputValue inputValue Message
   * @param senderUsername sender information
   * @throws IOException throws IOException
   */
  private synchronized void sendBroadcast(String inputValue, String senderUsername) throws IOException {
    byte[] usernameLength = senderUsername.getBytes();
    byte[] chatMessage = inputValue.getBytes();
    this.outputStream.writeInt(MessageIdentifier.BROADCAST_MESSAGE);
    this.outputStream.writeInt(usernameLength.length);
    this.outputStream.write(usernameLength);
    this.outputStream.writeInt(chatMessage.length);
    this.outputStream.write(chatMessage);
    outputStream.flush();
  }

  /**insult calls the assignment4 jar file and generates insult sentence for each user
   *
   * @throws IOException IOException
   * @throws ParseException ParseException
   * @throws UndefinedInputException UndefinedInputException
   */
  public synchronized void insult() throws IOException, ParseException, UndefinedInputException {
    String user = getValue(inputStream,inputStream.readInt());
    String receiver = getValue(inputStream,inputStream.readInt());
    byte[] usernameLength =  user.getBytes();
    byte[] receiverNameLength = receiver.getBytes(StandardCharsets.UTF_8);
    Path insultGrammarFilePath = FileSystems.getDefault().getPath("src/main/InsultGrammar/insult_grammar.json");
    String fileName = insultGrammarFilePath.toString();
    System.out.println("FileName: "+fileName);
    JsonParser parserObject = new JsonParser();
    JSONObject parseOutput = parserObject.load(fileName);
    System.out.println("parseOutput: "+parseOutput);
    new HashMap();
    HashMap<String, List<String>> grammarMap = parserObject.grammarParse(parseOutput);
    System.out.println("grammarMap: "+grammarMap);
    GrammarProcessor grammarProcessor = new GrammarProcessor();
    String insultSentence = grammarProcessor.contentProcessor("start", grammarMap);
    byte[] messageBytes = insultSentence.getBytes(StandardCharsets.UTF_8);
    for(GenerateChat textValue: GenerateChat.chatList){
      textValue.outputStream.writeInt(MessageIdentifier.DIRECT_MESSAGE);
      textValue.outputStream.writeInt(usernameLength.length);
      textValue.outputStream.write(usernameLength);
      textValue.outputStream.writeInt(receiverNameLength.length);
      textValue.outputStream.write(receiverNameLength);
      textValue.outputStream.writeInt(messageBytes.length);
      textValue.outputStream.write(messageBytes);
      textValue.outputStream.flush();
    }
  }

  /** Function fail called when message fails
   *
   * @param failedMessage failedMessage
   * @throws IOException IOException
   */
  private synchronized void fail(String failedMessage) throws IOException
  {
    System.out.println("Message failed");
    byte[] textValueBytes = failedMessage.getBytes();
    this.outputStream.writeInt(MessageIdentifier.FAILED_MESSAGE);
    this.outputStream.writeInt(textValueBytes.length);
    this.outputStream.write(textValueBytes);
  }
  /**close() closes the outputStream and inputStream
   *
   */
  public void close(){
    try {
      outputStream.close();
      inputStream.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** getValue creates a string and converts to byte
   *
   * @param inputStream  inputStream
   * @param byteSize byteSize
   * @return String
   * @throws IOException IOException
   */
  private String getValue(DataInputStream inputStream, int byteSize) throws IOException {
    byte[] bytes = new byte[byteSize];
    inputStream.readFully(bytes);
    String test= new String(bytes, StandardCharsets.UTF_8);
    return test;
  }

  /**
   * toString method
   * @return String
   */
  @Override
  public String toString() {
    return "GenerateChat{}";
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
