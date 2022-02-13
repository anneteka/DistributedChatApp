package election.data;

import java.io.Serializable;
import java.net.*;
import java.util.UUID;

public class PeerInfo implements Serializable {
    private UUID uniqueIdentifier;
    private boolean participent;
    private boolean leader;
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

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
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

    @Override
    public String toString() {
        return "PeerInfo [UUID='" + uniqueIdentifier + "', ipAddr='" + ipAddr.getHostName() + "']";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PeerInfo other = (PeerInfo) obj;
        if (ipAddr != other.ipAddr)
            return false;
        if (uniqueIdentifier == null) {
            if (other.uniqueIdentifier != null)
            //TODO UUID may need to be the same in The in BroadcastListener class
                return true;
                //return false;
        } else if (!uniqueIdentifier.equals(other.uniqueIdentifier))
            return false;
        return true;
    }
}
