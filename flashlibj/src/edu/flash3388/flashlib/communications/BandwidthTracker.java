package edu.flash3388.flashlib.communications;

import edu.flash3388.flashlib.util.FlashUtil;

public class BandwidthTracker implements CommInterface{

	private CommInterface commInterface;
	private long infoStart = -1;
	private long bytes = 0;
	
	public BandwidthTracker(CommInterface comm){
		this.commInterface = comm;
	}
	
	@Override
	public void open() {
		commInterface.open();
	}
	@Override
	public void close() {
		commInterface.close();
	}

	@Override
	public void connect(Packet packet) {
		commInterface.connect(packet);
	}
	@Override
	public void disconnect() {
		commInterface.disconnect();
	}

	@Override
	public boolean isConnected() {
		return commInterface.isConnected();
	}
	@Override
	public boolean isOpened() {
		return commInterface.isOpened();
	}

	@Override
	public boolean read(Packet packet) {
		boolean b = commInterface.read(packet);
		
		if(infoStart == -1)
			infoStart = FlashUtil.millis();
		bytes += packet.length;
		return b;
	}

	@Override
	public void setReadTimeout(long millis) {
		commInterface.setReadTimeout(millis);
	}
	@Override
	public long getTimeout() {
		return commInterface.getTimeout();
	}
	@Override
	public void setMaxBufferSize(int bytes) {
		commInterface.setMaxBufferSize(bytes);
	}
	@Override
	public int getMaxBufferSize() {
		return commInterface.getMaxBufferSize();
	}

	@Override
	public void write(byte[] data) {
		if(infoStart == -1)
			infoStart = FlashUtil.millis();
		bytes += data.length;
		commInterface.write(data);
	}
	@Override
	public void write(byte[] data, int start, int length) {
		if(infoStart == -1)
			infoStart = FlashUtil.millis();
		bytes += data.length;
		commInterface.write(data, start, length);
	}
	
	@Override
	public void update(long millis) {
		commInterface.update(millis);
	}
	
	public long getBandwidthUsage(){
		if(infoStart < 0)
			return 0;
		return (long) (bytes * 0.000008 / ((FlashUtil.millis() - infoStart) * 0.001));
	}
	public void reset(){
		infoStart = -1;
		bytes = 0;
	}
}
