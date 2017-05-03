package edu.flash3388.flashlib.robot.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.robot.devices.BooleanDataSource;

public class BooleanProperty extends Sendable{
	
	private boolean lastValue = false, value = false, changed = false;
	private BooleanDataSource src;
	private byte[] bytes = new byte[1];
	
	public BooleanProperty(String name, BooleanDataSource data) {
		super(name, FlashboardSendableType.BOOLEAN);
		src = data;
	}
	public BooleanProperty(String name){
		this(name, new BooleanDataSource.VarDataSource());
	}

	public void set(BooleanDataSource src){
		lastValue = src.get();
		this.src = src;
	}
	public BooleanDataSource get(){
		return src;
	}
	
	@Override
	public void newData(byte[] data) {}
	@Override
	public byte[] dataForTransmition() {
		lastValue = value;
		bytes[0] = (byte) (lastValue? 1 : 0);
		changed = false;
		return bytes;
	}
	@Override
	public boolean hasChanged() {
		return src != null && (lastValue != (value = src.get()) || changed);
	}
	@Override
	public void onConnection() {
		changed = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
