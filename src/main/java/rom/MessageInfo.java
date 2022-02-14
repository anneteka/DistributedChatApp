package rom;

import election.Bully;

import java.io.Serializable;
import java.util.UUID;

public class MessageInfo implements Serializable  {
    private Integer globalId;
    private Integer messageId;
    private String message;
    private UUID peerID;

    public MessageInfo(Integer messageId, String message) {
        this.globalId = -1;
        this.messageId = messageId;
        this.message = message;
        this.peerID = Bully.getInstance().getClientUUID();
    }

    public Integer getGlobalId() {
        return globalId;
    }

    public void setGlobalId(Integer globalId) {
        this.globalId = globalId;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public UUID getPeerID() {
        return peerID;
    }
}
