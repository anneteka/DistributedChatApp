package node;
import message.MessageReceiver;
import message.MessageSender;

import java.net.DatagramSocket;
import java.net.SocketException;


public class ClientNode{

    public void start(String hostname) throws SocketException {
        DatagramSocket socket = new DatagramSocket();
        MessageReceiver r = new MessageReceiver(socket);
        MessageSender s = new MessageSender(socket, hostname);
        Thread rt = new Thread(r);
        Thread st = new Thread(s);
        rt.start(); st.start();
    }

    public static void main(String[] args) throws Exception {
        //hostname of the server should be entered here. hostname is printed into the console after the server started
        String host = "DESKTOP-8DHRR0H";
        DatagramSocket socket = new DatagramSocket();
        MessageReceiver r = new MessageReceiver(socket);
        MessageSender s = new MessageSender(socket, host);
        Thread rt = new Thread(r);
        Thread st = new Thread(s);
        rt.start(); st.start();
        System.out.println("client instance started");
    }
}
