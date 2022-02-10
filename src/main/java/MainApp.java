import election.Bully;
import node.Node;

import java.net.SocketException;

public class MainApp {
    public static void main(String[] args) throws SocketException {
        //Bully election has to be started first
        Bully leaderElection = new Bully();
        //If leader is not detected then we need to start the election
        leaderElection.startElection();

        Node appNode = new Node();
        // set hostname here
        // should be set automatically sometime after connecting to the network
        //This should be called when application is closing
        leaderElection.stopPolling();
        //appNode.setHostname("DESKTOP-8DHRR0H");       
        //appNode.becomeClient();
        appNode.broadcast();
    }
}
