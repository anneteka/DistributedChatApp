package broadcast;

import election.data.PeerInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Peers implements Serializable {
    public enum Flag {
        ACK, BROADCAST, EMPTY
    }

    private List<PeerInfo> peers;
    private Flag flag = Flag.EMPTY;

    public Peers() {
        peers = new ArrayList<PeerInfo>();
    }

    public List<PeerInfo> getPeers() {
        return peers;
    }

    public void addPeer(PeerInfo peer) {
        this.peers.add(peer);
    }

    public void clear(){
        this.peers.clear();;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

}
