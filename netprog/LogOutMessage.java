package chat;

public class LogOutMessage extends Message {

    public LogOutMessage(String originalText, String fromUser) {
        this.originalText = originalText;
        this.fromUser = fromUser;
        this.isValid = true;
    }
    
}
