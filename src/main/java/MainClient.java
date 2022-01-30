import message.MessageReceiver;
import message.MessageSender;

import java.net.DatagramSocket;



public class MainClient {

    public static void main(String[] args) throws Exception {
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

class SecondClient{
    public static void main(String[] args) throws Exception {
        String host  = "DESKTOP-8DHRR0H";
        DatagramSocket socket = new DatagramSocket();
        MessageReceiver r = new MessageReceiver(socket);
        MessageSender s = new MessageSender(socket, host);
        Thread rt = new Thread(r);
        Thread st = new Thread(s);
        rt.start(); st.start();
        System.out.println("client instance started");
    }
}