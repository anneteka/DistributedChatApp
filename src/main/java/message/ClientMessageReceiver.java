package message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashSet;
import java.util.Objects;

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
                MessageAcknowledgement ack = new MessageAcknowledgement(packet.getData());
                ConnectionMessage con = new ConnectionMessage(packet.getData());
                if (!rec.isEmpty() && received.contains(rec)) {
                    System.out.println(rec);
                    received.add(rec);
                    sender.sendAcknowledgement(new MessageAcknowledgement(rec.getId(), clientId));
                }
                if (!ack.isEmpty()){
                    sender.getQueue().removeIf(message -> Objects.equals(message.getId(), ack.getMessageId()));
                }
                if (!con.isEmpty()){
                    sender.setConnected(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}