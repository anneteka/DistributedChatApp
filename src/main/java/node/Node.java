package node;

import broadcast.BroadcastListener;
import broadcast.BroadcastSender;
import election.Bully;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import message.ClientMessageReceiver;
import message.ClientMessageSender;
import rom.Peer;
import rom.PeerHelper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@AllArgsConstructor
@Setter
@Getter
public class Node {
    private Role role;
    private BroadcastListener bclistener;
    private BroadcastSender bcsender;
    private Bully leaderElection;
    private ClientMessageReceiver receiver;
    private ClientMessageSender sender;
    private ServerNode serverNode;
    private ClientNode clientNode;
    private String hostname;
    private String deviceId = "client=device-id-generated";
    private String username = "client-user";
    private InetAddress serverAddress;
    private Peer peer;

    public Node() {
        role = Role.undecided;
        this.bcsender = new BroadcastSender();
        this.bclistener = BroadcastListener.getInstance();
        this.leaderElection = Bully.getInstance();
        this.peer = Peer.getInstacne();
    }

    public Node(
            Role role, ClientMessageReceiver receiver, ClientMessageSender sender, ServerNode serverNode,
            ClientNode clientNode, String hostname, String deviceId, InetAddress address
    ) {
        this.role = role;
        this.receiver = receiver;
        this.sender = sender;
        this.serverNode = serverNode;
        this.clientNode = clientNode;
        this.hostname = hostname;
        this.deviceId = deviceId;
        this.serverAddress = address;

        this.leaderElection = Bully.getInstance();
        this.bcsender = new BroadcastSender();
        this.bclistener = BroadcastListener.getInstance();
        this.peer = Peer.getInstacne();
    }

    public void terminate(){
        leaderElection.stopPolling();
    }

    public void pingNode() {

    }

    public void discover(){            
        Thread listenerThread = new Thread(() -> {
            bclistener.run();
        });
        listenerThread.start();
        bcsender.broadcast();
        for(int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print(".");
        }
        System.out.print("\n");

        if(bclistener.getPeersSize() == 0) {
            System.out.println("\nNo other Node was discovered");
            startElection();
        }

        startMessaging();
    }

    public void startElection()
    {
        leaderElection.startElection();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startMessaging(){
        if(leaderElection.amILeader()){
            peer.setRole(PeerHelper.PeerRole.SERVER);
        }else {
            peer.setRole(PeerHelper.PeerRole.CLIENT);
        }

        peer.startMessaging();
    }

    public void becomeServer() throws IOException {
        role = Role.server;
        if (clientNode != null){
            clientNode.stop();
        }
        serverNode = new ServerNode();
        System.out.println("became a server");
    }

    public void becomeClient() throws SocketException {
        role = Role.client;
        //server address should be reassigned if server changed
        serverAddress = bclistener.getLeader().getIpAddr();
        if (clientNode == null) {
            clientNode = new ClientNode();
            clientNode.start(hostname, deviceId, username, serverAddress);
        } else {
            clientNode.setSetverAddress(serverAddress);
            System.out.println("assigned new server");
        }
        // whatever will do smth better later
//        hostname = "100.117.151.186";
//        try {
//            serverAddress = InetAddress.getByName(hostname);
//            System.out.println(serverAddress);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }


    }

    public void initiateVoting() {

    }

    public void vote() {

    }

    public void showMessage(message.Message msg) {

    }

    public String findServer() {
        return "server host name to connect to or null if there is no server";
    }

    // determines the network it is connected to
    // saves the data about it that is needed
    public void joinNetwork() {
    }

    // sends broadcast message to find the server
    // if no server is found initiates voting
    public void connectToServer() throws SocketException {
        String hostConnection = findServer();
        if (hostConnection == null) {
            initiateVoting();
        } else {
            hostname = hostConnection;
            becomeClient();
        }
    }
}
