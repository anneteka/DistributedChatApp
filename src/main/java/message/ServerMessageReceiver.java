package message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerMessageReceiver implements Runnable {
    DatagramSocket socket;
    ServerMessageSender sender;
    HashMap<String, LinkedList<Message>> msgQueue;
    private byte[] buffer;

    public ServerMessageReceiver(
            DatagramSocket socket, ServerMessageSender sender
    ) {
        this.socket = socket;
        this.sender = sender;
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                Message msg = null;
                MessageAcknowledgement ack = null;
                try {
                    msg = new Message(packet.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    ack = new MessageAcknowledgement(packet.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (msg != null) {
                    sender.addMessage(msg);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
