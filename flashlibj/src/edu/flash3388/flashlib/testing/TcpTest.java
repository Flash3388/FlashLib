package edu.flash3388.flashlib.testing;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Map;
import java.util.Scanner;

import edu.flash3388.flashlib.communications.BandwidthTracker;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.communications.SendableCreator;
import edu.flash3388.flashlib.communications.TcpCommInterface;
import edu.flash3388.flashlib.util.CodeTester.CodeTest;
import edu.flash3388.flashlib.util.FlashUtil;

public class TcpTest implements CodeTest{
	
	private static class Echo extends Sendable{

		private boolean origin;
		private String toSend = null;
		private boolean send = false;
		
		public Echo(String name, byte type) {
			super(name, type);
			toSend = name;
			origin = true;
		}
		public Echo(String name, int id, byte type) {
			super(name, type);
			origin = false;
		}

		@Override
		public void newData(byte[] data) {
			send = true;
			if(!origin)
				toSend = new String(data);
		}
		@Override
		public byte[] dataForTransmition() {
			send = false;
			return toSend.getBytes();
		}
		@Override
		public boolean hasChanged() {
			return send;
		}

		@Override
		public void onConnection() {
			send = origin;
		}
		@Override
		public void onConnectionLost() {}
	}
	
	
	private Communications server, client;
	
	@Override
	public String getName() {
		return "tcptest";
	}
	@Override
	public void run(String[] args, Scanner in, PrintStream out) {
		//FlashUtil.getLog().setLoggingMode(Log.MODE_WRITE);
		
		Map<String, String> map = FlashUtil.parseValueParameters(args);
		int echos = FlashUtil.toInt(map.get("echo"));
		long ms = FlashUtil.toLong(map.get("ms"));
		long wait = FlashUtil.toLong(map.get("wait"));
		
		if(echos <= 0){
			out.println("Echos: Default - 10");
			echos = 10;
		}else out.println("Echos: "+echos);
		if(ms <= 0){
			out.println("Time: Default - 2000ms");
			ms = 2000;
		}else out.println("Time: "+ms+"ms");
		if(wait <= 0){
			out.println("Wait: Default - 2000ms");
			wait = 2000;
		}else out.println("Wait: "+wait+"ms");
		
		try {
			InetAddress local = FlashUtil.getLocalAddress(InetAddress.getByName("Flash3388"));//InetAddress.getLoopbackAddress();
			TcpCommInterface commI;// = new TcpCommInterface(local, local, 1000, 1001);
			/*client = new Communications("Client", new BandwidthTracker(commI));
			client.setSendableCreator(new SendableCreator(){
				@Override
				public Sendable create(String name, int id, byte type) {
					return new Echo(name, id, type);
				}
			});*/
			
			commI = new TcpCommInterface(local, 1001);
			server = new Communications("Server", new BandwidthTracker(commI));
		} catch (IOException e) {
		}
		
		if(server == null){
			out.println("Error executing test");
			return;
		}
		
		out.println("Starting communications");
		server.start();
		//client.start();
		
		out.println("Awaiting connection");
		long tTime = FlashUtil.millis();
		while(!server.isConnected()){
			FlashUtil.delay(10);
			if(FlashUtil.millis() - tTime > 5000){
				out.println("Unable to create connection");
				close();
				return;
			}
		}
		
		long delayms = ms / echos;
		tTime = FlashUtil.millis();
		int i = 1;
		for (; i <= echos; i++) {
			if(!server.isConnected()){
				out.println("Server lost connection");
				break;
			}
			
			Echo sendable = new Echo("echo"+i, (byte)0);
			server.attach(sendable);
			out.println("Attached echo "+ i);
			FlashUtil.delay(delayms);
		}

		if(i > echos){
			out.println("Waiting for: "+wait+"ms");
			FlashUtil.delay(wait + (ms - tTime));
			tTime = FlashUtil.millis() - tTime;
			out.println("Success - Echos: "+echos+" Time: "+(tTime)+"ms");
		}else{
			tTime = FlashUtil.millis() - tTime;
			out.println("Failed - Echos: "+(i-1)+"/"+echos+" Time: "+tTime+" ms");
		}
		
		long bandwidth = ((BandwidthTracker)server.getCommInterface()).getBandwidthUsage();
		out.println("Bandwidth: "+bandwidth+" mbps");
		
		close();
	}
	private void close(){
		server.close();
		//client.close();
		FlashUtil.delay(100);
	}
}
