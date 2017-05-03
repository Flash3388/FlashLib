package edu.flash3388.flashlib.testing;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import edu.flash3388.flashlib.communications.CommInfo;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.ReadInterface;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.communications.SendableCreator;
import edu.flash3388.flashlib.communications.TCPReadInterface;
import edu.flash3388.flashlib.communications.UDPReadInterface;
import edu.flash3388.flashlib.util.FlashUtil;

public class MainComm {
	
	private static class ClientTask implements Runnable{

		@Override
		public void run() {
			try {
				client = new Communications(getInterface(false));
				client.setSendableCreator(new Creator());
				client.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private static final boolean TCP = true;
	
	static Communications server;
	static Communications client;
	static Scanner in = new Scanner(System.in);
	
	public static void main(String[] args) throws Exception{
		FlashUtil.setStart();
		
		InetAddress remote = InetAddress.getByName("roborio-3388-frc.local");
		InetAddress add = CommInfo.getInterfaceAddress(remote);
		System.err.println();
		
		/*server = new Communications(getInterface(true));
		server.attach(new Echo("echo1", true));
		server.start();
		
		Thread clientThread = new Thread(new ClientTask());
		clientThread.start();
		
		in.next();
		System.out.println("Done");
		client.close();
		server.close();
		if (clientThread.isAlive()) {
			clientThread.interrupt();
		}*/
	}
	
	private static ReadInterface getInterface(boolean server) throws IOException{
		if(server){
			if(TCP) return new TCPReadInterface(InetAddress.getLoopbackAddress(), 11000);
			return new UDPReadInterface(InetAddress.getLoopbackAddress(), 11000);
		}else{
			if(TCP) return new TCPReadInterface(InetAddress.getLoopbackAddress(), InetAddress.getLoopbackAddress(), 11001, 11000);
			return new UDPReadInterface(InetAddress.getLoopbackAddress(), 11001, 11000);
		}
	}
	
	public static class Echo extends Sendable{
		
		private byte[] origindata;
		private byte[][] byteData = new byte[2][0];
		private int byteindex = 0; boolean rec = false;
		
		public Echo(String name, boolean sender) {
			super(name, (byte)0x0);
			if(sender)
				origindata = name.getBytes();
		}
		public Echo(String name, int id, boolean sender){
			super(name, id, (byte)0x0);
			if(sender)
				origindata = name.getBytes();
		}

		@Override
		public void newData(byte[] data) {
			if(data.length != byteData[1 - byteindex].length)
				byteData[1 - byteindex] = new byte[data.length];
			System.arraycopy(data, 0, byteData[1 - byteindex], 0, 
					data.length);
			System.out.println(getName()+": "+new String(data, 0, 
					data.length));

			byteindex ^= 1;
			rec = true;
		}
		@Override
		public byte[] dataForTransmition() {
			byte[] data = new byte[byteData[byteindex].length];
			System.arraycopy(byteData[byteindex], 0, data, 0, data.length);
			rec = false;
			return data;
		}
		@Override
		public boolean hasChanged() {
			return rec;
		}
		@Override
		public void onConnection() {
			byteindex = 0;
			if(origindata == null){
				rec = false;
			}else{
				rec = true;
				if(byteData[byteindex].length != origindata.length)
					byteData[byteindex] = new byte[origindata.length];
				System.arraycopy(origindata, 0, byteData[byteindex], 0, origindata.length);
			}
		}
		@Override
		public void onConnectionLost() {}
	}
	private static class Creator implements SendableCreator{

		@Override
		public Sendable create(String name, int id, byte type) {
			if(type == 0) return new Echo("echo"+id, id, false);
			return null;
		}
	}
}
