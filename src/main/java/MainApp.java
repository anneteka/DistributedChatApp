import node.Node;

import java.net.SocketException;

public class MainApp {
    public static void main(String[] args) throws SocketException {
        Node appNode = new Node();
        // set hostname here
        // should be set automatically sometime after connecting to the network
        appNode.joinNetwork();
        appNode.connectToServer();
    }
}
