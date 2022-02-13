package rom;

public class Clock {
    private Integer currentMessageId;

    public Clock(){
        currentMessageId = 0;
    }

    public void reset(){
        currentMessageId = 0;
    }

    public Integer getNewMessageId(){
        currentMessageId = currentMessageId + 1;
        return currentMessageId;
    }

    public Integer getCurrentMessageId(){
        return currentMessageId;
    }
}
