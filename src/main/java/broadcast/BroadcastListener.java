package broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import election.Bully;
import election.data.PeerInfo;
import election.network.UDP;
import faulttolerance.FaultListener;


public class BroadcastListener extends Thread{

	static DatagramSocket socket;
	boolean running;
	byte[] buffer = new byte[1024];
	static int port = 5024;
	List<PeerInfo> selfInfo;
	Peers peers;
	FaultListener faultListener;

	private static BroadcastListener instance = null;

	public static BroadcastListener getInstance(){
		if(instance == null){
			instance = new BroadcastListener();
		}
		return instance;
	}

	public PeerInfo getLeader(){
		for(int i = 0; i < this.getPeersSize(); i++){
			if(peers.getPeers().get(i).isLeader()){
				return peers.getPeers().get(i);
			}
		}
		return null;
	}

	public Integer getPeersSize(){
		return peers.getPeers().size();
	}

	public Peers getPeers(){
		return peers;
	}

	private BroadcastListener() {
		selfInfo = new ArrayList<>();
		try {
			peers = new Peers();
			socket = new DatagramSocket(port);
			setSelfInfo();
			faultListener = FaultListener.getInstance();
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

				byte[] data = packet.getData();
				Peers localPeers = (Peers) UDP.deserializeByteArray(data);

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

					switch(localPeers.getFlag()) {
						case BROADCAST:	//Reply to the Client with List of peers"(Only the Leader should reply)
						{
							PeerInfo leader = Bully.getInstance().getLeader();

							if (leader.isLeader()) {

								// Adding client address
								PeerInfo newPeer = new PeerInfo();
								newPeer.setIpAddr(address);
								newPeer.setParticipent(true);
								peers.addPeer(newPeer);

								// This is for ACK
								peers.setFlag(Peers.Flag.ACK);
								for (int i = 0; i < getPeersSize(); i++) {									
									sendResponse(responseMessage, peers.getPeers().get(i).getIpAddr(), UDP.serializeToByteArray(peers));
								}
								FaultListener faultListener = FaultListener.getInstance();
								Thread faultListenerlistenThread = new Thread(() -> {
									faultListener.listen();
								});								
								Thread faultListenerrunThread = new Thread(() -> {
									faultListener.run();
								});								
								faultListenerrunThread.start();
								faultListenerlistenThread.start();

							} else {
							}//This Node is not the leader
						}
						break;
						case  ACK:			//1-1 Add the address as a new Client Peer(This is another Client)
						{
							peers.clear();
							PeerInfo newLeader = new PeerInfo();
							newLeader.setIpAddr(address);
							newLeader.setLeader(true);
							peers.addPeer(newLeader);

							for (int i = 0; i < localPeers.getPeers().size(); i++)//TODO: deserialize object
							{
								peers.addPeer(localPeers.getPeers().get(i));
							}
							//After receiving information from Learder, listen for ALIVE messages
							Thread faultListenerThread = new Thread(() -> {
								faultListener.listen();
							});
							faultListenerThread.start();
							System.out.println("Connected Nodes are Begin : ");
							for(int i = 0; i <this.getPeersSize(); i++){
								System.out.println(this.getPeers().getPeers().get(i).getIpAddr().getHostAddress());
							}
							System.out.println("Connected Nodes are End: ");
						}
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
        } finally {
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
