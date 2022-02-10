package election;

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
import java.util.List;
import java.util.UUID;

public class Bully {

    private static final String BULLY_ALGO = "Bully Algorithm : ";

    private PeerInfo myInfo = new PeerInfo();
    private static PeerInfo leader = new PeerInfo();

    private ElectionInfo myElectionInfo = null;

    private List<PeerInfo> Participants = new ArrayList<PeerInfo>();
    private PollElectionData pollElectionData;

    private DatagramSocket receiverSocket = null;

    private boolean poll = true;
    private boolean bullied = true;

    public Bully() {
        try {

            receiverSocket = new DatagramSocket(NetworkConstant.leaderDestPort);
            myInfo.setUniqueIdentifier(UUID.randomUUID());
            myInfo.setParticipent(true);
            myInfo.setPort(NetworkConstant.leaderDestPort);
            myInfo.setIpAddr(InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));

            myElectionInfo = new ElectionInfo(myInfo);

            pollElectionData = new PollElectionData();
            pollElectionData.start();

            System.out.println(BULLY_ALGO + "My unique Id : " + myInfo.getUniqueIdentifier());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void startElection() {
        try {

            bullied = false;

            myElectionInfo.setElectionSate(ElectionInfo.Election.REGISTRATION_START);
            serializeAndSendUDP(myElectionInfo, InetAddress.getByName(NetworkConstant.broadcastAddress), true);
            System.out.println(BULLY_ALGO + "Election started with IP : " + myInfo.getIpAddr());
            Thread.sleep(5000);

            if(!bullied) {
                myElectionInfo.setElectionSate(ElectionInfo.Election.LEADER);
                serializeAndSendUDP(myElectionInfo, InetAddress.getByName(NetworkConstant.broadcastAddress), true);
                System.out.println(BULLY_ALGO + "Not Bullied, Announce as leader with IP : " + myInfo.getIpAddr());
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopPolling() {
        poll = false;
        receiverSocket.close();
    }

    private void processReceivedData(ElectionInfo info){
        if(ElectionInfo.Election.REGISTRATION_START == info.getElectionSate()){
            if(!bullyAndStartElection(info))
            {
                getRegistered(info);
            }
        }
        else if(ElectionInfo.Election.LEADER == info.getElectionSate()){
            if(myInfo.getUniqueIdentifier().compareTo(info.getPeerInfo().getUniqueIdentifier()) != 0) {
                leader = info.getPeerInfo();
                System.out.println(BULLY_ALGO + "Leader elected with port and IP : " + info.getPeerInfo().getPort() + "\t" + info.getPeerInfo().getIpAddr());
            }
        }
        else if(ElectionInfo.Election.REGISTER == info.getElectionSate()){
            if(myElectionInfo.getElectionSate() == ElectionInfo.Election.REGISTRATION_START) {
                Participants.add(info.getPeerInfo());
                System.out.println(BULLY_ALGO + "Register Participant with port and IP : " + info.getPeerInfo().getPort() + "\t" + info.getPeerInfo().getIpAddr());
            }
        }else if(ElectionInfo.Election.BULLY_FOR_ELECTION == info.getElectionSate()) {
            bullied = true;
            myElectionInfo.setElectionSate(ElectionInfo.Election.IDLE);
            Participants.clear();
            System.out.println(BULLY_ALGO + "Bullied, Clearing Participants, New peer will takeover : "  +  info.getPeerInfo().getIpAddr());
        }
    }

    private void getRegistered(ElectionInfo info)
    {
        if(myInfo.getUniqueIdentifier().compareTo(info.getPeerInfo().getUniqueIdentifier()) != 0)
        {
            myElectionInfo.setElectionSate(ElectionInfo.Election.REGISTER);
            serializeAndSendUDP(myElectionInfo, info.getPeerInfo().getIpAddr(), false);
            System.out.println(BULLY_ALGO + "Register My Port and IP : " + myInfo.getPort() + "\t" + myInfo.getIpAddr() + " with Leader Port and IP :" + info.getPeerInfo().getPort() + "\t" + info.getPeerInfo().getIpAddr());
        }
    }

    private boolean bullyAndStartElection(ElectionInfo info)
    {
        if(myInfo.getUniqueIdentifier().compareTo(info.getPeerInfo().getUniqueIdentifier()) > 0)
        {
            myElectionInfo.setElectionSate(ElectionInfo.Election.BULLY_FOR_ELECTION);
            serializeAndSendUDP(myElectionInfo, info.getPeerInfo().getIpAddr(), true);

            System.out.println(BULLY_ALGO + "Bully peer and takeover with port and IP : "  + myInfo.getPort() + "\t" + myInfo.getIpAddr());
            startElection();
            return true;
        }

        return false;
    }

    private void serializeAndSendUDP(Object obj, InetAddress address, boolean broadcast)
    {
        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(obj);
            out.flush();

            //byte[] data = SerializationUtils.serialize(myElectionInfo);
            if(broadcast) {
                UDP.broadcast(bos.toByteArray(), address, NetworkConstant.leaderDestPort);
            } else{
                UDP.broadcast(bos.toByteArray(), address, NetworkConstant.leaderDestPort);
            }

            bos.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PollElectionData extends Thread {

        public void run() {


                System.out.println(BULLY_ALGO + "Started Polling Election Data, My Port and Ip : " + myInfo.getPort() + "\t" + myInfo.getIpAddr());
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
