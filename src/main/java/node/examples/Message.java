package node.examples;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Message {
    private String message;
    private int timestamp;
    private int source;
    private MetaData metaData;

    public Message(int time, String msg, int id) {
        this.timestamp = time;
        message = msg;
        this.source = id;
        metaData = null;
    }

    public void setData(MetaData data) {
        this.metaData = data;
    }
}
