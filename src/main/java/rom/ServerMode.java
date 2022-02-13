package rom;

import election.data.PeerInfo;
import election.network.NetworkConstant;
import election.network.UDP;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerMode {
    private DatagramSocket senderSocket = null;
    private MulticastSocket receiverSocket = null;

    private PeerInfo leader = null;
    private ArrayList<Integer> cloks = new ArrayList<Integer>();

    private HashMap<Integer, MessageInfo> messages = new HashMap<Integer, MessageInfo>();
    private Clock clock = null;
    private Integer sentId = 0;

    public ServerMode(){

        PeerInfo info = new PeerInfo();
        try {
            senderSocket = new DatagramSocket(NetworkConstant.leaderMessageSenderPort);
            receiverSocket =  new MulticastSocket(NetworkConstant.leaderMessageReceiverPort);

            InetAddress group = InetAddress.getByName(NetworkConstant.multicastAddress);
            receiverSocket.joinGroup(group);

            clock = new Clock();
            clock.reset();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(Object obj){
        if(messages.size() > clock.getCurrentMessageId()) {
            byte[] byteArray = UDP.serializeToByteArray(obj);
            UDP.sendUdp(byteArray, senderSocket, leader.getIpAddr(), leader.getPort());
        }
    }

    public MessageInfo receiveData(){
        MessageInfo info = null;

        try {
            byte[] byteArray = UDP.receiveUdp(receiverSocket);
            System.out.println("Received Data from client" );
            info = (MessageInfo)UDP.deserializeByteArray(byteArray);
            messages.put(clock.getNewMessageId(), info);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return info;
    }
}
