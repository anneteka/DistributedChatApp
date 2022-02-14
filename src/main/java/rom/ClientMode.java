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

    public ClientMode(PeerInfo leader){
        try {
            senderSocket = new DatagramSocket(NetworkConstant.clientMessageSenderPort);
            receiverSocket =  new MulticastSocket(NetworkConstant.clientMessageReceiverPort);

            InetAddress group = InetAddress.getByName(NetworkConstant.multicastAddress);
            receiverSocket.joinGroup(group);

            this.leader = leader;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public void sendData(Object obj){
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

    public String format(MessageInfo info){
        return info.getPeerID() + "\t" + info.getGlobalId() + "\t" + info.getMessageId() + "\t" + info.getMessage();
    }
}
