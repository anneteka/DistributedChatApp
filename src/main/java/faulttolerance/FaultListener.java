package faulttolerance;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import election.network.UDP;

public class FaultListener extends Thread{

    public enum Flag {
        ALIVE, REPLY, EMPTY
    }

    static DatagramSocket socket;
    static int port;
    boolean running;
    byte[] buffer = new byte[1024];

    private static FaultListener instance = null;

	public static FaultListener getInstance(){
		if(instance == null){
			instance = new FaultListener();
		}
		return instance;
	}

    private FaultListener() {
        try {
            socket = new DatagramSocket();
            port = socket.getPort();       
            socket.setSoTimeout(2000);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run(InetAddress address) {		
        running = true;
        try {
            while (running) {                    
                sendPacket(Flag.ALIVE.toString(), address, UDP.serializeToByteArray(Flag.ALIVE));
                Thread.sleep(1000);
            }
        }
        catch (SocketException ex) {
			System.out.println("Socket error: " + ex.getMessage());
			ex.printStackTrace();
		}
        catch (IOException ex) {
            System.out.println("FaultListener error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void listen(){
        running = true;
        try {
            while (running) {                    
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
				byte[] data = packet.getData();
				Flag localFlag = (Flag) UDP.deserializeByteArray(data);
                switch(localFlag){
                    case ALIVE:{
                        sendPacket(Flag.REPLY.toString(), address, UDP.serializeToByteArray(Flag.REPLY));//send reply message meaning its alive 
                    }
                    break;
                    case REPLY:{
                        System.out.println("Received reply message from "+ address.getHostAddress());
                    }
                    break;
                }
            }
        }
        catch (SocketTimeoutException ex) {
            System.out.println("Socket timeout: " + ex.getMessage());
            //TODO Leader stoped sending messages, new election should be made
        }
        catch (SocketException ex) {
			System.out.println("Socket error: " + ex.getMessage());
			ex.printStackTrace();
		}
        catch (IOException ex) {
            System.out.println("FaultListener error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void sendPacket(String Message, InetAddress address, byte[] msg)
			throws UnknownHostException, IOException {
		DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);	
		System.out.println("Sending " + Message + " to " + address.getHostAddress());
		socket.send(packet);
	}
}
