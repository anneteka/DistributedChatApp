package node;

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
	int attemps = 10;
	
	public void broadcast() {
		running = true;		
		try {
			while(running){
				socket = new DatagramSocket();
				socket.setBroadcast(true);
				List<InetAddress> bcastaddressList = Helper.getBroadcastAddressList();
				List<InetAddress> ipaddressList = Helper.getIPAddressList();
				for(int i = 0;i<bcastaddressList.size(); i++)
				{
					String broadcastMessage = ipaddressList.get(i).getHostAddress();
					byte[] bcast_msg = broadcastMessage.getBytes();
					broadcastPacket(broadcastMessage, bcastaddressList.get(i), bcast_msg);
				}
				attemps--;
				if(attemps==0)
					break;
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