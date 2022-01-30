import message.MessageReceiver;
import message.MessageSender;

import java.net.DatagramSocket;



public class MainClient {

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

class SecondClient{
    public static void main(String[] args) throws Exception {
        // you can also start a second client
        // i am not sure if all of this is working on multiple computers but it is a start
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