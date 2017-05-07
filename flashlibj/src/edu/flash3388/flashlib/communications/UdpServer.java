package edu.flash3388.flashlib.communications;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.util.FlashUtil;

public class UdpServer {

	private static class ReadTask implements Runnable{

		private boolean stop = false;
		private UdpServer server;
		
		public ReadTask(UdpServer server){
			this.server = server;
		}
		
		@Override
		public void run() {
			while (!stop) {
				server.read();
			}
		}
		
		public void stop(){
			stop = true;
		}
	}
	private static class SendTask implements Runnable{

		private boolean stop = false;
		private UdpServer server;
		
		public SendTask(UdpServer server){
			this.server = server;
		}
		
		@Override
		public void run() {
			while (!stop){
				for(Enumeration<Client> eClients = server.clients.elements(); eClients.hasMoreElements();){
					Client client = eClients.nextElement();
					if(FlashUtil.millis() - client.lastRead >= 1/*connection timeout*/){
						client.readTimeouts++;
						FlashUtil.getLog().logTime(client.name+"- TIMEOUT " + client.readTimeouts);
						client.lastRead = FlashUtil.millis();
					}
					if(client.readTimeouts >= 3){
						FlashUtil.getLog().logTime(client.name+"- Client disconnected");
						client.connected = false;
						client.onDisconnect();
						continue;
					}
					if(!client.sendAll(server))
						server.writeHandshake(client);
						
				}
			}
		}
		
		public void stop(){
			stop = true;
		}
	}
	private static class Client{
		Vector<Sendable> sendables = new Vector<Sendable>();
		InetAddress address;
		int port, readTimeouts = 0;
		long lastRead;
		boolean connected = false;
		String name;
		
		Sendable getSendableByID(int id){
			Enumeration<Sendable> sendablesEnum = sendables.elements();
			while(sendablesEnum.hasMoreElements()){
				Sendable sen = sendablesEnum.nextElement();
				if(sen.getID() == id)
					return sen;
			}
			return null;
		}
		void resetAll(){
			Enumeration<Sendable> sendablesEnum = sendables.elements();
			while(sendablesEnum.hasMoreElements()){
				Sendable sen = sendablesEnum.nextElement();
				sen.setRemoteInit(false);
				sen.onConnection();
			}
		} 
		void onDisconnect(){
			Enumeration<Sendable> sendablesEnum = sendables.elements();
			while(sendablesEnum.hasMoreElements()){
				Sendable sen = sendablesEnum.nextElement();
				sen.onConnectionLost();
			}
		} 
		boolean sendAll(UdpServer server){
			Enumeration<Sendable> sendablesEnum = sendables.elements();
			int sends = 0;
			while(sendablesEnum.hasMoreElements()){
				Sendable sen = sendablesEnum.nextElement();
				
				if(!sen.remoteInit()){
					byte[] bytes = sen.getName().getBytes();
					server.send(bytes, sen, this);
					sends++;
					sen.setRemoteInit(true);
					continue;
				}
				
				byte[] dataB;
				
				if(!sen.hasChanged() || (dataB = sen.dataForTransmition()) == null) continue;
				server.send(dataB, sen, this);
				sends++;
			}
			return sends > 0;
		}
	}
	
	private Vector<Client> clients = new Vector<Client>();
	
	private Packet packet = new Packet();
	private UdpCommInterface readInterface;
	private SendableCreator sendableCreator;
	private String logName;
	
	private Thread sendThread, readThread;
	private SendTask sendTask;
	private ReadTask readTask;
	
	public UdpServer(String name, InetAddress addr, int port){
		this.logName = name+"-Comm";
		
		try {
			readInterface = new UdpCommInterface(addr, port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		sendTask = new SendTask(this);
		readTask = new ReadTask(this);
		sendThread = new Thread(sendTask, name+"-UdpServer-Send");
		readThread = new Thread(readTask, name+"-UdpServer-Read");
		FlashUtil.getLog().logTime(logName+": Initialized");
		
		readInterface.setMaxBufferSize(Communications.MAX_REC_LENGTH);
		readInterface.open();
	}
	public UdpServer(InetAddress addr, int port){
		this("", addr, port);
	}
	
	private Packet receivePacket(){
		if(!readInterface.read(packet))
			return null;
		return packet;
	}
	private void read(){
		Packet packet = receivePacket();
		if(packet == null || packet.length == 0 || packet.senderAddress == null)
			return;
		Client client = getClientByAddress(packet.senderAddress, packet.senderPort);
		if(client == null){
			client = new Client();
			client.address = packet.senderAddress;
			client.port = packet.senderPort;
			client.name = client.address.toString()+":"+client.port;
			client.connected = true;
		}
		if(!client.connected){
			client.resetAll();
			client.connected = true;
			client.readTimeouts = 0;
		}
		
		client.lastRead = FlashUtil.millis();
		/*if(Communications.isHandshake(packet.data, packet.length)){
			writeHandshake(client);
			return;
		}*/
		if(packet.length < 2) return;
		
		int id = FlashUtil.toInt(packet.data);
		Sendable sendable = client.getSendableByID(id);
		if(sendable == null && sendableCreator != null){
			String sendableName = new String(packet.data, 6, packet.length - 6);
			sendable = sendableCreator.create(sendableName, id, packet.data[5]);
			if(sendable != null)
				client.sendables.addElement(sendable);
		}else sendable.newData(Arrays.copyOfRange(packet.data, 0, packet.length));
	}
	
	private void writeHandshake(Client client){
		write(null/*Communications.HANDSHAKE*/, client);
	}
	private void write(byte[] data, InetAddress addr, int port){
		readInterface.write(data, addr, port);
	}
	private void write(byte[] data, Client client){
		write(data, client.address, client.port);
	}
	
	private void send(byte[] data, Sendable sendable, InetAddress addr, int port){
		byte[] bytes = new byte[data.length + 5];
		FlashUtil.fillByteArray(sendable.getID(), bytes);
		bytes[4] = sendable.getType();
		System.arraycopy(data, 0, bytes, 5, data.length);
		write(bytes, addr, port);
	}
	private void send(byte[] data, Sendable sendable, Client client){
		send(data, sendable, client.address, client.port);
	}
	
	private Client getClientByAddress(InetAddress addr, int port){
		for(Enumeration<Client> cEnum = clients.elements(); cEnum.hasMoreElements();){
			Client client = cEnum.nextElement();
			if(client.port == port && FlashUtil.equals(client.address.getAddress(), addr.getAddress()))
				return client;
		}
		return null;
	}
	
	public void start(){
		if(!sendThread.isAlive())
			sendThread.start();
		if(!readThread.isAlive())
			readThread.start();
	}
	public void stop(){
		if(sendThread.isAlive())
			sendTask.stop();
		if(readThread.isAlive())
			readTask.stop();
	}
	
	public void setSendableCreator(SendableCreator creator){
		sendableCreator = creator;
	}
	public void attach(InetAddress addr, int port, Sendable... sendables){
		for (Sendable sendable : sendables) 
			attach(addr, port, sendable);
	}
	public void attach(InetAddress addr, int port, Sendable sendable){
		Client client = getClientByAddress(addr, port);
		if(client == null) return;
		if(client.getSendableByID(sendable.getID()) == null){
			client.sendables.addElement(sendable);
			sendable.setAttached(true);
		}
	}
	public boolean detach(InetAddress addr, int port, Sendable sendable){
		Client client = getClientByAddress(addr, port);
		if(client == null) return false;
		if(client.sendables.remove(sendable))
			sendable.setAttached(false);
		return !sendable.attached();
	}
	public boolean detach(InetAddress addr, int port, int index){
		Client client = getClientByAddress(addr, port);
		if(client == null) return false;
		Sendable sen = client.sendables.get(index);
		if(sen != null) {
			client.sendables.remove(index);
			sen.setAttached(false);
		}
		return sen != null && !sen.attached();
	}
	public boolean detachByID(InetAddress addr, int port, int id){
		Client client = getClientByAddress(addr, port);
		if(client == null) return false;
		Sendable sen = client.getSendableByID(id);
		if(sen != null) {
			client.sendables.remove(sen);
			sen.setAttached(false);
		}
		return sen != null && !sen.attached();
	}
}
