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

    public static void main(String[] args) throws Exception {
        //hostname of the server should be entered here. hostname is printed into the console after the server started
        String host = "DESKTOP-8DHRR0H";
        ClientNode node = new ClientNode();
        InetAddress address = InetAddress.getByName(host);
        node.start(host, "test-client-node-"+ (new Random()).nextInt(), "test-user-1", address);
        System.out.println("client instance started");
    }
}
