package node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import message.MessageReceiver;
import message.MessageSender;
import node.examples.Message;

import java.io.IOException;
import java.net.SocketException;

@AllArgsConstructor
@Setter
@Getter
public class Node {
    private Role role;
    private MessageReceiver receiver;
    private MessageSender sender;
    private ServerNode serverNode;
    private ClientNode clientNode;
    private String hostname;
    private String deviceId;

    public Node(){
        role = Role.undecided;
    }

    public void pingNode(){

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

    public void broadcast(){

    }

    public void showMessage(Message msg){

    }

    public String findServer(){
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
        if (hostConnection == null){
            initiateVoting();
        } else {
            hostname = hostConnection;
            becomeClient();
        }
    }
}
