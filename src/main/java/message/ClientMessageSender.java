package message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;

public class ClientMessageSender implements Runnable {
    public final static int PORT = 8080;
    private final DatagramSocket socket;
    private final String hostname;
    private final String clientId;
    private final String username;
    private LinkedList<Message> queue;

    public ClientMessageSender(DatagramSocket s, String h, String clientId, String username) {
        socket = s;
        hostname = h;
        this.clientId = clientId;
        this.username = username;
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
            InetAddress address = InetAddress.getByName(hostname);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORT);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAcknowledgement(MessageAcknowledgement ack) {
        sendMsg(ack.toByteArray());
    }

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
                    Thread.sleep(100);
                }
                String msg = in.readLine();
                sendMessage(new Message("generated-message-id", msg, "server-id", clientId, username));
                // todo acknowledgement here
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}