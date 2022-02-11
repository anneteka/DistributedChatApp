package message;

import lombok.SneakyThrows;

import java.io.IOException;
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

    @SneakyThrows
    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                Message rec = new Message(packet.getData());
                MessageAcknowledgement ack = new MessageAcknowledgement(packet.getData());
                if (!rec.isEmpty() && !received.contains(rec)) {
                    System.out.println(rec);
                    received.add(rec);
                    sender.sendAcknowledgement(new MessageAcknowledgement(rec.getId(), clientId));
                }
                if (!ack.isEmpty()){
                    sender.getQueue().removeIf(message -> message.getId().equals(ack.getMessageId()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Thread.interrupted())
                throw new InterruptedException();
        }
    }
}