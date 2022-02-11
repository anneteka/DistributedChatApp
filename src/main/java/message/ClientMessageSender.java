package message;

import lombok.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

@Getter
@Setter
@AllArgsConstructor
public class ClientMessageSender implements Runnable {
    public final static int PORT = 8080;
    private final DatagramSocket socket;
    private String hostname;
    private final String clientId;
    private final String username;
    private LinkedList<Message> queue;
    private InetAddress serverAddress;

    public ClientMessageSender(DatagramSocket s, String h, String clientId, String username, InetAddress server) {
        socket = s;
        hostname = h;
        this.clientId = clientId;
        this.username = username;
        serverAddress = server;
    }

    private void sendMessage(String s) {
        sendMessage(new Message("generated-id", s, clientId, username ));
    }

    private void sendMessage(Message message){
        sendMsg(message.toByteArray());
    }

    private void sendMsg(byte[] buffer) {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAcknowledgement(MessageAcknowledgement ack) {
        sendMsg(ack.toByteArray());
    }

    @SneakyThrows
    public void run() {
        boolean connected = false;
        System.out.println("sender started");
        do {
            try {
                sendMessage(username+" joined chat");
                connected = true;
            } catch (Exception e) {

            }
        } while (!connected);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                while (!in.ready()) {
                    Thread.sleep(500);
                    sendMessage(queue.getFirst());
                }
                String msg = in.readLine();
                queue.add(new Message("generated-message-id", msg, clientId, username));
                // todo acknowledgement here
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Thread.interrupted())
                throw new InterruptedException();
        }
    }
}