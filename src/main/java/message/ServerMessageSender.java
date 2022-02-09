package message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServerMessageSender implements Runnable {
    DatagramSocket socket;
    HashMap<String, LinkedList<Message>> msgQueue;

    @Override
    public void run() {

    }

    public void addMessage(Message msg) {
        for (Map.Entry<String, LinkedList<Message>> msgs: msgQueue) {

        }
    }
}
