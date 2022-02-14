package election;

import broadcast.BroadcastListener;
import broadcast.Helper;
import broadcast.Peers;
import election.data.ElectionInfo;
import election.data.PeerInfo;
import election.network.NetworkConstant;
import election.network.UDP;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Bully {

    private static final String BULLY_ALGO = "Bully Algorithm : ";

    private static PeerInfo myInfo = new PeerInfo();
    private static PeerInfo leader = null;

    private ElectionInfo myElectionInfo = null;

    //private List<PeerInfo> Participants = new ArrayList<PeerInfo>();
    Peers peers;

    private PollElectionData pollElectionData;

    private DatagramSocket receiverSocket = null;

    private boolean poll = true;
    private boolean bullied = true;

    private static Bully instance = null;
    private Bully() {
        try {

            receiverSocket = new DatagramSocket(NetworkConstant.leaderDestPort);
            myInfo.setUniqueIdentifier(UUID.randomUUID());
            myInfo.setParticipent(true);
            myInfo.setLeader(false);
            myInfo.setPort(NetworkConstant.leaderDestPort);
            myInfo.setIpAddr(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));

            myElectionInfo = new ElectionInfo(myInfo);

            pollElectionData = new PollElectionData();
            pollElectionData.start();

            peers = new Peers();
            System.out.println(BULLY_ALGO + "My unique Id : " + myInfo.getUniqueIdentifier());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static Bully getInstance(){
        if(instance == null){
            instance = new Bully();
        }
        return instance;
    }

    public boolean amILeader(){
        if(myElectionInfo.getElectionSate() == ElectionInfo.Election.LEADER){
            return true;
        }
        return false;
    }

    public UUID getClientUUID()
    {
        return myInfo.getUniqueIdentifier();
    }

    /*
    private PeerInfo getLeader()
    {
        return leader;
    }*/

    public void startElection() {
        try {

            bullied = false;
            myElectionInfo.getPeerInfo().setParticipent(false);
            myElectionInfo.getPeerInfo().setLeader(true);
            peers.clear();
            BroadcastListener.getInstance().getPeers().clear();

            myElectionInfo.setElectionSate(ElectionInfo.Election.REGISTRATION_START);
            serializeAndSendUDP(myElectionInfo, Helper.getBroadcastAddressList(), true);
            System.out.println(BULLY_ALGO + "Election started with IP : " + myElectionInfo.getPeerInfo().getIpAddr());
            Thread.sleep(5000);

            if(!bullied) {
                myElectionInfo.setElectionSate(ElectionInfo.Election.LEADER);
                serializeAndSendUDP(myElectionInfo, Helper.getBroadcastAddressList(), true);
                leader = myElectionInfo.getPeerInfo();
                sendACKforClients();
                System.out.println(BULLY_ALGO + "Not Bullied, Announce as leader with IP : " + myElectionInfo.getPeerInfo().getIpAddr());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void stopPolling() {
        poll = false;
        receiverSocket.close();
    }

    private void sendACKforClients(){
        peers.addPeer(leader);
        BroadcastListener.getInstance().setPeers(peers);
        BroadcastListener.getInstance().sendACKForClients();
    }

    private void processReceivedData(ElectionInfo info){
        if(ElectionInfo.Election.REGISTRATION_START == info.getElectionSate()){
            if(!bullyAndStartElection(info))
            {
                getRegistered(info);
            }
        }
        else if(ElectionInfo.Election.LEADER == info.getElectionSate()){
            if(myElectionInfo.getPeerInfo().getUniqueIdentifier().compareTo(info.getPeerInfo().getUniqueIdentifier()) != 0) {
                leader = info.getPeerInfo();
                System.out.println(BULLY_ALGO + "Leader elected with port and IP : " + info.getPeerInfo().getPort() + "\t" + info.getPeerInfo().getIpAddr());
            }
        }
        else if(ElectionInfo.Election.REGISTER == info.getElectionSate()){
            if(myElectionInfo.getElectionSate() == ElectionInfo.Election.REGISTRATION_START) {
                peers.addPeer(info.getPeerInfo());
                System.out.println(BULLY_ALGO + "Register Participant with port and IP : " + info.getPeerInfo().getPort() + "\t" + info.getPeerInfo().getIpAddr());
            }
        }else if(ElectionInfo.Election.BULLY_FOR_ELECTION == info.getElectionSate()) {
            bullied = true;
            myElectionInfo.getPeerInfo().setParticipent(true);
            myElectionInfo.getPeerInfo().setLeader(false);

            myElectionInfo.setElectionSate(ElectionInfo.Election.IDLE);
            peers.clear();
            System.out.println(BULLY_ALGO + "Bullied, Clearing Participants, New peer will takeover : "  +  info.getPeerInfo().getIpAddr());
        }
    }

    private void getRegistered(ElectionInfo info)
    {
        if(myElectionInfo.getPeerInfo().getUniqueIdentifier().compareTo(info.getPeerInfo().getUniqueIdentifier()) != 0)
        {
            myElectionInfo.setElectionSate(ElectionInfo.Election.REGISTER);
            serializeAndSendUDP(myElectionInfo, List.of(info.getPeerInfo().getIpAddr()), false);
            System.out.println(BULLY_ALGO + "Register My Port and IP : " + myElectionInfo.getPeerInfo().getPort() + "\t" + myElectionInfo.getPeerInfo().getIpAddr() + " with Leader Port and IP :" + info.getPeerInfo().getPort() + "\t" + info.getPeerInfo().getIpAddr());
        }
    }

    private boolean bullyAndStartElection(ElectionInfo info)
    {
        if(myElectionInfo.getPeerInfo().getUniqueIdentifier().compareTo(info.getPeerInfo().getUniqueIdentifier()) > 0)
        {
            myElectionInfo.getPeerInfo().setParticipent(false);
            myElectionInfo.getPeerInfo().setLeader(true);

            myElectionInfo.setElectionSate(ElectionInfo.Election.BULLY_FOR_ELECTION);
            serializeAndSendUDP(myElectionInfo, List.of(info.getPeerInfo().getIpAddr()), true);

            System.out.println(BULLY_ALGO + "Bully peer and takeover with port and IP : "  + myElectionInfo.getPeerInfo().getPort() + "\t" + myElectionInfo.getPeerInfo().getIpAddr());
            startElection();
            return true;
        }

        return false;
    }

    private boolean shouldIbully(ElectionInfo info){
        boolean infoIsDiffernt = myElectionInfo.getPeerInfo().getUniqueIdentifier().compareTo(info.getPeerInfo().getUniqueIdentifier()) != 0;
        boolean electionStartedByMe = myElectionInfo.getElectionSate() == ElectionInfo.Election.REGISTRATION_START;
        boolean bully = myElectionInfo.getPeerInfo().getUniqueIdentifier().compareTo(info.getPeerInfo().getUniqueIdentifier()) > 0;

        //return ((infoIsDiffernt && electionStartedByMe) || bully);
        return (bully);
    }

    private void serializeAndSendUDP(Object obj, List<InetAddress> address, boolean broadcast)
    {
        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(obj);
            out.flush();

            //byte[] data = SerializationUtils.serialize(myElectionInfo);
            if(broadcast) {
                for(int i = 0; i < address.size(); i++) {
                    UDP.broadcast(bos.toByteArray(), address.get(0), NetworkConstant.leaderDestPort);
                }
            } else{
                UDP.broadcast(bos.toByteArray(), address.get(0), NetworkConstant.leaderDestPort);
            }

            bos.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PollElectionData extends Thread {

        public void run() {


                System.out.println(BULLY_ALGO + "Started Polling Election Data, My Port and Ip : " + myElectionInfo.getPeerInfo().getPort() + "\t" + myElectionInfo.getPeerInfo().getIpAddr());
                while(poll) {
                    try {
                        byte[] data = UDP.receiveUdp(receiverSocket);
                        //ElectionInfo receivedElectionInfo = SerializationUtils.deserialize(data);

                        ByteArrayInputStream bis = new ByteArrayInputStream(data);
                        ObjectInput in = new ObjectInputStream(bis);
                        ElectionInfo receivedElectionInfo = (ElectionInfo) in.readObject();

                        processReceivedData(receivedElectionInfo);

                        in.close();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        if(poll) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println(BULLY_ALGO + "Stopped Polling Election Data ");
        }
    }
}
