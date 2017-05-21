package edu.flash3388.flashlib.communications;

import java.util.Arrays;
import java.util.zip.CRC32;

import edu.flash3388.flashlib.util.FlashUtil;

public abstract class StreamCommInterface implements CommInterface{

	private CRC32 crc;
	private boolean server;
	private byte[] dataBuffer = new byte[BUFFER_SIZE], leftoverData = new byte[0];
	private byte crclen = 0;
	
	public StreamCommInterface(boolean server, boolean crcallow){
		this.server = server;
		if(crcallow){
			crc = new CRC32();
			crclen = 8;
		}
	}
	public StreamCommInterface(boolean server){
		this(server, true);
	}
	
	private boolean disassemblePacket(byte[] data, int start, int datalen, Packet packet){
		byte[] datarec = Arrays.copyOfRange(data, start, start + datalen);
		
		if(crclen > 0){
			long crcrec = FlashUtil.toLong(data, start + datalen + 1);
			crc.reset();
			crc.update(datarec);
			long crcd = crc.getValue();
			
			if(crcrec != crcd){
				System.out.println("data corruption?");
				packet.length = 0;
				return false;
			}
		}
		
		packet.length = datalen;
		packet.data = datarec;
		return true;
	}
	private byte[] assemblePacket(byte[] data){
		byte[] sdata = new byte[data.length + 4 + crclen];
		FlashUtil.fillByteArray(data.length, 0, sdata);
		System.arraycopy(data, 0, sdata, 4, data.length);
		
		if(crclen > 0){
			crc.reset();
			crc.update(data);
			long crcd = crc.getValue();
			FlashUtil.fillByteArray(crcd, 4 + data.length, sdata);
		}
		
		return sdata;
	}
	private boolean handleData(Packet packet){
		if(leftoverData.length < 4)
			return false;
		int datalen = FlashUtil.toInt(leftoverData);
		if(leftoverData.length < datalen + crclen)
			return false;
		
		boolean ret = disassemblePacket(leftoverData, 4, datalen, packet);
		if(4 + datalen + crclen > leftoverData.length-1)
			leftoverData = new byte[0];
		else
			leftoverData = Arrays.copyOfRange(leftoverData, 4 + datalen + crclen, leftoverData.length-1);
		
		return ret;
	}
	
	@Override
	public boolean read(Packet packet) {
		packet.length = -1;
		
		if(!isOpened())
			return false;
		
		if(isBoundAsServer()){
			packet.length = -1;
		}else{
			packet.length = -1;
		}
		
		if(handleData(packet))
			return true;
		
		int len = readData(dataBuffer);
		if(len < 1){
			packet.length = 0;
			return false;
		}
		
		if(!isBoundAsServer()) FlashUtil.getLog().log("Rec: "+len); 
		
		leftoverData = Arrays.copyOf(leftoverData, leftoverData.length + len);
		System.arraycopy(dataBuffer, 0, leftoverData, leftoverData.length - len, len);
		
		if(handleData(packet))
			return true;
		
		return false;
	}
	
	@Override
	public void write(byte[] data) {
		data = assemblePacket(data);
		writeData(data);
	}
	@Override
	public void write(byte[] data, int start, int length) {
		write(Arrays.copyOfRange(data, start, start + length));
	}
	
	@Override
	public void setMaxBufferSize(int bytes) {
		dataBuffer = new byte[bytes];
	}
	@Override
	public int getMaxBufferSize() {
		return dataBuffer.length;
	}
	
	protected boolean isBoundAsServer(){
		return server;
	}
	protected void resetBuffers(){
		leftoverData = new byte[0];
		for (int i = 0; i < dataBuffer.length; i++)
			dataBuffer[i] = 0;
	}
	
	protected abstract void writeData(byte[] data);
	protected abstract int readData(byte[] buffer);
}
