package broadcast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import election.Bully;
import election.data.PeerInfo;


public class BroadcastListener extends Thread{

	static DatagramSocket socket;
	boolean running;
	byte[] buffer = new byte[512];
	static int port = 5024;
	List<PeerInfo> selfInfo;
	List<PeerInfo> peers;
	
	public List<PeerInfo> getPeers() {
		return peers;
	}

	public void setPeers(List<PeerInfo> peers) {
		this.peers = peers;
	}

	public BroadcastListener() {
		peers = new ArrayList<>(); 
		selfInfo = new ArrayList<>();
		try {
			socket = new DatagramSocket(port);
			setSelfInfo();
		}
		catch (SocketException ex) {
			System.out.println("Socket error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void setSelfInfo() throws SocketException {
		List<InetAddress> ipaddressesses = Helper.getIPAddressList();
		for(int i = 0; i < ipaddressesses.size(); i++)
		{
			PeerInfo newPeer = new PeerInfo();
			newPeer.setIpAddr(ipaddressesses.get(i));
			selfInfo.add(newPeer);
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
                InetAddress address = packet.getAddress();
				// if(peers.contains(address) && !address.isMulticastAddress()) {
				// 	continue;
				// }
				for(int i = 0;i<selfInfo.size(); i++)// Check it is not your own messsage
				{
					if (selfInfo.get(i).getIpAddr().equals(address)){
						selfmessage = true;
						break;
					}
					else{
						responseMessage = selfInfo.get(i).getIpAddr().getHostAddress();
					}
				}
				if(selfmessage == false) {					                  
					String[] received = new String(packet.getData(), 0, packet.getLength()).split(";");
					String type = received[0];
					String content = received[1];
					switch(type) {
						case "broadcast":	//Reply to the Client with List of peers"(Only the Leader should reply)
							PeerInfo leader = Bully.getLeader();			
							if(leader != null){
								if(selfInfo.contains(leader)){//TODO check if comparison is right
									byte[] msg;
									responseMessage = "reply;" + peers;// tostring??
									msg = responseMessage.getBytes();
									sendResponse(responseMessage, address, msg);
									for(int i = 0; i < peers.size(); i++)
									{
										responseMessage = "ack;" + address.getHostAddress();
										msg = responseMessage.getBytes();
										sendResponse(responseMessage, peers.get(i).getIpAddr(), msg);
									}
									PeerInfo newPeer = new PeerInfo();
									newPeer.setIpAddr(address);
									newPeer.setParticipent(true);
									peers.add(newPeer);
								}
								else{}// The Leader is another Node
							}
							else{}//This Node is not the leader
							break;

						case "reply":		//1-1 Add the address as Leader Peer and update local Peers(This is a Client who joined)
							PeerInfo newLeader = new PeerInfo();
							newLeader.setIpAddr(address);
							newLeader.setLeader(true);
							peers.add(newLeader);
							byte[] data = packet.getData();
							ByteArrayInputStream bis = new ByteArrayInputStream(data);
							bis.skip(6);
                    		ObjectInput in = new ObjectInputStream(bis);
                    		List<PeerInfo> receivedPeerInfo = (List<PeerInfo>) in.readObject();
							for(int i = 0; i < receivedPeerInfo.size(); i++)//TODO: deserialize object
							{
								PeerInfo newPeer = new PeerInfo();
								newPeer.setIpAddr(receivedPeerInfo.get(i).getIpAddr());
								newPeer.setParticipent(true);
								peers.add(newPeer);
							}
							break;

						case "ack":			//1-1 Add the address as a new Client Peer(This is another Client)
							PeerInfo newPeer = new PeerInfo();
							newPeer.setIpAddr(address);
							newPeer.setParticipent(true);
							peers.add(newPeer);
							break;
					}
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
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
