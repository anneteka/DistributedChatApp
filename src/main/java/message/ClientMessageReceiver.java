package message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashSet;

public class ClientMessageReceiver implements Runnable {
    DatagramSocket socket;
    final byte[] buffer;
    ClientMessageSender sender;
    String clientId;
    HashSet<Message> received;

    public ClientMessageReceiver(DatagramSocket s, ClientMessageSender sender, String clientId) {
        socket = s;
        buffer = new byte[1024];
        this.sender = sender;
        this.clientId = clientId;
    }

    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                Message rec = new Message(packet.getData());
                if (!received.contains(rec)) {
                    System.out.println(rec);
                    received.add(rec);
                }
                sender.sendAcknowledgement(new MessageAcknowledgement(rec.getId(), clientId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}