package election.network;

import election.data.ElectionInfo;

import java.io.*;
import java.net.*;
import java.util.List;

public class UDP {

    public static void broadcast(
            byte[] broadcastMessage, InetAddress address, int destPort ){

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            if(broadcastMessage.length > NetworkConstant.receiverBufferLength)
            {
                throw new IOException("Invalid sender size. Max Supported size for current configuratio is " + NetworkConstant.receiverBufferLength);
            }

            DatagramPacket packet
                    = new DatagramPacket(broadcastMessage, broadcastMessage.length, address, destPort);

            socket.send(packet);
            socket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendUdp(
            byte[] broadcastMessage, InetAddress address, int destPort ){

        try {
            DatagramSocket socket = new DatagramSocket();

            if(broadcastMessage.length > NetworkConstant.receiverBufferLength)
            {
                throw new IOException("Invalid sender size. Max Supported size for current configuratio is " + NetworkConstant.receiverBufferLength);
            }

            DatagramPacket packet
                    = new DatagramPacket(broadcastMessage, broadcastMessage.length, address, destPort);

            socket.send(packet);
            socket.close();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendUdp(
            byte[] broadcastMessage, DatagramSocket socket, InetAddress address, int destPort ){

        try {

            if(broadcastMessage.length > NetworkConstant.receiverBufferLength)
            {
                throw new IOException("Invalid sender size. Max Supported size for current configuratio is " + NetworkConstant.receiverBufferLength);
            }

            DatagramPacket packet
                    = new DatagramPacket(broadcastMessage, broadcastMessage.length, address, destPort);

            socket.send(packet);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] receiveUdp(DatagramSocket socket) throws IOException {
        boolean running = true;
        byte[] buf = null;

        buf = new byte[NetworkConstant.receiverBufferLength];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        InetAddress address = packet.getAddress();
        int port = packet.getPort();

        return buf;
    }

    public static byte[] serializeToByteArray(Object obj) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(obj);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    public static Object deserializeByteArray(byte[] stream){

        Object data = null;
        ObjectInput in = null;

        ByteArrayInputStream bis = new ByteArrayInputStream(stream);

        try {
            in = new ObjectInputStream(bis);
            data = in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }
}
