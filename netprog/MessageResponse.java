package chat;

import java.util.List;

public class MessageResponse {

    public MessageResponse(List<String> responseTargets, String responseContents) {
        this.responseTargets = responseTargets;
        this.responseContents = responseContents;
    }
    
    public List<String> getResponseTargets() {
        return responseTargets;
    }
    
    public String getResponseTarget(int index) {
        return responseTargets.get(index);
    }
    
    public int getResponseTargetCount() {
        return responseTargets.size();
    }
    
    public String getResponseContents() {
        return responseContents;
    }
    
    private List<String> responseTargets;
    private String responseContents;

}
