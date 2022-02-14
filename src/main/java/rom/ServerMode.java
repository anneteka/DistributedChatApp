package rom;

import election.data.PeerInfo;
import election.network.NetworkConstant;
import election.network.UDP;
import message.Message;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ServerMode {
    private DatagramSocket senderSocket = null;
    private MulticastSocket receiverSocket = null;

    private PeerInfo leader = null;
    private ArrayList<Integer> cloks = new ArrayList<Integer>();

    private static HashMap<Integer, MessageInfo> messages = new HashMap<Integer, MessageInfo>();

    private static Clock clock = null;
    private static Clock serverClock = null;

    private Integer sentId = 0;

    public ServerMode(PeerInfo leader){

        PeerInfo info = new PeerInfo();
        try {
            senderSocket = new DatagramSocket(NetworkConstant.leaderMessageSenderPort);
            receiverSocket =  new MulticastSocket(NetworkConstant.leaderMessageReceiverPort);

            InetAddress group = InetAddress.getByName(NetworkConstant.multicastAddress);
            receiverSocket.joinGroup(group);

            clock = new Clock();
            serverClock = new Clock();
            clock.reset();

            this.leader = leader;
            serverReader.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(){

        while(messages.size() > sentId) {
            try {
                byte[] byteArray = UDP.serializeToByteArray(messages.get(sentId + 1));
                sentId = sentId + 1;
                UDP.sendUdp(byteArray, senderSocket, InetAddress.getByName(NetworkConstant.multicastAddress), NetworkConstant.clientMessageReceiverPort);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    public MessageInfo receiveData(){
        MessageInfo info = null;

        try {
            byte[] byteArray = UDP.receiveUdp(receiverSocket);
            info = (MessageInfo)UDP.deserializeByteArray(byteArray);
            info.setGlobalId(clock.getNewMessageId());
            messages.put(clock.getCurrentMessageId(), info);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return info;
    }

    public String format(MessageInfo info){
        return info.getPeerID() + "\t" + info.getGlobalId() + "\t" + info.getMessageId() + "\t" + info.getMessage();
    }


    private static Thread serverReader = new Thread() {
        private static Scanner scanner;
        public void run() {
            scanner = new Scanner(System.in);
            while(scanner.hasNext()) {
                MessageInfo info  = new MessageInfo(serverClock.getNewMessageId(), scanner.nextLine());
                info.setGlobalId(clock.getNewMessageId());
                messages.put(clock.getCurrentMessageId(), info);
            }
        }
    };
}
