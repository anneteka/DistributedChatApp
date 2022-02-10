package node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class BroadcastListener extends Thread{

	public static DatagramSocket socket;
	boolean running;
	byte[] buffer = new byte[512];
	static int port = 5024;
	List<String> neighbours;
	
	public BroadcastListener() {
		neighbours = new ArrayList<>(); 
		try {
			socket = new DatagramSocket(port);
		}
		catch (SocketException ex) {
			System.out.println("Socket error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
 
	public void run() {
        running = true;     
        try {
            while (running) {        
				String responseMessage = "";   
				Boolean selfmessage = false;   
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                InetAddress address = packet.getAddress();
                String ip = address.getHostAddress();
                System.out.println("Message recieved: " + received + " from: " + ip);
                System.out.println("Current Neighbours: " + neighbours);
				List<InetAddress> ipaddressesses = Helper.getIPAddressList();
				if(neighbours.contains(ip) && !address.isMulticastAddress()) {
					continue;
				}
				for(int i = 0;i<ipaddressesses.size(); i++)
				{
					if (ipaddressesses.get(i).getHostAddress().equals(ip)){
						selfmessage = true;
						break;
					}
					else
						responseMessage = ipaddressesses.get(i).getHostAddress();
				}
				if(selfmessage == false) {
					neighbours.add(ip);
					System.out.println(ip + " added to the Neighbours List");
					byte[] msg = responseMessage.getBytes();
					sendResponse(responseMessage, InetAddress.getByName(ip), msg);
				}
			}
        }
        catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
            ex.printStackTrace();
        }
        catch (IOException ex) {
            System.out.println("BroadcastListener error: " + ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            if (socket != null) {
            	socket.close();
            }
        }
    }

    private static void sendResponse(String Message, InetAddress address, byte[] msg)
			throws UnknownHostException, IOException {
		DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);	
		System.out.println("Responding " + Message + " to " + address.getHostAddress());
		socket.send(packet);
	}

}
