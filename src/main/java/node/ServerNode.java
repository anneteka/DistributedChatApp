package node;

import lombok.Getter;
import message.Message;
import message.ServerMessageReceiver;
import message.ServerMessageSender;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

@Getter
public class ServerNode {
    public final static int PORT = 8080;
    private final static int BUFFER = 1024;

    private final DatagramSocket socket;
    private final ArrayList<InetAddress> clientAddresses;
    private final ArrayList<Integer> clientPorts;
    private final HashSet<String> existingClients;
    private String hostname;

    public ServerNode() throws IOException {
        socket = new DatagramSocket(PORT);
        clientAddresses = new ArrayList<>();
        clientPorts = new ArrayList<>();
        existingClients = new HashSet<>();
        DatagramSocket socket = new DatagramSocket();
        ServerMessageSender s = new ServerMessageSender(socket);
        ServerMessageReceiver r = new ServerMessageReceiver(socket, s);
        Thread rt = new Thread(r);
        Thread st = new Thread(s);
        rt.start(); st.start();
    }

//    public void run() {
//        byte[] buf = new byte[BUFFER];
//        try {
//            hostname = InetAddress.getLocalHost().getHostName();
//            System.out.println("Chat server started on host "+ hostname);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        while (true) {
//            try {
//                Arrays.fill(buf, (byte) 0);
//                DatagramPacket packet = new DatagramPacket(buf, buf.length);
//                socket.receive(packet);
//
//                String content = new String(buf, buf.length);
//
//                InetAddress clientAddress = packet.getAddress();
//                int clientPort = packet.getPort();
//
//                String id = clientAddress.toString() + "," + clientPort;
//                if (!existingClients.contains(id)) {
//                    existingClients.add(id);
//                    clientPorts.add(clientPort);
//                    clientAddresses.add(clientAddress);
//                }
//
//                System.out.println(id + " : " + content);
//                byte[] data = (id + " : " + content).getBytes();
//                for (int i = 0; i < clientAddresses.size(); i++) {
//                    InetAddress cl = clientAddresses.get(i);
//                    int cp = clientPorts.get(i);
//                    packet = new DatagramPacket(data, data.length, cl, cp);
//                    socket.send(packet);
//                }
//            } catch (Exception e) {
//                System.err.println(e);
//            }
//        }
//    }

    public static void main(String[] args) throws Exception {
        var s = new ServerNode();
//        s.start();
    }
}