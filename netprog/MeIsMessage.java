package chat;

public class MeIsMessage extends Message {

    public MeIsMessage(String originalText, String fromUser) {
        this.originalText = originalText;
        this.fromUser = fromUser;
        this.isValid = true;
    }
    
}
