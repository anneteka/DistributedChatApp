package message;

import broadcast.BroadcastListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private LinkedList<Message> queue = new LinkedList<>();
    private InetAddress serverAddress;
    private boolean isConnected;
    private boolean isCLient = true;

    public ClientMessageSender(DatagramSocket s, String h, String clientId, String username, InetAddress server) {
        socket = s;
        hostname = h;
        this.clientId = clientId;
        this.username = username;
        serverAddress = server;
        isConnected = false;
        isCLient = true;
    }

    private void sendMessage(String s) {
        byte[] buffer = s.getBytes();
        sendMsg(buffer);
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

    public void run() {
        isConnected = false;
        System.out.println("sender started");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (isCLient) {
            try {
                while (!in.ready()) {
                    Thread.sleep(200);
                    if (!isConnected) {
                        sendMessage(new ConnectionMessage(clientId));
                    } else if (!queue.isEmpty()) {
                        sendMessage(queue.getFirst());
                    }
                }
                String msg = in.readLine();
                queue.add(new Message("generated-message-id", msg, clientId, username));
                // todo acknowledgement here
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(ConnectionMessage connectionMessage) {
        sendMsg(connectionMessage.toByteArray());
    }
}