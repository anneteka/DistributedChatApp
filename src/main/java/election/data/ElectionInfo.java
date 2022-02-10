package election.data;

import java.io.Serializable;

public class ElectionInfo implements Serializable {

    public enum Election { IDLE, REGISTRATION_START, REGISTER, BULLY_FOR_ELECTION, LEADER }

    private PeerInfo peerInfo;
    private Election electionSate;

    public ElectionInfo(PeerInfo info) {
        this.peerInfo = info;
        this.electionSate = Election.IDLE;
    }

    public Election getElectionSate() {
        return electionSate;
    }

    public void setElectionSate(Election electionSate) {
        this.electionSate = electionSate;
    }

    public PeerInfo getPeerInfo() {
        return peerInfo;
    }

    public void setPeerInfo(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
    }
}
