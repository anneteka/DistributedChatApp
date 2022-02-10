package node;
import message.ClientMessageReceiver;
import message.ClientMessageSender;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;


public class ClientNode{

    public void start(String hostname, String id, String username, InetAddress server) throws SocketException {
        DatagramSocket socket = new DatagramSocket();
        ClientMessageSender s = new ClientMessageSender(socket, hostname, id, username, server);
        ClientMessageReceiver r = new ClientMessageReceiver(socket, s, id);
        Thread rt = new Thread(r);
        Thread st = new Thread(s);
        rt.start(); st.start();
    }
}
