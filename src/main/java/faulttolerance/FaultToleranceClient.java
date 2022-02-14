package faulttolerance;

import broadcast.BroadcastListener;
import election.Bully;
import election.network.NetworkConstant;
import election.network.UDP;
import rom.MessageInfo;
import rom.Peer;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class FaultToleranceClient {
    private static DatagramSocket sender = null;
    private static MulticastSocket receiver = null;
    private static FaultToleranceClient instacne = null;

    public static FaultToleranceClient getInstacne(){
        if(instacne == null){
            instacne = new FaultToleranceClient();
        }
        return instacne;
    }

    private FaultToleranceClient(){
        try {
            sender = new DatagramSocket();
            receiver =  new MulticastSocket(NetworkConstant.clientMessageRecevierSyncPort);
            InetAddress group = InetAddress.getByName(NetworkConstant.multicastAddressSync);
            receiver.joinGroup(group);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Thread syncRecive = new Thread() {
        public void run() {
            try {
                while(true) {
                    receiver.setSoTimeout(1500);
                    byte[] data = UDP.receiveUdp(receiver);
                    Fault f = (Fault) UDP.deserializeByteArray(data);
                    if(f.getFlag() != Fault.Flag.SYNC_ALIVE_SERVER){
                        throw new IOException("Sync Server ACK Failure");
                    }
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout : Unable to sync with leader. Starting election");
                Bully.getInstance().startElection();
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
                    fault.setFlag(Fault.Flag.SYNC_ALIVE_CLIENT);
                    UDP.sendUdp(UDP.serializeToByteArray(fault), sender, InetAddress.getByName(BroadcastListener.getInstance().getLeader().getIpAddr().getHostAddress()), NetworkConstant.leaderMessageRecevierSyncPort);
                    Thread.sleep(1800);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}
