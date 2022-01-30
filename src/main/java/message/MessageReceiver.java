package message;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MessageReceiver implements Runnable {
    DatagramSocket socket;
    final byte[] buffer;

    public MessageReceiver(DatagramSocket s) {
        socket = s;
        buffer = new byte[1024];
    }

    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}