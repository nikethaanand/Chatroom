/**
 * @author divyadharshinimuruganandham  nikethaanand
 */
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * ReceiveText class is responsible to receive the user input from socket and show whatever is received in the command line
 */

public class ReceiveText implements Runnable{

    private String user;
    BufferedReader bufferReader;
    DataOutputStream outputStream;
    private DataInputStream inputStreamBuffer;
    private boolean checkIsActive;
    private Socket clientMachine;

    /**
     * Class constructor which initiates the object for the inputStream and to receive a new input message
     * @param clientMachine is the client which is going to ready to receive from the server
     * @param userName is the input given
     */

    public ReceiveText(Socket clientMachine, String userName) {
        try {
            inputStreamBuffer = new DataInputStream(clientMachine.getInputStream());
            this.clientMachine = clientMachine;
            this.user = userName;
            this.checkIsActive = true;
        } catch (IOException e) {
            e.printStackTrace();
            checkIsActive = false;
            shutdown();
        }
    }

    /**
     * Defined constructor to test the override methods of the class
     */
    public ReceiveText(){}

    /**
     * showMessageIdentifier have the list of message identifier and matches with the type of the given identifier
     */

    public void showMessageIdentifier(){
        int textIdentifier = -1;
        try {
            textIdentifier = inputStreamBuffer.readInt();
            if(textIdentifier == 20){
                establishConnection();
            }
            else if(textIdentifier == 21){
                suspendConnection();
            }
            else if(textIdentifier == 23){
                queryResponse();
            }
            else if(textIdentifier == 24){
                sendMessageBroadcast();
            }
            else if(textIdentifier == 25){
                sendMessageDirect();
            }
            else if(textIdentifier == 26){
                messageFailure();
            }
            else {
                System.out.println("Please enter a valid message type");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(checkIsActive);
            checkIsActive = false;
            shutdown();
        }
    }

    /**
     * establishConnection does the job for establishing the connection
     * @throws IOException throws IO exception
     */

    public void establishConnection() throws IOException{
        boolean checkStatus = inputStreamBuffer.readBoolean();
        System.out.println(checkStatus ? "Successfully Connection Established": "Connection is not established!");
        System.out.println(getValue(inputStreamBuffer,inputStreamBuffer.readInt()));
    }

    /**
     * messageFailure does the job for failed messages
     * @throws IOException throws IO exception
     */

    public void messageFailure() throws IOException {
        System.out.println(getValue(inputStreamBuffer,inputStreamBuffer.readInt()));
    }

    /**
     * sendMessageBroadcast does the job fof broadcasting the messages
     * @throws IOException throws IO exception
     */

    public void sendMessageBroadcast() throws IOException {
        String outputText = "";
        outputText=outputText+getValue(inputStreamBuffer,inputStreamBuffer.readInt())+"="+"to all: ";
        outputText=outputText+getValue(inputStreamBuffer,inputStreamBuffer.readInt());
        System.out.println(outputText);
    }

    /**
     * askQuestion does the job of query messages
     * @throws IOException throws IO exception
     */
    public void queryResponse() throws IOException {
        int usersCount = inputStreamBuffer.readInt();
        String query = "Active Users in the channel are : ";
        for(int count=0;count<usersCount;count++){
            query=query+getValue(inputStreamBuffer,inputStreamBuffer.readInt()) + ",";
        }
        System.out.println(query);
    }

    /**
     * suspendConnection does the job of suspending the messages
     * @throws IOException throws IO exception
     */

    public void suspendConnection() throws IOException {
        System.out.println("Connection terminated successfully");
        System.out.println(getValue(inputStreamBuffer,inputStreamBuffer.readInt()));
        this.checkIsActive = false;
    }

    /**
     * sendMessageDirect does the job for sending the texts
     * @throws IOException throws IO exception
     */
    public void sendMessageDirect() throws IOException {
        String textInput="";
        textInput=textInput+getValue(inputStreamBuffer,inputStreamBuffer.readInt())+" to ";
        textInput=textInput+getValue(inputStreamBuffer,inputStreamBuffer.readInt())+" is sent ";
        textInput=textInput+getValue(inputStreamBuffer,inputStreamBuffer.readInt());
        System.out.println(textInput);
    }


    /**
     *
     * @param inputStreamBuffer it is the DataInputStream which receives a data from the commandline
     * @param value value length of the given text
     * @return the string value
     * @throws IOException throws IO exception
     */
    public String getValue(DataInputStream inputStreamBuffer, int value) throws IOException {
        byte[] bytes = new byte[value];
        inputStreamBuffer.readFully(bytes);
        String test= new String(bytes, StandardCharsets.UTF_8);
        return test;
    }

    //----------------------------------------------Override-------------------------------------------------------------

    /**
     * override run method
     */
    @Override
    public void run() {
        while (checkIsActive) {
            showMessageIdentifier();
            if (!checkIsActive) {
                System.out.println("You can exit the program now");
            }
        }
    }

    /**
     * shutdown closes all active states
     *
     */
    public void shutdown(){
        try {
            outputStream.close();
            bufferReader.close();
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
        return "ReceiveTextMessage{}";
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



