package faulttolerance;

import broadcast.BroadcastListener;
import broadcast.Peers;
import election.Bully;
import election.data.PeerInfo;
import election.network.NetworkConstant;
import election.network.UDP;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class FaultToleranceServer {
    private static DatagramSocket sender = null;
    private static DatagramSocket receiver = null;
    private static FaultToleranceServer instacne = null;
    private static Peers receivedPeers;

    public static FaultToleranceServer getInstacne(){
        if(instacne == null){
            instacne = new FaultToleranceServer();
        }
        return instacne;
    }

    private FaultToleranceServer(){
        try {
            sender = new DatagramSocket();
            receiver =  new DatagramSocket(NetworkConstant.leaderMessageRecevierSyncPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Thread syncCompare = new Thread(){
        public void run() {
            try {
                while(true) {
                    Thread.sleep(1500);
                    BroadcastListener bcListener = BroadcastListener.getInstance();
                    Peers registeredPeers = bcListener.getPeers();
                    for (int i= 0; i < registeredPeers.getPeers().size(); i++)
                    {
                        if(!registeredPeers.getPeers().contains(receivedPeers.getPeers().get(i))) {//TODO check contains comparisson
                            System.out.println("Peer "+ receivedPeers.getPeers().get(i).getIpAddr().getHostAddress() + " sync wasn't received");
                            bcListener.removeClientFromPeers(receivedPeers.getPeers().get(i));
                            bcListener.sendACKForClients();
                        }
                        // if(registeredPeers.getPeers().get(i).getIpAddr() == receivedPeers.getPeers().get)
                        //     continue;
                        // else
                        //     ;
                    }
                    receivedPeers.clear();
                }
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }       
        }
    };

    public static Thread syncRecive = new Thread() {
        public void run() {
            try {
                boolean firstTime = true;
                while(true) {
                    byte[] buffer = null;
                    buffer = new byte[NetworkConstant.receiverBufferLength];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);                                       
                    receiver.receive(packet);
                    if(firstTime) {
                        firstTime = false;
                        syncCompare.start();
                    }
                    byte[] data = packet.getData();
                    InetAddress address = packet.getAddress();                    
                    Fault f = (Fault) UDP.deserializeByteArray(data);
                    PeerInfo newPeer = new PeerInfo();
					newPeer.setIpAddr(address);
					newPeer.setParticipent(true);
                    receivedPeers.addPeer(newPeer);
                    
                    if(f.getFlag() != Fault.Flag.SYNC_ALIVE_CLIENT){
                        throw new IOException("Sync Server ACK Failure");
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public static Thread syncSend = new Thread() {
        public void run() {
            try {
                while(true) {
                    Fault fault = new Fault();
                    fault.setFlag(Fault.Flag.SYNC_ALIVE_SERVER);
                    UDP.sendUdp(UDP.serializeToByteArray(fault), sender, InetAddress.getByName(NetworkConstant.multicastAddressSync), NetworkConstant.clientMessageRecevierSyncPort);
                    Thread.sleep(900);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}
