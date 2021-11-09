/**
 * 
 */
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Random;

/**
 * @author Eric
 *
 */
public class NoisyDatagramSocket extends DatagramSocket {
	
	private int dropRate = 0;
	private int drops = 0;
	Random rand = new Random(System.currentTimeMillis());
	
	/**
	 * @throws SocketException
	 */
	public NoisyDatagramSocket() throws SocketException {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param impl
	 */
	public NoisyDatagramSocket(DatagramSocketImpl impl) {
		super(impl);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param bindaddr
	 * @throws SocketException
	 */
	public NoisyDatagramSocket(SocketAddress bindaddr) throws SocketException {
		super(bindaddr);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param port
	 * @throws SocketException
	 */
	public NoisyDatagramSocket(int port) throws SocketException {
		super(port);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param port
	 * @param laddr
	 * @throws SocketException
	 */
	public NoisyDatagramSocket(int port, InetAddress laddr) throws SocketException {
		super(port, laddr);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the dropRate
	 */
	public int getDropRate() {
		return dropRate;
	}

	/**
	 * @param dropRate the dropRate to set
	 */
	public void setDropRate(int dropRate) {
		this.dropRate = dropRate;
	}

	public int getDrops() {
		return drops;
	}

	public void setDrops(int drops) {
		this.drops = drops;
	}

	@Override
	public void send(DatagramPacket arg0) throws IOException {
		// TODO Auto-generated method stub
		int tempRand = rand.nextInt(100);
		int tempDrop = this.getDropRate();
		System.out.println("\n************ Random: " + tempRand + " | DropRate: " + tempDrop + " *************");
		if(tempRand >= tempDrop) {
			super.send(arg0);
		}else {
			setDrops(getDrops()+1);
		}
	}

	
}
