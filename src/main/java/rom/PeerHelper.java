package rom;

import election.Bully;
import election.data.PeerInfo;
import election.network.UDP;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PeerHelper {
    public enum PeerRole{
        CLIENT, SERVER, IDLE
    }
}


