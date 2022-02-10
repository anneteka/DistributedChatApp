package node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import message.MessageReceiver;
import message.MessageSender;

import java.io.IOException;
import java.net.SocketException;

@AllArgsConstructor
@Setter
@Getter
public class Node {
    private Role role;
    private MessageReceiver receiver;
    private MessageSender sender;
    private BroadcastListener bclistener;
    private BroadcastSender bcsender;
    private ServerNode serverNode;
    private ClientNode clientNode;
    private String hostname;

    public Node(){
        role = Role.undecided;
    }

    public void pingNode(){

    }

    public void broadcast() {
        bcsender = new BroadcastSender();
        bclistener = new BroadcastListener();
        Thread senderThread = new Thread(() -> {
            bcsender.broadcast();
        });      
        Thread listenerThread = new Thread(() -> {
            bclistener.run();
        });
        senderThread.start();
        listenerThread.start();
    }

    public void becomeServer() throws IOException {
        role = Role.server;
        if (serverNode == null) {
            serverNode = new ServerNode();
        }
        serverNode.start();
    }

    public void becomeClient() throws SocketException {
        role = Role.client;
        if (serverNode != null){
            // stop server thread so there is only one server always?
        }
        if (clientNode == null){
            clientNode = new ClientNode();
        }
        // whatever will do smth better later
        clientNode.start(hostname);
    }

    public void initiateVoting(){

    }

    public void vote(){

    }


}
