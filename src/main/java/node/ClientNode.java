package node;
import message.ClientMessageReceiver;
import message.ClientMessageSender;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;


public class ClientNode{
    private ClientMessageSender s;
    private ClientMessageReceiver r;
    Thread rt;
    Thread st;
    public void start(String hostname, String id, String username, InetAddress server) throws SocketException {
        DatagramSocket socket = new DatagramSocket(8080);
        s = new ClientMessageSender(socket, hostname, id, username, server);
        r = new ClientMessageReceiver(socket, s, id);
        rt = new Thread(r);
        st = new Thread(s);
        rt.start(); st.start();
    }
    public void setSetverAddress(InetAddress address){
        s.setServerAddress(address);
    }

    public void stop() {
        s.setCLient(false);
        s.setCLient(false);
    }
}
