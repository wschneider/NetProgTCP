package chat;

public class BroadcastMessage extends Message {

    public BroadcastMessage(String originalText, String fromUser, String messageContents) {
        this.originalText = originalText;
        this.fromUser = fromUser;
        this.messageContents = messageContents;
        this.isValid = true;
    }
    
    public String getMessageContents() {
        return messageContents;
    }
    
    private String messageContents;

}
