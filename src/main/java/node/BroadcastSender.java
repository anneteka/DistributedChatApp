package node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class BroadcastSender{

	private static DatagramSocket socket;
	private static int port = 5024;
	
	public static void broadcast(String message, String address) {
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			List<InetAddress> bcastaddressList = getBroadcastAddressList();						
	        for(int i = 0;i<bcastaddressList.size(); i++)
	        {
	        	String broadcastMessage = bcastaddressList.get(i).getHostAddress();
				byte[] bcast_msg = broadcastMessage.getBytes();
				broadcastPacket(broadcastMessage, bcastaddressList.get(i), bcast_msg);
	        }
		}
        catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
            ex.printStackTrace();
        }
		catch (SocketTimeoutException ex) {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        }
		catch (IOException ex) {
            System.out.println("BroadcastSender error: " + ex.getMessage());
            ex.printStackTrace();
        }
		finally {
            if (socket != null) {   
                socket.close();
            }
        }
    }

	private static void broadcastPacket(String broadcastMessage, InetAddress address, byte[] bcast_msg)
			throws UnknownHostException, IOException {
		DatagramPacket packet = new DatagramPacket(bcast_msg, bcast_msg.length, address, port);	
		System.out.println("Broadcasting " + broadcastMessage + " to " + address);
		socket.send(packet);
	}

    private static List<InetAddress> getBroadcastAddressList() throws SocketException {
		List<InetAddress> addressList = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback() || !networkInterface.isUp()) {
				continue;
			}
			networkInterface.getInterfaceAddresses().stream()
				.map(a -> a.getBroadcast())
				.filter(Objects::nonNull)
				.forEach(addressList::add);
		}
		return addressList;
	}
}