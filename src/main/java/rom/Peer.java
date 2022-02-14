package rom;

import election.data.PeerInfo;
import node.Node;
import rom.PeerHelper;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Peer {


    SendMessage sendMessage = null;
    ReceiveMessage receiveMessage = null;

    ReadWrite readWrite = new ReadWrite();

    private final AtomicBoolean stopReceiveThread = new AtomicBoolean(false);
    private final AtomicBoolean stopSendThread = new AtomicBoolean(false);

    PeerHelper.PeerRole role = PeerHelper.PeerRole.IDLE;
    ClientMode client = null;
    ServerMode server = null;

    private Clock clock = null;

    private static Peer instance = null;

    public static Peer getInstacne(){
        if(instance == null){
            instance = new Peer();
        }
        return instance;
    }

    private Peer(){
        stopReceiveThread.set(false);
        stopSendThread.set(false);
        clock = new Clock();
    }

    public void setRoleAndLeader(PeerHelper.PeerRole role, PeerInfo leader){
        this.role = role;

        if(role == PeerHelper.PeerRole.CLIENT) {
            client = new ClientMode(leader);
        }else if(role == PeerHelper.PeerRole.SERVER){
            server = new ServerMode(leader);
        }
    }

    public void startMessaging(){
        sendMessage = new SendMessage();
        sendMessage.start();

        receiveMessage = new ReceiveMessage();
        receiveMessage.start();
    }

    public void terminate() {
        stopReceiveThread.set(true);
        stopSendThread.set(true);
    }

    private class ReceiveMessage extends Thread {
        public void run() {
            while(!stopReceiveThread.get()){
                if(role == PeerHelper.PeerRole.CLIENT) {
                    MessageInfo info = client.receiveData();
                    readWrite.receive.print(client.format(info));
                }else if(role == PeerHelper.PeerRole.SERVER){
                    MessageInfo info = server.receiveData();
                    readWrite.receive.print(server.format(info));
                }
            }
        }
    }

    private class SendMessage extends Thread {

        public void run() {
            while(!stopSendThread.get()){
                if(role == PeerHelper.PeerRole.CLIENT) {
                    MessageInfo info = readWrite.transmit.read(clock.getNewMessageId());
                    client.sendData(info);
                }else if(role == PeerHelper.PeerRole.SERVER) {
                    server.sendData();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}