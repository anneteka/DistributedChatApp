package broadcast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class Helper {
    
    public static List<InetAddress> getIPAddressList() throws SocketException {
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

    public static List<InetAddress> getBroadcastAddressList() throws SocketException {
		List<InetAddress> addressList = new ArrayList<>();
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isLoopback() || !networkInterface.isUp()) {
				continue;
			}
			networkInterface.getInterfaceAddresses().stream()
				.map(a -> a.getBroadcast())
					.filter( Objects::nonNull)
				.filter(x -> {
                    if (x.getHostAddress().equals("0.0.0.0")) {
                        return false;
                    }
                    return true;
                })
				.forEach(addressList::add);
		}
		return addressList;
	}
}
