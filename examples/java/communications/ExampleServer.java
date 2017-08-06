package examples.communications;

import java.io.IOException;
import java.net.InetAddress;

import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.EchoSendable;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.communications.SendableCreator;
import edu.flash3388.flashlib.communications.TcpCommInterface;
import edu.flash3388.flashlib.util.FlashUtil;

/*
 * Creates and runs a communications manager for a tcp server
 */
public class ExampleServer {

	public static void main(String[] args) throws IOException {
		/*
		 * Gets the IP address of a computer with the hostname "hostname". This should be the local address.
		 */
		InetAddress address = InetAddress.getByName("hostname");
		/*
		 * Local port to listen to for connection
		 */
		int localPort = 11000;
		
		/*
		 * Creates a TCP server communications interface for the address and ports we got earlier.
		 */
		TcpCommInterface serverInterface = new TcpCommInterface(address, localPort);
		
		/*
		 * Creates a communications manager for the TCP communications interface 
		 */
		Communications server = new Communications("testing-server", serverInterface);
		
		/*
		 * Adds a new SendableCreator object which is used to create sendables by type whenever a new remote sendable
		 * is detected with no corresponding local sendable
		 */
		server.setSendableCreator(new SendableCreator(){
			@Override
			public Sendable create(String name, int id, byte type) {
				if(type == 0)// when the type is '0' we expect to use an echo sendable.
					return new EchoSendable(name, id, type);
				return null;
			}
		});
		
		/*
		 * Starts the communications manager
		 */
		server.start();
		
		/*
		 * While the communications manager is not connected
		 */
		while(!server.isConnected()) 
			FlashUtil.delay(1000);// delay for 1000 milliseconds, i.e. 1 second
		
		System.out.println("Connection");
		
		/*
		 * While the communications manager is connected
		 */
		while(server.isConnected())
			FlashUtil.delay(1000);// delay for 1000 milliseconds, i.e. 1 second
		
		System.out.println("Not Connected");
		
		/*
		 * Terminates the communications manager
		 */
		server.close();
	}

}
