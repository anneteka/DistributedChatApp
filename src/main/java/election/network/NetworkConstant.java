package election.network;

public class NetworkConstant {

    //public static final String broadcastAddress = "255.255.255.255";

    public static final Integer leaderDestPort = 6611;

    public static final Integer leaderMessageReceiverPort = 8080;
    public static final Integer leaderMessageSenderPort = 7722;

    public static final Integer clientMessageReceiverPort = 7711;
    public static final Integer clientMessageSenderPort = 7722;

    public static final Integer receiverBufferLength = 1024;

    public static final String multicastAddress = "230.0.0.0";

    public static final Integer leaderMessageRecevierSyncPort = 7733;
    public static final Integer clientMessageRecevierSyncPort = 7744;
    public static final String multicastAddressSync = "230.0.0.1";
}
