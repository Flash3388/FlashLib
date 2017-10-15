package examples.communications.tcp;

import java.io.IOException;
import java.net.InetAddress;

import edu.flash3388.flashlib.communications.Packet;
import edu.flash3388.flashlib.communications.TCPCommInterface;
import edu.flash3388.flashlib.util.FlashUtil;

/*
 * Example tcp server 
 */
public class ExampleTcpServer {
	
	public static void main(String[] args) throws IOException{
		/*
		 * Gets the local IP address
		 */
		InetAddress address = InetAddress.getLocalHost();
		/*
		 * Local port to listen to for connection
		 */
		int localPort = 11000;
		
		/*
		 * Creates a TCP server communications interface for the address and ports we got earlier.
		 */
		TCPCommInterface serverInterface = new TCPCommInterface(address, localPort);
		/*
		 * Sets the maximum amount of milliseconds to wait while reading. If data was not received during this
		 * time frame, reading is stopped.
		 */
		serverInterface.setReadTimeout(20);
		
		/*
		 * Creates a new packet instance. Packets is used to store read data from a CommInterface.
		 */
		Packet packet = new Packet();
		/*
		 * We put the server into connection acceptance mode which blocks our code until a connection was established 
		 * or was interrupted 
		 */
		serverInterface.connect(packet);
		
		/*
		 * While connected, lets check for new data and print if received
		 */
		while(serverInterface.isConnected()){
			/*
			 * Reading data from the server. If received, we print it
			 */
			if(serverInterface.read(packet)){
				String data = new String(packet.data, 0, packet.length);
				System.out.println(data);
			}
			
			/*
			 * A small delay of 10 milliseconds to not strain the current thread
			 */
			FlashUtil.delay(10);
		}
		
		
		/*
		 * Disconnecting and closing the server
		 */
		serverInterface.close();
	}
}
