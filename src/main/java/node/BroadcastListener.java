package node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;


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
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                //String[] received = new String(packet.getData(), 0, packet.getLength()).split(";"); message splitter
                String received = new String(packet.getData(), 0, packet.getLength());
                InetAddress address = packet.getAddress();
                String ip = address.getHostAddress();
                System.out.println("Message recieved: " + received + " from: " + ip);
                System.out.println("Current Neighbours: " + neighbours);
                if (neighbours.contains(ip)) {
                	continue;
                }
                else {
                	neighbours.add(ip);
                    System.out.println(ip + " added to the Neighbours List");
                    String responseMessage = getIPAddressList().toString();
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
		System.out.println("Responding " + Message + " to " + address);
		socket.send(packet);
	}

    private static List<InetAddress> getIPAddressList() throws SocketException {
		List<InetAddress> addressList = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback() || !networkInterface.isUp()) {
				continue;
			}
			networkInterface.getInterfaceAddresses().stream()
				.map(a -> a.getAddress())
				.filter(Objects::nonNull)
				.forEach(addressList::add);
		}
		return addressList;
	}
}
