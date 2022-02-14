package faulttolerance;

import java.io.Serializable;

public class Fault implements Serializable {
    public enum Flag{
        SYNC_ALIVE_CLIENT, SYNC_ALIVE_SERVER, IDLE
    }

    public Fault(){
        this.flag = Flag.IDLE;
    }

    private Flag flag;

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }
}
