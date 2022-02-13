package rom;

import election.data.PeerInfo;
import election.network.NetworkConstant;
import election.network.UDP;

import java.io.IOException;
import java.net.*;

public class ClientMode {

    private DatagramSocket senderSocket = null;
    private MulticastSocket receiverSocket = null;

    private PeerInfo leader = null;

    public ClientMode(){
        //leader = Bully.getLeader();
        PeerInfo info = new PeerInfo();
        try {
            info.setIpAddr(InetAddress.getLocalHost());
            info.setPort(NetworkConstant.clientMessageReceiverPort);

            senderSocket = new DatagramSocket(NetworkConstant.clientMessageSenderPort);
            receiverSocket =  new MulticastSocket(NetworkConstant.clientMessageReceiverPort);

            InetAddress group = InetAddress.getByName(NetworkConstant.multicastAddress);
            receiverSocket.joinGroup(group);

        } catch (IOException e) {
            e.printStackTrace();
        }
        leader = info;

    }

        public void sendData(Object obj){
            System.out.println("Send Data from client IP and Port " + NetworkConstant.leaderMessageReceiverPort);
            byte[] byteArray = UDP.serializeToByteArray(obj);
            UDP.sendUdp(byteArray, senderSocket, leader.getIpAddr(), NetworkConstant.leaderMessageReceiverPort);
        }

        public MessageInfo receiveData(){
            MessageInfo info = null;

            try {
                byte[] byteArray = UDP.receiveUdp(receiverSocket);
                info = (MessageInfo)UDP.deserializeByteArray(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return info;
        }
}
