package edu.flash3388.flashlib.communications;

import java.io.IOException;
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
 * It is necessary to implement {@link #readRaw(byte[], int, int)} for reading data from the port and {@link #writeRaw(byte[], int, int)} 
 * for writing data to the port used.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class StreamCommInterface extends ManualConnectionVerifier {

	private static final int PACKET_LENGTH_BYTE_COUNT = 4;
	
	private CRC32 crc;
	private byte[] dataBuffer = new byte[BUFFER_SIZE];
	private byte[] sizeBuffer = new byte[PACKET_LENGTH_BYTE_COUNT];
	private int nextPacketLength = -1;
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
	
	private boolean readError(int lengthRead) throws IOException {
		if(lengthRead < 0){
			disconnect();
		}
		return lengthRead < 1;
	}
	
	private boolean checkCrc(byte[] data, long expected){
		crc.reset();
		crc.update(data);
		
		return expected == crc.getValue();
	}
	private byte[] disassemblePacket(byte[] data, int start, int datalen){
		byte[] datarec = Arrays.copyOfRange(data, start, start + datalen);
		
		if(crclen > 0){
			if((data.length - start - datalen - 1) < 8 || 
					!checkCrc(datarec, FlashUtil.toLong(data, start + datalen + 1))){
				return null;
			}
		}
		
		return datarec;
	}
	private byte[] assemblePacket(byte[] data, int start, int len){
		byte[] sdata = new byte[len + PACKET_LENGTH_BYTE_COUNT + crclen - start];
		FlashUtil.fillByteArray(len, 0, sdata);
		System.arraycopy(data, start, sdata, PACKET_LENGTH_BYTE_COUNT, len);
		
		if(crclen > 0){
			crc.reset();
			crc.update(data);
			long crcd = crc.getValue();
			FlashUtil.fillByteArray(crcd, PACKET_LENGTH_BYTE_COUNT + len, sdata);
		}
		
		return sdata;
	}
	private byte[] handleData(){
		byte[] retData = disassemblePacket(dataBuffer, 0, nextPacketLength - crclen);
		
		if(retData != null && isHandshake(retData, retData.length))
			return null;
		
		return retData;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] read() throws IOException {
		if(!isOpened())
			return null;
		
		int availabe = availableData();
		if(nextPacketLength < 0 && availabe < PACKET_LENGTH_BYTE_COUNT)
			return null;
		
		if(nextPacketLength < 0 && availabe >= PACKET_LENGTH_BYTE_COUNT){
			int len = readRaw(sizeBuffer, 0, PACKET_LENGTH_BYTE_COUNT);
			if(readError(len)){
				return null;
			}
			nextPacketLength = FlashUtil.toInt(sizeBuffer);
			newDataRead();
		}
		
		if(nextPacketLength > 0 && availabe >= nextPacketLength){
			int len = readRaw(dataBuffer, 0, nextPacketLength);
			if(readError(len)){
				return null;
			}
			newDataRead();
			
			byte[] data = handleData();
			if(data != null){
				nextPacketLength = -1;
				return data;
			}
		}
		
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] data, int start, int length) throws IOException {
		data = assemblePacket(data, start, length);
		writeRaw(data, 0, data.length);
		newDataSent();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaxBufferSize(int bytes) throws IOException {
		dataBuffer = new byte[bytes];
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxBufferSize() throws IOException {
		return dataBuffer.length;
	}

	/**
	 * Resets the data buffers used by this interface for data reading.
	 */
	protected void resetBuffers(){
		nextPacketLength = -1;
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
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	protected abstract void writeRaw(byte[] data, int start, int length) throws IOException;
	/**
	 * Reads raw data from the IO port used for communications. Unlike {@link #read()}, this method does not 
	 * handle packet and data logic.
	 * 
	 * @param buffer data buffer to store read data into
	 * @param start start index in the buffer to save data from.
	 * @param length amount of bytes to read.
	 * 
	 * @return the amount of bytes read
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	protected abstract int readRaw(byte[] buffer, int start, int length) throws IOException;
	/**
	 * Gets the amount of bytes available to be read from the port.
	 * 
	 * @return bytes ready to be read.
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	protected abstract int availableData() throws IOException;
}
