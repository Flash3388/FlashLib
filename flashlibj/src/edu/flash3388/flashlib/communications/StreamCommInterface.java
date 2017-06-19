package edu.flash3388.flashlib.communications;

import java.util.Arrays;
import java.util.zip.CRC32;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * An abstract logic for stream-based {@link CommInterface}s, such as: TCP/IP. Provides simple data corruption detection
 * using {@link CRC32} and separation of data to packets.
 * <p>
 * Data to be sent is wrapped inside a packet which contains: the size of the data, the data itself and if defined, a CRC32
 * checksum. When reading data, the size of the data is checked first, than the data is extracted by its size. If CRC32 is 
 * allowed, than the checksum data is read as well and compared to the checksum of the data now. If they do not match, the data
 * is ignored.
 * </p>
 * <p>
 * When data is read, it is checked for a packet, if the entire packet was read, than it is analyzed. If the the packet
 * does not exist in its entirety, the data is saved to a secondary buffer which is updated next time data is read.
 * </p>
 * <p>
 * It is necessary to implement {@link #readData(byte[])} for reading data from the port and {@link #writeData(byte[])} 
 * for writing data to the port used.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class StreamCommInterface implements CommInterface{

	private CRC32 crc;
	private boolean server;
	private byte[] dataBuffer = new byte[BUFFER_SIZE], leftoverData = new byte[0];
	private byte crclen = 0;
	
	/**
	 * Creates a new stream-based {@link CommInterface}. For connection management it is necessary to define if
	 * this communication side must initiate communications or not. This is defined by whether or not it is a server:
	 * <ul>
	 * 	<li> Server: waits for connection </li>
	 * 	<li> Client: initiates connection </li>
	 * </ul>
	 * <p>
	 * For data corruption detection it is possible to use a CRC-32 checksum algorithm with this interface.
	 * </p>
	 * 
	 * @param server true - server, false - client
	 * @param crcallow if true, CRC-32 checksum is used
	 */
	public StreamCommInterface(boolean server, boolean crcallow){
		this.server = server;
		if(crcallow){
			crc = new CRC32();
			crclen = 8;
		}
	}
	/**
	 * Creates a new stream-based {@link CommInterface}. For connection management it is necessary to define if
	 * this communication side must initiate communications or not. This is defined by whether or not it is a server:
	 * <ul>
	 * 	<li> Server: waits for connection </li>
	 * 	<li> Client: initiates connection </li>
	 * </ul>
	 * <p>
	 * This constructor uses by default a CRC-32 checksum algorithm for data corruption. See {@link #StreamCommInterface(boolean, boolean)}
	 * for other options.
	 * </p>
	 * 
	 * @param server true - server, false - client
	 */
	public StreamCommInterface(boolean server){
		this(server, true);
	}
	
	private boolean checkCrc(byte[] data, long expected){
		crc.reset();
		crc.update(data);
		
		return expected == crc.getValue();
	}
	private boolean disassemblePacket(byte[] data, int start, int datalen, Packet packet){
		byte[] datarec = Arrays.copyOfRange(data, start, start + datalen);
		
		if(crclen > 0){
			if((data.length - start - datalen - 1) < 8 || 
					!checkCrc(datarec, FlashUtil.toLong(data, start + datalen + 1))){
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
		if(leftoverData.length < 4 + datalen)
			return false;
		
		boolean ret = disassemblePacket(leftoverData, 4, datalen - crclen, packet);
		leftoverData = Arrays.copyOfRange(leftoverData, 4 + datalen, leftoverData.length);
		
		return ret;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean read(Packet packet) {
		packet.length = -1;
		
		if(!isOpened())
			return false;
		
		if(handleData(packet))
			return true;
		
		int len = readData(dataBuffer);
		if(len < 1){
			packet.length = 0;
			return false;
		}
		
		leftoverData = Arrays.copyOf(leftoverData, leftoverData.length + len);
		System.arraycopy(dataBuffer, 0, leftoverData, leftoverData.length - len, len);
		
		if(handleData(packet))
			return true;
		
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] data) {
		data = assemblePacket(data);
		writeData(data);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] data, int start, int length) {
		write(Arrays.copyOfRange(data, start, start + length));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaxBufferSize(int bytes) {
		dataBuffer = new byte[bytes];
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxBufferSize() {
		return dataBuffer.length;
	}
	
	/**
	 * Gets whether or not this interface acts like as a server:
	 * <ul>
	 * 	<li> Server: waits for connection </li>
	 * 	<li> Client: initiates connection </li>
	 * </ul>
	 * 
	 * @return true - server, false - client
	 */
	protected boolean isBoundAsServer(){
		return server;
	}
	/**
	 * Resets the data buffers used by this interface for data reading.
	 */
	protected void resetBuffers(){
		leftoverData = new byte[0];
		for (int i = 0; i < dataBuffer.length; i++)
			dataBuffer[i] = 0;
	}
	
	/**
	 * Writes raw data to the IO port used for communications. Unlike {@link #write(byte[])}, this method does not 
	 * handle packet and data logic.
	 * 
	 * @param data an array of bytes to send
	 */
	protected abstract void writeData(byte[] data);
	/**
	 * Reads raw data from the IO port used for communications. Unlike {@link #readData(byte[])}, this method does not 
	 * handle packet and data logic.
	 * 
	 * @param buffer data buffer to store read data into
	 * @return the amount of bytes read
	 */
	protected abstract int readData(byte[] buffer);
}
