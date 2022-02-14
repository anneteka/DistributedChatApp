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
    private boolean isClient = true;

    public ClientMessageReceiver(DatagramSocket s, ClientMessageSender sender, String clientId) {
        socket = s;
        buffer = new byte[1024];
        this.sender = sender;
        this.clientId = clientId;
        received = new HashSet<>();
        isClient = true;
    }

    public void run() {
        while (isClient) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                Message rec = new Message(packet.getData());
                MessageAcknowledgement ack = new MessageAcknowledgement(packet.getData());
                ConnectionMessage con = new ConnectionMessage(packet.getData());
                if (!rec.isEmpty()) {
                    if (!received.contains(rec)) {
                        received.add(rec);
                        System.out.println(rec);
                    }
                    sender.sendAcknowledgement(new MessageAcknowledgement(rec.getId(), clientId));
                }
                if (!ack.isEmpty()) {
                    sender.getQueue().removeIf(message -> Objects.equals(message.getId(), ack.getMessageId()));
                }
                if (!con.isEmpty()) {
                    sender.setConnected(true);
                    sender.setServerAddress(packet.getAddress());
                    System.out.println("connection to the server established");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}