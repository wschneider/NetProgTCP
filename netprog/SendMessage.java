package chat;

public class SendMessage extends Message {

    public SendMessage(String originalText, String fromUser, String toUser,
            String messageContents) {
        this.originalText = originalText;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.messageContents = messageContents;
        this.isValid = true;
    }
    
    public String getToUser() {
        return toUser;
    }
    
    public String getMessageContents() {
        return messageContents;
    }
    
    private String toUser;
    private String messageContents;

}
