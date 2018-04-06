package edu.flash3388.flashlib.communications;

import java.io.IOException;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * A wrapper {@link CommInterface} for tracking the usage of bandwidth by a CommInterface. 
 * It counts the bytes being received and sent in a time frame. {@link #getBandwidthUsage()} returns
 * the bandwidth usage tracked in Mbps.
 * 
 * <p>
 * When using the tracked communications interface for actual communications, it is required to instead use the tracker
 * as an interface for it to work. Data will be passed normally to the actual interface without interference.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class BandwidthTracker implements CommInterface{

	private CommInterface commInterface;
	private long infoStart = -1;
	private long bytesCount = 0;
	
	/**
	 * Creates a new BandwidthTracker wrapper for a {@link CommInterface}. 
	 * 
	 * @param comm interface to track bandwidth usage on
	 */
	public BandwidthTracker(CommInterface comm){
		this.commInterface = comm;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void open() throws IOException {
		commInterface.open();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		commInterface.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connect() throws IOException {
		commInterface.connect();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disconnect() throws IOException {
		commInterface.disconnect();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return commInterface.isConnected();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOpened() {
		return commInterface.isOpened();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] read() throws IOException {
		byte[] bytes = commInterface.read();
		
		if(infoStart == -1)
			infoStart = FlashUtil.millis();
		bytesCount += bytes.length;
		return bytes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadTimeout(int millis) throws IOException {
		commInterface.setReadTimeout(millis);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getReadTimeout() throws IOException {
		return commInterface.getReadTimeout();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaxBufferSize(int bytes) throws IOException {
		commInterface.setMaxBufferSize(bytes);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxBufferSize() throws IOException {
		return commInterface.getMaxBufferSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] data) throws IOException {
		if(infoStart == -1)
			infoStart = FlashUtil.millis();
		bytesCount += data.length;
		commInterface.write(data);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(byte[] data, int start, int length) throws IOException {
		if(infoStart == -1)
			infoStart = FlashUtil.millis();
		bytesCount += data.length;
		commInterface.write(data, start, length);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(int millis) throws IOException {
		commInterface.update(millis);
	}
	
	/**
	 * Gets the bandwidth usage of the wrapped {@link CommInterface} in Mpbs.
	 * 
	 * @return bandwidth usage in Mpbs
	 */
	public double getBandwidthUsage(){
		if(infoStart < 0)
			return 0.0;
		return ((bytesCount * 0.000008) / ((FlashUtil.millis() - infoStart) * 0.001));
	}
	/**
	 * Resets the bandwidth tracker data
	 */
	public void reset(){
		infoStart = -1;
		bytesCount = 0;
	}
}
