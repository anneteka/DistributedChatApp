package election.data;

import java.io.Serializable;
import java.net.*;
import java.util.UUID;

public class PeerInfo implements Serializable {
    private UUID uniqueIdentifier;
    private boolean participent;
    private InetAddress ipAddr;
    private int port;

    public UUID getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(UUID uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public boolean isParticipent() {
        return participent;
    }

    public void setParticipent(boolean participent) {
        this.participent = participent;
    }

    public InetAddress getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(InetAddress ipAddr) {
        this.ipAddr = ipAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
