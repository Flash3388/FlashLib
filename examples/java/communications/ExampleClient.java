package examples.communications;

import java.io.IOException;
import java.net.InetAddress;

import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.EchoSendable;
import edu.flash3388.flashlib.communications.TcpCommInterface;
import edu.flash3388.flashlib.util.FlashUtil;

/*
 * Creates and runs a communications manager for a tcp client
 */
public class ExampleClient {

	public static void main(String[] args) throws IOException {
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
		int localPort = 11000;
		/*
		 * Remote port of the client
		 */
		int remotePort = 11000;
		
		/*
		 * Creates a TCP client communications interface for the address and ports we got earlier.
		 */
		TcpCommInterface clientInterface = new TcpCommInterface(localAddress, remoteAddress, localPort, remotePort);
		
		/*
		 * Creates a communications manager for the TCP communications interface 
		 */
		Communications client = new Communications("testing-client", clientInterface);
		
		/*
		 * Attaches a new sendable object to the communications manager. This is a pre-made echo sendable. We define
		 * the sendable type as 0 which the server will use to create a corresponding sendable.
		 */
		client.attach(new EchoSendable("Test1", (byte)0, true));
		
		/*
		 * Starts the communications manager
		 */
		client.start();
		
		/*
		 * While the communications manager is not connected
		 */
		while(!client.isConnected()) 
			FlashUtil.delay(1000);// delay for 1000 milliseconds, i.e. 1 second
		
		System.out.println("Connection");
		
		/*
		 * Counter to wait while connected.
		 */
		int count = 10;
		
		/*
		 * While the communications manager is connected. Decreases the value of the counter. When the counter
		 * reaches 0, the loop stops.
		 */
		while(client.isConnected() && (--count) > 0)
			FlashUtil.delay(1000);// delay for 1000 milliseconds, i.e. 1 second
		
		System.out.println("Not Connected");
		
		/*
		 * Terminates the communications manager
		 */
		client.close();
	}

}
