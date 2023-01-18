package ExceptionHandling;
/**
 * @author divyadharshinimuruganandham  nikethaanand
 */
public class ChatTypeException extends Throwable {
    /**
     * prints the exception message thrown
     * @param message message
     */
    public ChatTypeException(String message){
        super(message);
        System.out.println(message);
    }
}
