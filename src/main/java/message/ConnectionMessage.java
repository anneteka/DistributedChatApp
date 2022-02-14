package message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionMessage implements Serializable {
    private String nodeId;
    private boolean isEmpty;

    public ConnectionMessage(String nodeId) {
        this.nodeId = nodeId;
    }

    public ConnectionMessage(byte[] fromArray) {
        ByteArrayInputStream bis = new ByteArrayInputStream(fromArray);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            ConnectionMessage m = (ConnectionMessage) in.readObject();
            this.nodeId = m.getNodeId();
            isEmpty = false;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            isEmpty = true;
        }
        // ignore close exception
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        byte[] res = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            res = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return res;
    }
}
