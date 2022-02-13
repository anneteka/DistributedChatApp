package broadcast;

import election.network.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

public class BroadcastSender{

	boolean running;
	private static DatagramSocket socket;
	private static int port = 5024;
	
	public void broadcast() {	
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
			List<InetAddress> bcastaddressList = Helper.getBroadcastAddressList();
			List<InetAddress> ipaddressList = Helper.getIPAddressList();
			for(int i = 0;i<bcastaddressList.size(); i++)
			{
				Peers localPeer = new Peers();
				localPeer.setFlag(Peers.Flag.BROADCAST);
				broadcastPacket( ipaddressList.get(i).getHostAddress(),bcastaddressList.get(i), UDP.serializeToByteArray(localPeer));
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
		System.out.println("Broadcasting " + broadcastMessage + " to " + address.getHostAddress());
		socket.send(packet);
	}

}