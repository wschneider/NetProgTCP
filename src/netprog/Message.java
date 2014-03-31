package chat;

public class Message {
    
    public Message() {
        this.originalText = null;
        this.isValid = false;
    }

    public Message(String originalText, boolean valid) {
        this.originalText = originalText;
        this.isValid = valid;
    }
    
    @Override
    public String toString() {
        return originalText;
    }
    
    protected boolean isValid;

    protected String originalText;
    protected String fromUser;
}
