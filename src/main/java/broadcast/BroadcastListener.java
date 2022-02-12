package broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import election.data.PeerInfo;


public class BroadcastListener extends Thread{

	static DatagramSocket socket;
	boolean running;
	byte[] buffer = new byte[512];
	static int port = 5024;
	List<PeerInfo> peers;
	
	public List<PeerInfo> getPeers() {
		return peers;
	}

	public void setPeers(List<PeerInfo> peers) {
		this.peers = peers;
	}

	public BroadcastListener() {
		peers = new ArrayList<>(); 
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
				if(peers.contains(address) && !address.isMulticastAddress()) {
					continue;
				}
				System.out.println("Message recieved: " + received + " from: " + address.getHostAddress());
                System.out.println("Current Peers: " + peers);
				List<InetAddress> ipaddressesses = Helper.getIPAddressList();
				for(int i = 0;i<ipaddressesses.size(); i++)
				{
					if (ipaddressesses.get(i).equals(address)){
						selfmessage = true;
						break;
					}
					else
						responseMessage = ipaddressesses.get(i).getHostAddress();
				}
				if(selfmessage == false) {
					//A Broadcast message is received
					PeerInfo newPeer = new PeerInfo();
					newPeer.setIpAddr(address);
					peers.add(newPeer);
					System.out.println(address.getHostAddress() + " added to the Peers List");
					byte[] msg = responseMessage.getBytes();
					sendResponse(responseMessage, address, msg);
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
