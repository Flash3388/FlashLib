package edu.flash3388.flashlib.communications;

import java.util.Arrays;
import java.util.zip.CRC32;

import edu.flash3388.flashlib.util.FlashUtil;

public abstract class BusCommInterface implements ManualCommInterface, StreamCommInterface{

	private CRC32 crc;
	private boolean server, isConnected = false;
	private long lastRead = -1, timeLastTimeout = -1;
	private int connectionTimeout = CONNECTION_TIMEOUT, timeouts = 0, maxTimeouts = 3;
	private byte[] dataBuffer = new byte[BUFFER_SIZE], leftoverData = new byte[0];
	
	public BusCommInterface(boolean server){
		this.server = server;
		crc = new CRC32();
	}
	
	private void writeHandshake(){
		write(HANDSHAKE);
	}
	private boolean disassemblePacket(byte[] data, int start, int end, Packet packet){
		int datalen = FlashUtil.toInt(data, start + 1);
		byte[] datarec = Arrays.copyOfRange(data, start + 5, start + 5 + datalen);
		long crcrec = FlashUtil.toLong(data, start + 5 + datalen + 1);
		
		crc.reset();
		crc.update(datarec);
		long crcd = crc.getValue();
		
		if(crcrec != crcd){
			System.out.println("data corruption?");
			packet.length = 0;
			return false;
		}
		packet.length = datalen;
		packet.data = datarec;
		return true;
	}
	private byte[] assemblePacket(byte[] data){
		crc.reset();
		crc.update(data);
		long crcd = crc.getValue();
		
		byte[] sdata = new byte[data.length + 14];
		sdata[0] = SEPERATOR_START;
		sdata[sdata.length-1] = SEPERATOR_END;
		
		FlashUtil.fillByteArray(data.length, 1, sdata);
		System.arraycopy(data, 0, sdata, 5, data.length);
		FlashUtil.fillByteArray(crcd, 5 + data.length, sdata);
		
		return sdata;
	}
	private boolean handleData(Packet packet){
		if(leftoverData.length > 0){
			int start = FlashUtil.indexOf(leftoverData, 0, leftoverData.length-1, SEPERATOR_START);
			int end = FlashUtil.indexOf(leftoverData, 0, leftoverData.length-1, SEPERATOR_END);
			
			while(start >= 0 && end > start && end - start < 15){
				leftoverData = Arrays.copyOfRange(leftoverData, end+1, leftoverData.length-1);
				
				start = FlashUtil.indexOf(leftoverData, 0, leftoverData.length-1, SEPERATOR_START);
				end = FlashUtil.indexOf(leftoverData, 0, leftoverData.length-1, SEPERATOR_END);
			}
			
			if(start >= 0 && end > start){
				boolean ok = disassemblePacket(leftoverData, start, end, packet);
				
				if(end+1 > leftoverData.length - 1)
					leftoverData = new byte[0];
				else 
					leftoverData = Arrays.copyOfRange(leftoverData, end+1, leftoverData.length - 1);
				if(ok){ 
					if(Communications.isHandshake(packet.data, packet.length))
						packet.length = 0;
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void connect(Packet packet) {
		timeouts = 0;
		isConnected = server? Communications.handshakeServer(this, packet) : 
			Communications.handshakeClient(this, packet);
	}
	@Override
	public void disconnect() {
		isConnected = false;
	}
	@Override
	public boolean isConnected() {
		return isConnected;
	}

	@Override
	public boolean read(Packet packet) {
		
		if(handleData(packet)){
			lastRead = FlashUtil.millis();
			return true;
		}
		
		int len = readPort(dataBuffer);
		if(len < 1){
			packet.length = 0;
			return false;
		}
		lastRead = FlashUtil.millis();
		
		leftoverData = Arrays.copyOf(leftoverData, leftoverData.length + len);
		System.arraycopy(dataBuffer, 0, leftoverData, leftoverData.length - len, len);
		
		if(handleData(packet))
			return true;
		
		return false;
	}
	
	@Override
	public void write(byte[] data) {
		data = assemblePacket(data);
		writePort(data);
	}
	@Override
	public void write(byte[] data, int start, int length) {
		write(Arrays.copyOfRange(data, start, start + length));
	}

	@Override
	public void update(long millis) {
		if(millis - lastRead >= connectionTimeout){
			timeouts++;
			lastRead = millis;
			timeLastTimeout = millis;
			System.out.println("Timeout");
		}
		if(timeouts >= maxTimeouts){
			System.out.println("Max timeouts");
			isConnected = false;
		}
		if(timeouts > 0 && timeLastTimeout != -1 && 
				millis - timeLastTimeout > (connectionTimeout*3)){
			timeouts = 0;
			timeLastTimeout = -1;
			System.out.println("Timeout reset");
		}
		writeHandshake();
	}

	@Override
	public void setMaxBufferSize(int bytes) {
		dataBuffer = new byte[bytes];
	}
	@Override
	public int getMaxBufferSize() {
		return dataBuffer.length;
	}
	
	public void setMaxTimeoutsCount(int timeouts){
		maxTimeouts = timeouts;
	}
	public int getMaxTimeoutsCount(){
		return maxTimeouts;
	}
	public void setConnectionTimeout(int timeout){
		connectionTimeout = timeout;
	}
	public int getConnectionTimeout(){
		return connectionTimeout;
	}
	
	protected abstract void writePort(byte[] data);
	protected abstract int readPort(byte[] buffer);
}
