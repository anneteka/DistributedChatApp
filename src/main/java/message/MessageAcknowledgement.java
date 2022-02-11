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
public class MessageAcknowledgement {
    private String messageId;
    private String clientId;
    private boolean isEmpty;

    public MessageAcknowledgement(byte[] fromArray) {
        ByteArrayInputStream bis = new ByteArrayInputStream(fromArray);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            MessageAcknowledgement m = (MessageAcknowledgement) in.readObject();
            this.messageId = m.getMessageId();
            isEmpty = false;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            isEmpty = true;
        }
        // ignore close exception
    }

    public MessageAcknowledgement(String messageId, String clientId) {
        this.messageId = messageId;
        this.clientId = clientId;
        isEmpty = false;
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
