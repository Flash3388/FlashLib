package edu.flash3388.flashlib.testing;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import edu.flash3388.flashlib.communications.CommInfo;
import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.communications.SendableCreator;
import edu.flash3388.flashlib.communications.TcpCommInterface;
import edu.flash3388.flashlib.communications.UdpCommInterface;
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
	static int preEchoNum = 1;
	
	public static void main(String[] args) throws Exception{
		FlashUtil.setStart();
		server = new Communications(getInterface(true));
		server.start();
		
		Thread clientThread = new Thread(new ClientTask());
		clientThread.start();
		
		String line = in.nextLine();
		while(!line.equalsIgnoreCase("done")){
			parseInstruction(line);
			line = in.nextLine();
		}
		
		System.out.println("Done");
		client.close();
		server.close();
		if (clientThread.isAlive()) {
			clientThread.interrupt();
		}
	}
	
	private static void parseInstruction(String str){
		String[] params = str.split(" ");
		if(params.length < 1){
			System.out.println("Error: Empty Instruction");
			return;
		}
		
		if(params[0].equals("echo")){
			if(params.length != 2){
				System.out.println("Error: Currect syntax: echo [num]");
				return;
			}
			int num = -1;
			try{
				num = Integer.parseInt(params[1]);
			}catch(NumberFormatException e){
				System.out.println("Error: parameter is not a valid integer");
				return;
			}
			if(num < 0){
				System.out.println("Error: parameter must be non negative");
				return;
			}
			
			addEchos(num);
			System.out.println("Added "+num+" Echos");
		}
	}
	private static void addEchos(int echos){
		for(int i = 0; i < echos; i++)
			server.attach(new Echo("echo"+(i+preEchoNum)));
		preEchoNum += echos;
	}
	private static CommInterface getInterface(boolean server) throws IOException{
		if(server){
			if(TCP) return new TcpCommInterface(InetAddress.getLoopbackAddress(), 11000);
			return new UdpCommInterface(InetAddress.getLoopbackAddress(), 11000);
		}else{
			if(TCP) return new TcpCommInterface(InetAddress.getLoopbackAddress(), InetAddress.getLoopbackAddress(), 11001, 11000);
			return new UdpCommInterface(InetAddress.getLoopbackAddress(), 11001, 11000);
		}
	}
	
	public static class Echo extends Sendable{
		
		private byte[] origindata;
		private byte[] byteData = new byte[0];
		private boolean rec = false;
		
		public Echo(String name) {
			super(name, (byte)0x0);
			origindata = name.getBytes();
		}
		public Echo(String name, int id){
			super(name, id, (byte)0x0);
		}

		@Override
		public void newData(byte[] data) {
			if(data.length != byteData.length)
				byteData = new byte[data.length];
			System.arraycopy(data, 0, byteData, 0, 
					data.length);
			String dstr = new String(data, 0, data.length);
			System.out.println(getName()+": "+dstr);

			rec = true;
		}
		@Override
		public byte[] dataForTransmition() {
			byte[] data = new byte[byteData.length];
			System.arraycopy(byteData, 0, data, 0, data.length);
			rec = false;
			return data;
		}
		@Override
		public boolean hasChanged() {
			return rec;
		}
		@Override
		public void onConnection() {
			if(origindata == null){
				rec = false;
			}else{
				rec = true;
				if(byteData.length != origindata.length)
					byteData = new byte[origindata.length];
				System.arraycopy(origindata, 0, byteData, 0, origindata.length);
			}
		}
		@Override
		public void onConnectionLost() {}
	}
	private static class Creator implements SendableCreator{

		@Override
		public Sendable create(String name, int id, byte type) {
			if(type == 0) return new Echo(name+"-c", id);
			return null;
		}
	}
}
