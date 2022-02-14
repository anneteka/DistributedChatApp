package faulttolerance;

import broadcast.BroadcastListener;
import election.Bully;
import election.network.NetworkConstant;
import election.network.UDP;

import java.io.IOException;
import java.net.*;

public class FaultToleranceServer {
    private static DatagramSocket sender = null;
    private static DatagramSocket receiver = null;
    private static FaultToleranceServer instacne = null;

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

    public static Thread syncRecive = new Thread() {
        public void run() {
            try {
                while(true) {
                    receiver.setSoTimeout(2000);
                    byte[] data = UDP.receiveUdp(receiver);
                    Fault f = (Fault) UDP.deserializeByteArray(data);
                    //list
                    //compare with peers
                    if(f.getFlag() != Fault.Flag.SYNC_ALIVE_CLIENT){
                        throw new IOException("Sync Server ACK Failure");
                    }
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout : Unable to sync with client.");
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
