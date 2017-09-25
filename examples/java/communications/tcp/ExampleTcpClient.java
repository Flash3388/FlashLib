package examples.communications.tcp;

import java.io.IOException;
import java.net.InetAddress;

import edu.flash3388.flashlib.communications.Packet;
import edu.flash3388.flashlib.communications.TCPCommInterface;
import edu.flash3388.flashlib.util.FlashUtil;

/*
 * Example tcp client
 */
public class ExampleTcpClient {

	public static void main(String[] args) throws IOException{
		/*
		 * Gets the IP address of a computer with the hostname "hostname". This should be the remote address.
		 */
		InetAddress remoteAddress = InetAddress.getByName("hostname");
		/*
		 * Getting the local IP address of the interface connected to the remote ip
		 */
		InetAddress localAddress = FlashUtil.getLocalAddress(remoteAddress);
		/*
		 * Local port for communications
		 */
		int localPort = 11001;
		/*
		 * Remote port of the client
		 */
		int remotePort = 11000;
		
		/*
		 * Creates a TCP client communications interface for the address and ports we got earlier.
		 */
		TCPCommInterface clientInterface = new TCPCommInterface(localAddress, remoteAddress, localPort, remotePort);
		/*
		 * Sets the maximum amount of milliseconds to wait while reading. If data was not received during this
		 * time frame, reading is stopped.
		 */
		clientInterface.setReadTimeout(20);
		
		/*
		 * Creates a new packet instance. Packets is used to store read data from a CommInterface.
		 */
		Packet packet = new Packet();
		
		/*
		 * Performs connection to a remote source continuously until it is established
		 */
		while(!clientInterface.isConnected()){
			FlashUtil.delay(100);// 100 ms delay
			clientInterface.connect(packet); // connecting
		}
		
		/*
		 * Sending bytes of data to the server
		 */
		clientInterface.write("Hi".getBytes());
		/*
		 * Waiting 100 milliseconds before reading to insure data has arrived and response has had time to be
		 * received
		 */
		FlashUtil.delay(100);
		/*
		 * Reading data from the server. If received, we print it
		 */
		if(clientInterface.read(packet)){
			String data = new String(packet.data, 0, packet.length);
			System.out.println(data);
		}
		
		/*
		 * Disconnecting and closing the client
		 */
		clientInterface.close();
	}
}
