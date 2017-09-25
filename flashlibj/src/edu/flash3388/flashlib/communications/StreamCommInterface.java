package edu.flash3388.flashlib.communications;

import java.util.Arrays;
import java.util.zip.CRC32;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * An abstract logic for stream-based {@link CommInterface}s, such as: TCP/IP. Provides simple data corruption detection
 * using {@link CRC32} and separation of data to packets. To insure connection, this comm interface extends the abstract
 * {@link ManualConnectionVerifier}.
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
 * It is necessary to implement {@link #readData(byte[])} for reading data from the port and {@link #writeData(byte[], int, int)} 
 * for writing data to the port used.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class StreamCommInterface extends ManualConnectionVerifier{

	private CRC32 crc;
	private byte[] dataBuffer = new byte[BUFFER_SIZE], leftoverData = new byte[0];
	private byte crclen = 0;
	
	/**
	 * Creates a new stream-based {@link CommInterface}. 
	 * corruption detection it is possible to use a CRC-32 checksum algorithm with this interface.
	 * 
	 * @param crcallow if true, CRC-32 checksum is used
	 */
	public StreamCommInterface(boolean crcallow){
		if(crcallow){
			crc = new CRC32();
			crclen = 8;
		}
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
	private byte[] assemblePacket(byte[] data, int start, int len){
		byte[] sdata = new byte[len + 4 + crclen];
		FlashUtil.fillByteArray(len, 0, sdata);
		System.arraycopy(data, start, sdata, 4, len);
		
		if(crclen > 0){
			crc.reset();
			crc.update(data);
			long crcd = crc.getValue();
			FlashUtil.fillByteArray(crcd, 4 + len, sdata);
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
		
		if(ret && isHandshake(packet.data, packet.length))
			return false;
		
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
			if(len < 0)
				disconnect();
			packet.length = 0;
			return false;
		}
		newDataRead();
		
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
		write(data, 0, data.length);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] data, int start, int length) {
		data = assemblePacket(data, start, length);
		writeData(data, 0, data.length);
		newDataSent();
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
	 * @param start the start index to send data from
	 * @param length the amount of bytes to send
	 */
	protected abstract void writeData(byte[] data, int start, int length);
	/**
	 * Reads raw data from the IO port used for communications. Unlike {@link #readData(byte[])}, this method does not 
	 * handle packet and data logic.
	 * 
	 * @param buffer data buffer to store read data into
	 * @return the amount of bytes read
	 */
	protected abstract int readData(byte[] buffer);
}
