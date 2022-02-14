package message;

import lombok.*;

import java.io.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Message implements Serializable{
    private String id;
    private String msg;
    private String senderId;
    private String senderUsername;
    private boolean isEmpty;

    public Message(String id, String msg, String senderId, String senderUsername) {
        this.id = id;
        this.msg = msg;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
    }

    public Message(byte[] fromArray) {
        ByteArrayInputStream bis = new ByteArrayInputStream(fromArray);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            Message m = (Message) in.readObject();
            this.msg = m.getMsg();
            this.senderId = m.getSenderId();
            this.senderUsername = m.getSenderUsername();
            isEmpty = false;
        } catch (IOException | ClassNotFoundException e) {
            isEmpty = true;
            e.printStackTrace();
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

    @Override
    public String toString() {
        return senderUsername +": "+msg;
    }
}
