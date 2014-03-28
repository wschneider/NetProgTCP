package chat;

public class WhoHereMessage extends Message {

    public WhoHereMessage(String originalText, String fromUser) {
        this.originalText = originalText;
        this.fromUser = fromUser;
        this.isValid = true;
    }
    
    
}
