package message;

import broadcast.BroadcastListener;
import broadcast.Peers;
import election.data.PeerInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rom.Peer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServerMessageSender implements Runnable {
    private DatagramSocket socket;
    // node id / node queue
    private HashMap<String, LinkedList<Message>> msgQueue;
    private HashMap<String, InetAddress> clientAddresses;
    private final Integer CLIENT_PORT = 8080;
    private String serverId = "server-id";
    private BroadcastListener bcListener;


    public ServerMessageSender(DatagramSocket socket) {
        this.socket = socket;
        msgQueue = new HashMap<>();
        clientAddresses = new HashMap<>();
        bcListener = BroadcastListener.getInstance();
    }

    @Override
    public void run() {
        Peers nodes = bcListener.getPeers();
        for (PeerInfo peer:nodes.getPeers()) {
            if(!peer.isLeader())
                addClient(peer.getUniqueIdentifier().toString(), peer.getIpAddr());
        }
        while (true){
            try {
                Thread.sleep(200);
                for (Map.Entry<String, LinkedList<Message>> msgs: msgQueue.entrySet()) {
                    if (!msgs.getValue().isEmpty()) {
                        sendMessage(msgs.getKey(), msgs.getValue().getFirst());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void sendAcknowledgement(Message message) {
        sendMsg(new MessageAcknowledgement(message.getId(), serverId).toByteArray(), clientAddresses.get(message.getSenderId()), CLIENT_PORT);
    }

    private void sendMessage(String clientId, Message message) {
        InetAddress address = clientAddresses.get(clientId);
        sendMsg(message.toByteArray(), address, CLIENT_PORT);
    }

    private void sendMsg(byte[] buffer, InetAddress clientAddress, Integer port) {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, port);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMessage(Message msg) {
        for (Map.Entry<String, LinkedList<Message>> msgs: msgQueue.entrySet()) {
            if (!msgs.getKey().equals(msg.getSenderId())) {
                msgs.getValue().add(msg);
            }
        }
    }
    public void removeMessage(String clientId, String messageId){
        msgQueue.get(clientId).removeIf(message -> Objects.equals(message.getId(), messageId));
    }

    public void addClient(String nodeId, InetAddress address){
        if (!clientAddresses.containsKey(nodeId)) {
            clientAddresses.put(nodeId, address);
            msgQueue.put(nodeId, new LinkedList<>());
            System.out.println("New user joined the chat");
        }
    }

    public void sendAcknowledgement(ConnectionMessage con) {
        sendMsg(con.toByteArray(), clientAddresses.get(con.getNodeId()), CLIENT_PORT);
    }
}
