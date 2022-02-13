import node.Node;

import java.net.SocketException;

public class MainApp {
    public static void main(String[] args) throws SocketException {
        Node appNode = new Node();
        //Start the dynamic discovery
        appNode.discover();
        //appNode.becomeServer();
        //appNode.becomeClient();
        //Bully election has to be started first
        //If leader is not detected then we need to start the election
        // set hostname here
        // should be set automatically sometime after connecting to the network
        //appNode.setHostname("DESKTOP-8DHRR0H");
    }
}
