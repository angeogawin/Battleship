package tp7_threaded_server;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Class that allows a device to identify itself on the INTRANET.
 * 
 * @author Decoded4620 2016
 *         http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
 */
//mod2
public class NetIdentity {
	
	private static final boolean LOG = false;

	private String loopbackHost = "";
	private String host = "";

	private String loopbackIp = "";

	private String ip = "";
	private String hostAddress = "";

	public static final String IPV4_REGEX = "\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
	
	private Pattern pattern = Pattern.compile(IPV4_REGEX);

	private String macAddress;

	private boolean validIP(String ip) {
		if (ip == null || ip.isEmpty())
			return false;
		ip = ip.trim();
		if ((ip.length() < 6) & (ip.length() > 15))
			return false;
		try {
			Matcher matcher = pattern.matcher(ip);
			return matcher.matches();
		} catch (PatternSyntaxException ex) {
			return false;
		}
	}

	public NetIdentity() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface network = interfaces.nextElement();
				if (network != null) {
					Enumeration<InetAddress> addresses = network.getInetAddresses();
					if (LOG) {
						log("");
						log("\t- displayName: "+network.getDisplayName());
						log("\t- name: " + network.getName());
						log("\t- idx: " + network.getIndex());
						log("\t- max trans unit (MTU): " + network.getMTU());
						log("\t- is loopback: " + (network.isLoopback() ? "true" : "false"));
						log("\t- is PPP: " + (network.isPointToPoint() ? "true" : "false"));
						log("\t- isUp: " + (network.isUp() ? "true" : "false"));
						log("\t- isVirtual: " + (network.isVirtual() ? "true" : "false"));
						log("\t- supportsMulticast: " + (network.supportsMulticast() ? "true" : "false"));
						log("\t- macAddress: " + getMacAddress(network));
						log("-------------------------------------------------");
					}

					while (addresses.hasMoreElements()) {
						InetAddress address = addresses.nextElement();
						String hostAddr = address.getHostAddress();

						// local loopback
						if (hostAddr.indexOf("127.") == 0) {
							this.loopbackIp = address.getHostAddress();
							this.loopbackHost = address.getHostName();
						}

						// internal ip addresses (behind this router)
						if (hostAddr.indexOf("192.168") == 0 || hostAddr.indexOf("10.") == 0
								|| hostAddr.indexOf("172.16") == 0) {
							this.host = address.getHostName();
							this.ip = address.getHostAddress();
						}
						if (LOG)
							log("\t\t-" + address.getHostName() + ":" + address.getHostAddress() + " - "
									+ address.getAddress());
						if (network.isUp() && !network.isVirtual()) {
							String hAddress = new String(address.getHostAddress());
							if (validIP(hAddress)) {
								if (!network.getDisplayName().contains("VirtualBox")) {
									this.hostAddress = hAddress;
									this.macAddress=getMacAddress(network);
									if (LOG)
										log("*********  " + this.hostAddress);
								}
							}
						}
					}
				}
			}
		} catch (SocketException e) {

		}
		try {
			InetAddress loopbackIpAddress = InetAddress.getLocalHost();
			this.loopbackIp = loopbackIpAddress.getHostName();

		} catch (UnknownHostException e) {
			System.err.println("ERR: " + e.toString());
		}
	}

	public String getLoopbackHost() {
		return loopbackHost;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public String getHost() {
		return host;
	}

	public String getIp() {
		return ip;
	}

	public String getLoopbackIp() {
		return loopbackIp;
	}

	public String getHostAddress() {
		return hostAddress;
	}
	
	private static String getMacAddress(NetworkInterface network) {
		String macAddress = null;
		try {
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) 
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			macAddress = sb.toString();
		} catch (Exception e) {
			macAddress="n/a";
			//e.printStackTrace();
		}
		return macAddress;
	}
	
	
	public static void test_me(String[] args) {
		try {
			NetIdentity ni = new NetIdentity();
			if (LOG) {
				log("------------   result ---------------");
				log(ni.getHost());
				log(ni.getIp());
				log(ni.getLoopbackHost());
				log(ni.getLoopbackIp());
				log(ni.getHostAddress());
				log(ni.getMacAddress());
			}

		} catch (Exception e) {
          e.printStackTrace();
		}
	}

	private static void log(String m) {
		if (LOG)
			System.out.println(m);
	}
	
	

}